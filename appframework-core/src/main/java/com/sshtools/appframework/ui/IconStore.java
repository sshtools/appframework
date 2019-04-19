/**
 * Maverick Application Framework - Application framework
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.sshtools.appframework.ui;

import java.awt.Image;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.vfs2.FileSystemException;
import org.freedesktop.icons.DefaultIconService;
import org.freedesktop.icons.IconService;
import org.freedesktop.icons.LinuxIconService;
import org.freedesktop.mime.AliasService;
import org.freedesktop.mime.DefaultAliasService;
import org.freedesktop.mime.DefaultGlobService;
import org.freedesktop.mime.DefaultMIMEService;
import org.freedesktop.mime.DefaultMagicService;
import org.freedesktop.mime.GlobService;
import org.freedesktop.mime.LinuxAliasService;
import org.freedesktop.mime.LinuxGlobService;
import org.freedesktop.mime.LinuxMIMEService;
import org.freedesktop.mime.LinuxMagicService;
import org.freedesktop.mime.MIMEEntry;
import org.freedesktop.mime.MIMEService;
import org.freedesktop.swing.SVGIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sshtools.appframework.util.IOUtil;

public class IconStore {
	final static Logger LOG = LoggerFactory.getLogger(IconStore.class);
	private static Properties fixes = new Properties();
	private static IconStore iconStore;
	static {
		try {
			InputStream in = IconStore.class.getResourceAsStream("/icon-name-map.properties");
			if (in != null) {
				try {
					fixes.load(in);
				} finally {
					in.close();
				}
			}
		} catch (Exception e) {
		}
	}

	public static IconStore getInstance() {
		if (iconStore == null) {
			try {
				iconStore = new IconStore();
			} catch (Exception e) {
				throw new Error(e);
			}
		}
		return iconStore;
	}

	private AliasService aliasService;
	private Map<String, Icon> cache = new HashMap<String, Icon>();
	private GlobService globService;
	private DefaultIconService iconService;
	private DefaultMagicService magicService;
	private LimitedCache<Path, MIMEEntry> mimeCache = new LimitedCache<Path, MIMEEntry>();
	private LimitedCache<String, MIMEEntry> mimePatternCache = new LimitedCache<String, MIMEEntry>();
	private MIMEService mimeService;

	private IconStore() throws IOException, ParseException {
		aliasService = new DefaultAliasService();
		globService = new DefaultGlobService();
		magicService = new DefaultMagicService();
		mimeService = new DefaultMIMEService(globService, aliasService, magicService);
		if (SystemUtils.IS_OS_LINUX) {
			if (System.getProperty("appframework.disableDefaultIconThemes", "false").equals("false")) {
				try {
					iconService = new LinuxIconService();
				} catch (Exception e) {
					LOG.error("Failed to load icon theme.", e);
				}
			}
			try {
				globService = new LinuxGlobService();
			} catch (Exception e) {
				LOG.error("Failed to globs.", e);
			}
			try {
				magicService = new LinuxMagicService();
			} catch (Exception e) {
				LOG.error("Failed to magic.", e);
			}
			try {
				aliasService = new LinuxAliasService();
			} catch (Exception e) {
				LOG.error("Failed to aliases.", e);
			}
			try {
				mimeService = new LinuxMIMEService(globService, aliasService, magicService);
			} catch (Exception e) {
				LOG.error("Failed to MIME.", e);
			}
		}
		if (iconService == null) {
			iconService = new DefaultIconService();
		}
		iconService.setReturnMissingImage(false);
		// Add the default fallback icon
		addThemeJar("default-tango-theme");
		setDefaultThemeName("default-tango-theme");
	}

	public void addThemeJar(String themeName) throws IOException {
		URL loc = getClass().getClassLoader().getResource(themeName + "/index.theme");
		Path obj = null;
		if (loc != null) {
			try {
				LOG.info(String.format("Adding theme resource %s", loc));
				obj = IOUtil.resourceToPath(loc);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		if (obj != null) {
			URI uri = obj.toUri();
			if (uri.getScheme().equals("jar")) {
				for (Path r : obj.getFileSystem().getRootDirectories()) {
					LOG.info(String.format("Adding theme base %s", r));
					iconService.addBase(r);
				}
			} else if (uri.getScheme().equals("file")) {
				LOG.info(String.format("Adding theme base %s", obj.getParent().getParent()));
				iconService.addBase(obj.getParent().getParent());
			}
		}
	}

	public void configure(SshToolsApplication application) throws IOException, ParseException {
		// Initialise icon service
		iconService.postInit();
	}

	public Icon getIcon(String name, int size) {
		if (iconService == null) {
			throw new IllegalStateException("configure() not yet called.");
		}
		String cacheKey = name + "/" + size;
		if (cache.containsKey(cacheKey)) {
			return cache.get(cacheKey);
		}
		Icon icon = null;
		try {
			Path file = iconService.findIcon(name, 48);
			if (file != null) {
				icon = get(name, size, cacheKey, file);
			} else {
				if (fixes.containsKey(name)) {
					file = iconService.findIcon(fixes.getProperty(name), 48);
					if (file != null) {
						icon = get(name, size, cacheKey, file);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Failed to load icon " + name + " at size " + size + ".", e);
		}
		cache.put(cacheKey, icon);
		return icon;
	}

	public Icon getIconForFile(Path file) {
		return getIconForFile(file, 48);
	}

	public Icon getIconForFile(Path file, int size) {
		return getIconForFile(file, size, true);
	}

	public Icon getIconForFile(Path file, int size, boolean useMagic) {
		try {
			if (Files.isRegularFile(file)) {
				MIMEEntry mime = mimeCache.get(file);
				if (mime == null) {
					mime = mimeService.getMimeTypeForFile(file, useMagic);
				}
				if (mime != null) {
					mimeCache.cache(file, mime);
				}
				if (mime != null && mime.getIcon() != null) {
					Icon icon = getIcon(mime.getIcon(), size);
					if (icon != null) {
						return icon;
					}
				}
				if (mime != null && mime.getGenericIcon() != null) {
					Icon icon = getIcon(mime.getGenericIcon(), size);
					if (icon != null) {
						return icon;
					}
				}
				if (mime != null && mime.getSubclasses() != null) {
					for (String subclass : mime.getSubclasses()) {
						Icon icon = getIcon(subclass, size);
						if (icon != null) {
							return icon;
						}
					}
				}
				return getIcon("text-x-generic", size);
			} else if (Files.isDirectory(file)) {
				return getIcon("folder", size);
			} else if (!Files.isReadable(file)) {
				return getIcon("emblem-unreadable", size);
			} else {
				return getIcon("text-x-generic", size);
			}
		} catch (Exception fse) {
			LOG.debug("Failed to load icon.", fse);
			return getIcon("dialog-error", size);
		}
	}

	public MIMEEntry getMIMEEntryForPattern(String pattern) {
		try {
			if (mimePatternCache.containsKey(pattern)) {
				return mimePatternCache.get(pattern);
			}
			MIMEEntry mime = mimeService.getMimeTypeForPattern(pattern);
			if (mime != null) {
				mimePatternCache.cache(pattern, mime);
			}
			return mime;
		} catch (Exception fse) {
			LOG.debug("Failed to load MIME.", fse);
			return null;
		}
	}

	public MIMEEntry getMIMEEntryForFile(Path file, boolean useMagic) {
		try {
			if (mimeCache.containsKey(file)) {
				return mimeCache.get(file);
			}
			if (Files.isRegularFile(file) || Files.isDirectory(file)) {
				MIMEEntry mime = mimeService.getMimeTypeForFile(file, useMagic);
				if (mime != null) {
					mimeCache.cache(file, mime);
				}
				return mime;
			} else {
				return null;
			}
		} catch (Exception fse) {
			LOG.debug("Failed to load MIME.", fse);
			return null;
		}
	}

	public MIMEService getMIMEService() {
		return mimeService;
	}

	public IconService getService() {
		return iconService;
	}

	public void setDefaultThemeName(String defaultThemeName) {
		iconService.setDefaultThemeName(defaultThemeName);
	}

	private Icon get(String name, int size, String cacheKey, Path file) throws FileSystemException, IOException {
		Icon icon;
		if (file.getFileName().toString().toLowerCase().endsWith(".svg")) {
			InputStream in = Files.newInputStream(file);
			try {
				icon = new SVGIcon(name + "-" + size, in, size, size);
			} finally {
				in.close();
			}
		} else {
			DataInputStream din = new DataInputStream(Files.newInputStream(file));
			try {
				byte[] imgData = new byte[(int) Files.size(file)];
				din.readFully(imgData);
				icon = new ImageIcon(imgData);
			} finally {
				din.close();
			}
		}
		if (icon.getIconWidth() != size && icon instanceof ImageIcon) {
			Image img = ((ImageIcon) icon).getImage();
			img = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
			icon = new ImageIcon(img);
		}
		return icon;
	}

	public void addGlobalFallbackTheme(String theme) {
		iconService.addGlobalFallbackTheme(theme);
	}
}
