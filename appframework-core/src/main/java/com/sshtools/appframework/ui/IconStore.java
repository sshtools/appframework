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
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
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

public class IconStore {
	final static Log LOG = LogFactory.getLog(IconStore.class);

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

	private LimitedCache<FileObject, MIMEEntry> mimeCache = new LimitedCache<FileObject, MIMEEntry>();

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
		FileObject obj = null;
		try {
			obj = VFS.getManager().resolveFile("res:" + themeName + "/index.theme");
		} catch (Exception e) {
			URL loc = getClass().getClassLoader().getResource(themeName + "/index.theme");
			try {
				String sloc = loc.toURI().toString();
				if (sloc.startsWith("jar:file:/") || !sloc.startsWith("jar:file://")) {
					sloc = "jar:jar:/" + System.getProperty("user.dir") + sloc.substring(9);
					FileObject resolveFile = VFS.getManager().resolveFile(System.getProperty("user.dir"));
					obj = VFS.getManager().resolveFile(resolveFile, sloc);
				} else {
					obj = VFS.getManager().resolveFile(sloc);

				}
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
		if (obj != null) {
			iconService.addBase(obj.getParent().getParent());
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
		if (cache.containsKey(name)) {
			return cache.get(cacheKey);
		}
		Icon icon = null;
		try {
			FileObject file = iconService.findIcon(name, 48);
			if (file != null) {

				icon = get(name, size, cacheKey, file);
			} else {
				if (fixes.containsKey(name)) {
					file = iconService.findIcon(fixes.getProperty(name), 48);
					if (file != null) {

						icon = get(name, size, cacheKey, file);
					}
				}
				// if(file == null) {
				// System.err.println("Cannot find icon " + name);
				// }
			}
		} catch (Exception e) {
			LOG.error("Failed to load icon " + name + " at size " + size + ".", e);
		}
		return icon;
	}

	public Icon getIconForFile(FileObject file) {
		return getIconForFile(file, 48);
	}

	public Icon getIconForFile(FileObject file, int size) {
		return getIconForFile(file, size, true);
	}

	public Icon getIconForFile(FileObject file, int size, boolean useMagic) {
		try {
			if (file.getType().equals(FileType.FILE)) {
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
			} else if (file.getType().equals(FileType.FOLDER)) {
				return getIcon("folder", size);
			} else if (file.getType().equals(FileType.IMAGINARY)) {
				return getIcon("emblem-unreadable", size);
			} else {
				return getIcon("text-x-generic", size);
			}
		} catch (Exception fse) {
			LOG.debug("Failed to load icon.", fse);
			return getIcon("dialog-error", size);
		}
	}

	public MIMEEntry getMIMEEntryForFile(FileObject file, boolean useMagic) {
		try {
			if (file.getType().equals(FileType.FILE) || file.getType().equals(FileType.FOLDER)) {
				MIMEEntry mime = mimeCache.get(file);
				if (mime == null) {
					mime = mimeService.getMimeTypeForFile(file, useMagic);
				}

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

	public IconService getService() {
		return iconService;
	}

	public void setDefaultThemeName(String defaultThemeName) {
		iconService.setDefaultThemeName(defaultThemeName);
	}

	private Icon get(String name, int size, String cacheKey, FileObject file) throws FileSystemException, IOException {
		Icon icon;
		if (file.getName().getBaseName().toLowerCase().endsWith(".svg")) {
			InputStream in = file.getContent().getInputStream();
			try {
				icon = new SVGIcon(name + "-" + size, in, size, size);
			} finally {
				in.close();
			}
		} else {
			DataInputStream din = new DataInputStream(file.getContent().getInputStream());
			try {
				byte[] imgData = new byte[(int) file.getContent().getSize()];
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
		cache.put(cacheKey, icon);
		return icon;
	}
}
