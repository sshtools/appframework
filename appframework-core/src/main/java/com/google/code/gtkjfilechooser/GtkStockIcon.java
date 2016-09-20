/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 ******************************************************************************/
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


public class GtkStockIcon {

	static private final String ICONS_FOLDER = "/usr/share/icons/gnome";
	static private final String ALL_USER_MIME_DIR = "/usr/share/mime";
	static private final String CURRENT_USER_MIME_DIR = System.getProperty("user.home")
	+ "/.local/share/mime";

	/**
	 * These are type-safe versions of the hard-coded numbers in GTKStyle, for
	 * use with getGnomeStockIcon.
	 */
	public enum Size {
		GTK_ICON_SIZE_INVALID(-1), // 
		GTK_ICON_SIZE_MENU(16), // 16 x 16
		GTK_ICON_SIZE_SMALL_TOOLBAR(22), // 18x18
		GTK_ICON_SIZE_LARGE_TOOLBAR(24), // 24x24
		GTK_ICON_SIZE_BUTTON(22), // 20x20
		GTK_ICON_SIZE_DND(32), // 32x32
		GTK_ICON_SIZE_DIALOG(48);// 48x48

		private int size;

		Size(int size) {
			this.size = size;
		}

		public int getSize() {
			return size;
		}
	}

	/**
	 * Returns an Icon for one of the GNOME stock icons. If the icon is not
	 * available for any reason, you'll get null. (Not using the GTK LAF is one
	 * reason why.)
	 * 
	 * @see http://library.gnome.org/devel/gtk/unstable/gtk-Stock-Items.html
	 */
	static public Icon get(String name, Size size) {
		if (name.startsWith("gtk-")) {
			return getFromStock(name, size);
		}

		// If not in stock, read from the file system
		String filename = ICONS_FOLDER + "/" + size.getSize() + "x" + size.getSize()
		+ "/" + name + ".png";
		if (!new File(filename).exists()) {
			Log.log(Level.WARNING, "No icon file '" , filename , "'.");
			return new MissingResourceIcon(size.getSize());
		}
		return new ImageIcon(filename);
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

	/**
	 * Given a file or directory, return the corresponding gtk icon.
	 * 
	 * @param file
	 * @param size
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
			return new File(ICONS_FOLDER
					+ "/16x16/mimetypes/application-x-executable.png");
		}

		return null;
	}

	private static boolean isTextScript(File file) throws IOException {
		if (!file.canRead()){
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
	 * Detect the mime of file according to the "shared-mime-info-spec" and
	 * returns the corresponding icon file.
	 * 
	 * @see {@link http://standards.freedesktop.org/shared-mime-info-spec/shared-mime-info-spec-latest.html}
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
	private static String extractIconName(String parentdir, String mimeType)
	throws FileNotFoundException {
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

	private static String scanMimeFile(String filename, String parentDir)
	throws FileNotFoundException {
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

	/**
	 * Look for thumns. Return null is no thumn was found.
	 * 
	 * @see {@link http://library.gnome.org/devel/libgnomeui/stable/libgnomeui-GnomeThumbnail.html#gnome-thumbnail-md5}
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

	static String toFileuri(File file) {
		return file.toURI().toASCIIString().replace("file:", "file://");
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

	static private String toHex(byte[] b) {
		StringBuilder sb = new StringBuilder(64);
		for (int i = 0; i < b.length; i++) {
			sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

}
