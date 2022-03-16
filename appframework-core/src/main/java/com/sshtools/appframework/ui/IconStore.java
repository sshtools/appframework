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

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.carbonicons.CarbonIcons;
import org.kordamp.ikonli.fileicons.FileIcons;
import org.kordamp.ikonli.swing.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sshtools.jfreedesktop.mime.AliasService;
import com.sshtools.jfreedesktop.mime.DefaultAliasService;
import com.sshtools.jfreedesktop.mime.DefaultGlobService;
import com.sshtools.jfreedesktop.mime.DefaultMIMEService;
import com.sshtools.jfreedesktop.mime.DefaultMagicService;
import com.sshtools.jfreedesktop.mime.GlobService;
import com.sshtools.jfreedesktop.mime.LinuxAliasService;
import com.sshtools.jfreedesktop.mime.LinuxGlobService;
import com.sshtools.jfreedesktop.mime.LinuxMIMEService;
import com.sshtools.jfreedesktop.mime.LinuxMagicService;
import com.sshtools.jfreedesktop.mime.MIMEEntry;
import com.sshtools.jfreedesktop.mime.MIMEService;
import com.sshtools.ui.GlobalUIUtil;

public class IconStore {
	private static final String HIGHLIGHT_PREFIX = "__HIGHLIGHT__/";
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
	private DefaultMagicService magicService;
	private LimitedCache<Path, MIMEEntry> mimeCache = new LimitedCache<Path, MIMEEntry>();
	private LimitedCache<String, MIMEEntry> mimePatternCache = new LimitedCache<String, MIMEEntry>();
	private MIMEService mimeService;
	private Color iconColor;

	private IconStore() throws IOException, ParseException {
		aliasService = new DefaultAliasService();
		globService = new DefaultGlobService();
		magicService = new DefaultMagicService();
		mimeService = new DefaultMIMEService(globService, aliasService, magicService);
		if (SystemUtils.IS_OS_LINUX) {
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
	}

	public void updateIconColors() {
		this.iconColor = null;
		Color hl = getHighlightColor();
		Color ic = getIconColor();
		for (Map.Entry<String, Icon> icon : cache.entrySet()) {
			if (icon.getValue() instanceof FontIcon) {
				if (icon.getKey().startsWith(HIGHLIGHT_PREFIX)) {
					((FontIcon) icon.getValue()).setIconColor(hl);
				} else {
					((FontIcon) icon.getValue()).setIconColor(ic);
				}
			}
		}
	}

	public void configure(SshToolsApplication application) throws IOException, ParseException {
	}

	public Icon mimeIcon(String name, int size) {

		if (name.indexOf('/') > -1) {
			// TODO lookup
			return mimeIcon(name.substring(0, name.indexOf('/')), size);
		}

		switch (name) {
		case "application":
			return icon(CarbonIcons.APPLICATION, size);
		case "audio":
			return icon(CarbonIcons.MUSIC, size);
		case "chemical":
			return icon(CarbonIcons.CHEMISTRY, size);
		case "font":
			return icon(CarbonIcons.STRING_TEXT, size);
		case "image":
			return icon(CarbonIcons.IMAGE, size);
		case "inode":
			return icon(CarbonIcons.EDGE_NODE, size);
		case "message":
			return icon(CarbonIcons.EMAIL, size);
		case "model":
			return icon(CarbonIcons.MODEL, size);
		case "multipart":
			return icon(CarbonIcons.CATEGORIES, size);
		case "video":
			return icon(CarbonIcons.VIDEO, size);
		case "text":
			return icon(FileIcons.ASCIIDOC, size);
		}
		return icon(CarbonIcons.ERROR_OUTLINE, size);
	}

	public Icon highlightIcon(Ikon name, int size) {
		return doIcon(HIGHLIGHT_PREFIX, name, size, getHighlightColor());
	}

	public Icon icon(Ikon name, int size) {
		return icon(name, size, getIconColor());
	}

	public Icon icon(Ikon name, int size, Color color) {
		return doIcon("", name, size, color);
	}

	protected Icon doIcon(String prefix, Ikon name, int size, Color color) {
		String cacheKey = prefix + name.getDescription() + "/" + size + "/" + color.getRGB();
		if (cache.containsKey(cacheKey)) {
			return cache.get(cacheKey);
		}
		Icon icon = null;
		try {
			icon = FontIcon.of(name, size, color);
		} catch (Exception e) {
			LOG.error("Failed to load icon " + name + " at size " + size + ".", e);
		}
		cache.put(cacheKey, icon);
		return icon;
	}

	public Color getHighlightColor() {
		Color iconColor = getIconColor();
		if (iconColor.equals(Color.BLACK) || iconColor.equals(Color.WHITE))
			return Color.GRAY;
		else {
			Color bg = UIManager.getColor("Panel.background");
			if (bg == null) {
				float[] hsb = Color.RGBtoHSB(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), null);
				if (hsb[2] < 0.5) {
					return Color.WHITE;
				} else {
					return Color.BLACK;
				}
			} else {
				float[] hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);
				if (hsb[2] < 0.5) {
					return Color.WHITE;
				} else {
					return Color.BLACK;
				}
			}
		}
	}

	public Color getIconColor() {
		if (this.iconColor == null) {
			Color iconColor = GlobalUIUtil
					.stringToColor(PreferencesStore.get(SshToolsApplication.PREF_ICON_COLOR, null), null);
			if (iconColor == null) {
				Color defc = UIManager.getColor("Panel.background");
				if (defc != null) {
					float[] hsb = Color.RGBtoHSB(defc.getRed(), defc.getGreen(), defc.getBlue(), null);
					if (hsb[2] < 0.5) {
						iconColor = Color.WHITE;
					} else {
						iconColor = Color.BLACK;
					}
				}
			}
			if (iconColor == null) {
				iconColor = Color.BLACK;
			}
			this.iconColor = iconColor;
		}
		return this.iconColor;
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
//				if (mime != null && mime.getIcon() != null) {
//					Icon icon = getIcon(mime.getIcon(), size);
//					if (icon != null) {
//						return icon;
//					}
//				}
//				if (mime != null && mime.getGenericIcon() != null) {
//					Icon icon = getIcon(mime.getGenericIcon(), size);
//					if (icon != null) {
//						return icon;
//					}
//				}
				if (mime != null && mime.getSubclasses() != null) {
					for (String subclass : mime.getSubclasses()) {
						Icon icon = mimeIcon(subclass, size);
						if (icon != null) {
							return icon;
						}
					}
				}
				return icon(CarbonIcons.DOCUMENT, size);
			} else if (Files.isDirectory(file)) {
				return icon(CarbonIcons.FOLDER, size);
			} else if (!Files.isReadable(file)) {
				return icon(CarbonIcons.ERROR_FILLED, size);
			} else {
				return icon(CarbonIcons.DOCUMENT, size);
			}
		} catch (Exception fse) {
			LOG.debug("Failed to load icon.", fse);
			return icon(CarbonIcons.ERROR, size);
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
}
