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
package com.google.code.gtkjfilechooser;

import static java.awt.Image.SCALE_SMOOTH;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.google.code.gtkjfilechooser.FreeDesktopUtil.WellKnownDir;
import com.google.code.gtkjfilechooser.ui.MissingResourceIcon;
import com.sshtools.appframework.ui.IconStore;

public class GtkStockIcon {
	/**
	 * These are type-safe versions of the hard-coded numbers in GTKStyle, for
	 * use with getGnomeStockIcon.
	 */
	public enum Size {
		GTK_ICON_SIZE_BUTTON(22), //
		GTK_ICON_SIZE_DIALOG(48), // 16 x 16
		GTK_ICON_SIZE_DND(32), // 18x18
		GTK_ICON_SIZE_INVALID(-1), // 24x24
		GTK_ICON_SIZE_LARGE_TOOLBAR(24), // 20x20
		GTK_ICON_SIZE_MENU(16), // 32x32
		GTK_ICON_SIZE_SMALL_TOOLBAR(22);// 48x48
		private int size;

		Size(int size) {
			this.size = size;
		}

		public int getSize() {
			return size;
		}
	}
	static private final String ALL_USER_MIME_DIR = "/usr/share/mime";
	static private final String CURRENT_USER_MIME_DIR = System.getProperty("user.home") + "/.local/share/mime";

	static private final String ICONS_FOLDER = "/usr/share/icons/gnome";

	/**
	 * Given a file or directory, return the corresponding gtk icon.
	 * 
	 * @param file file
	 * @param size size
	 * @return The gtk icon for the given file.
	 */
	static public Icon get(File file, Size size) {
		try {
			if (file.isDirectory()) {
				if (System.getProperty("user.home").endsWith(file.getAbsolutePath())) {
					// home dir ico
					return GtkStockIcon.get("places/user-home", size);
				} else if (FreeDesktopUtil.getWellKnownDirPath(WellKnownDir.DESKTOP).equals(file)) {
					// desktop dir icon
					return GtkStockIcon.get("places/user-desktop", size);
				} else {
					// normal dir
					return GtkStockIcon.get("gtk-directory", size);
				}
			}
			File iconFile = lookForThumbs(file);
			if (iconFile == null) {
				iconFile = lookForMime(file);
			}
			if (iconFile == null) {
				iconFile = lookForMagic(file);
			}
			if (iconFile == null) {
				// generic icon for file
				return get("gtk-file", size);
			}
			ImageIcon icon = new ImageIcon(iconFile.toURI().toURL());
			Image img = icon.getImage();
			int width = icon.getIconWidth();
			int height = icon.getIconHeight();
			Image scaledImg = null;
			if (size != Size.GTK_ICON_SIZE_INVALID && size.getSize() != height) {
				// If GTK_ICON_SIZE_INVALID don't resize the icons
				double ratio = width / (double) height;
				if (ratio > 0) {
					width = size.getSize();
					height = (int) (width / ratio);
				} else {
					height = size.getSize();
					width = (int) (height / ratio);
				}
				scaledImg = img.getScaledInstance(width, height, SCALE_SMOOTH);
			}
			// If we haven't scaled the image, return the original one.
			return (scaledImg != null) ? new ImageIcon(scaledImg) : icon;
		} catch (Exception e) {
			e.printStackTrace();
			return get("gtk-file", size);
		}
	}

	/**
	 * Returns an Icon for one of the GNOME stock icons. If the icon is not
	 * available for any reason, you'll get null. (Not using the GTK LAF is one
	 * reason why.). See <a href=
	 * "http://library.gnome.org/devel/gtk/unstable/gtk-Stock-Items.html">gtk-Stock-Items.html</a>
	 * 
	 * @param name name
	 * @param size size
	 * @return icon
	 */
	static public Icon get(String name, Size size) {
		Icon icon = IconStore.getInstance().getIcon(name, size.getSize());
		if (icon != null)
			return icon;
		if (name.startsWith("gtk-")) {
			return getFromStock(name, size);
		}
		// If not in stock, read from the file system
		String filename = ICONS_FOLDER + "/" + size.getSize() + "x" + size.getSize() + "/" + name + ".png";
		if (!new File(filename).exists()) {
			Log.log(Level.WARNING, "No icon file '", filename, "'.");
			return new MissingResourceIcon(size.getSize());
		}
		return new ImageIcon(filename);
	}

	static String md5(String uri) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] result = md5.digest(uri.getBytes());
			return toHex(result);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
	}

	static String toFileuri(File file) {
		return file.toURI().toASCIIString().replace("file:", "file://");
	}

	/**
	 * Look for icons in the "generic-icon" file.
	 * 
	 * <p>
	 * generic-icon elements specify the icon to use as a generic icon for this
	 * particular mime-type, given by the name attribute. This is used if there
	 * is no specific icon (see icon for how these are found). These are used
	 * for categories of similar types (like spreadsheets or archives) that can
	 * use a common icon. The Icon Naming Specification lists a set of such icon
	 * names. If this element is not specified then the mimetype is used to
	 * generate the generic icon by using the top-level media type (e.g. "video"
	 * in "video/ogg") and appending "-x-generic" (i.e. "video-x-generic" in the
	 * previous example). Only one generic-icon element is allowed.
	 * </p>
	 * 
	 * @param parentdir
	 * @param mimeType
	 * @return
	 * @throws FileNotFoundException
	 */
	private static String extractIconName(String parentdir, String mimeType) throws FileNotFoundException {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(parentdir + "/generic-icons"));
			String iconname = null;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.startsWith("#")) {
					continue;
				}
				String[] elements = line.split(Pattern.quote(":"));
				if (mimeType.equals(elements[0])) {
					iconname = elements[1];
					break;
				}
			}
			return iconname;
		} finally {
			if (sc != null) {
				sc.close();
			}
		}
	}

	private static Icon getFromStock(String name, Size size) {
		try {
			Class<?> gtkStockIconClass = Class.forName("com.sun.java.swing.plaf.gtk.GTKStyle$GTKStockIcon");
			Constructor<?> constructor = gtkStockIconClass.getDeclaredConstructor(String.class, int.class);
			constructor.setAccessible(true);
			return (Icon) constructor.newInstance(name, size.ordinal());
		} catch (Exception ex) {
			return null;
		}
	}

	private static boolean isTextScript(File file) throws IOException {
		if (!file.canRead()) {
			return false;
		}
		byte[] bytes = new byte[3];
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			is.read(bytes);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return (bytes[0] == '#' && bytes[1] == '!' && bytes[2] == '/');
	}

	/**
	 * If the other methods coudn't identify the mime type, we try with magic.
	 * This is a very naive implementation that follows this rules:
	 * 
	 * 1. If the file is a script (first three bytes are !#/), we return the
	 * icon "text-x-script"
	 * 
	 * 2. If the file is executable, we return application-x-executable.png
	 * 
	 * 3. else null
	 */
	private static File lookForMagic(File file) throws IOException {
		// don't handle /dev files (devices)
		if (!file.exists() || file.getAbsolutePath().startsWith("/dev")) {
			return null;
		}
		if (isTextScript(file)) {
			return new File(ICONS_FOLDER + "/16x16/mimetypes/text-x-script.png");
		}
		if (file.canExecute()) {
			return new File(ICONS_FOLDER + "/16x16/mimetypes/application-x-executable.png");
		}
		return null;
	}

	/**
	 * Detect the mime of file according to the "shared-mime-info-spec" and
	 * returns the corresponding icon file. See <a href=
	 * "http://standards.freedesktop.org/shared-mime-info-spec/shared-mime-info-spec-latest.html">shared-mime-info-spec-latest.html</a>.
	 */
	private static File lookForMime(File file) throws IOException {
		String name = file.getName();
		String mimeType = scanMimeFile(name, ALL_USER_MIME_DIR);
		String iconname = null;
		if (mimeType != null) {
			// scan for all user
			iconname = extractIconName(ALL_USER_MIME_DIR, mimeType);
		} else {
			// if not for for all user, scan the mime directory for the current
			// user
			mimeType = scanMimeFile(name, CURRENT_USER_MIME_DIR);
			if (mimeType != null) {
				iconname = extractIconName(CURRENT_USER_MIME_DIR, mimeType);
			}
		}
		if (mimeType != null && iconname == null) {
			int indexOf = mimeType.indexOf('/');
			if (indexOf != -1) {
				/**
				 * If this element is not specified then the mimetype is used to
				 * generate the generic icon by using the top-level media type
				 * (e.g. "video" in "video/ogg") and appending "-x-generic"
				 * (i.e. "video-x-generic" in the previous example).
				 */
				String genericType = mimeType.substring(0, indexOf);
				// if text, check is executable script
				if ("text".equals(genericType)) {
					iconname = isTextScript(file) ? "text-x-script" : "text-x-generic";
				} else {
					iconname = genericType + "-x-generic";
				}
			}
		}
		if (mimeType == null) {
			return null;
		}
		// We use icons only for the file chooser, there the size 16x16 is ok
		File iconFile = new File(ICONS_FOLDER + "/16x16/mimetypes/" + iconname + ".png");
		return iconFile.exists() ? iconFile : null;
	}

	/**
	 * Look for thumns. Return null is no thumn was found. See <a href=
	 * "http://library.gnome.org/devel/libgnomeui/stable/libgnomeui-GnomeThumbnail.html#gnome-thumbnail-md5">libgnomeui-GnomeThumbnail.html#gnome-thumbnail-md5</a>
	 */
	private static File lookForThumbs(File file) {
		String thumbsFolder = System.getProperty("user.home") + "/.thumbnails/normal";
		String md5 = md5(toFileuri(file));
		File thumn = new File(thumbsFolder + "/" + md5 + ".png");
		if (!thumn.exists()) {
			return null;
		}
		return thumn;
	}

	private static String scanMimeFile(String filename, String parentDir) throws FileNotFoundException {
		File globs2 = new File(parentDir + "/globs2");
		if (!globs2.exists()) {
			return null;
		}
		Scanner sc = null;
		try {
			sc = new Scanner(globs2);
			String mimeType = null;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.startsWith("#")) {
					continue;
				}
				String[] elements = line.split(Pattern.quote(":"));
				String mimePattern = elements[2];
				if (Wildcard.matches(mimePattern, filename)) {
					mimeType = elements[1];
					break;
				}
			}
			return mimeType;
		} finally {
			if (sc != null) {
				sc.close();
			}
		}
	}

	static private String toHex(byte[] b) {
		StringBuilder sb = new StringBuilder(64);
		for (int i = 0; i < b.length; i++) {
			sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}
