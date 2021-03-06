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

import static com.google.code.gtkjfilechooser.UrlUtil.decode;
import static com.google.code.gtkjfilechooser.UrlUtil.encode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Read, add, edit and delete GTK Bookmarks.
 * 
 * @author c.cerbo
 * 
 */
public class BookmarkManager implements Serializable {


	/**
	 * Inner Class
	 * 
	 * Bookmark entity
	 */
	public class GtkBookmark extends BasicPath {

		private static final long serialVersionUID = 1L;

		public GtkBookmark() {
			super();
		}

		public GtkBookmark(String name, File location) {
			setName(name);
			setLocation(location.getAbsolutePath());
		}

		public GtkBookmark(String name, String location) {
			setName(name);
			setLocation(location);
		}

		@Override
		public String getIconName() {
			return "gtk-directory";
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public void setName(String name) {
			this.name = name;
		}



		public String toGtkString() {
			String loc = encode(getLocation());
			return "file://" + loc + " " + getName();
		}

		@Override
		public String toString() {
			return getName() + " = " + getLocation();
		}
	}

	private static final long serialVersionUID = 1L;

	private File bookmarkfile;

	public BookmarkManager() {
		bookmarkfile = new File(System.getProperty("user.home") + File.separator
				+ ".gtk-bookmarks");
	}

	/**
	 * Add a directory as bookmarks.
	 * 
	 * @param dir
	 *            The directory to bookmark
	 * @param name
	 *            the name of the bookmark. If {@code null}, the name is the
	 *            simple directory name.
	 * @return bookmark
	 */
	public GtkBookmark add(File dir, String name) {
		if (!dir.exists()) {
			throw new IllegalArgumentException(dir + " doesn't exist.");
		}
		
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(dir + " isn't a directory.");
		}

		if (name == null) {
			name = dir.getName();
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(bookmarkfile, true));
			GtkBookmark gtkBookmark = new GtkBookmark(name, dir);
			pw.println(gtkBookmark.toGtkString());
			return gtkBookmark;
		} catch (IOException e) {
			throw new IOError(e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * Delete bookmark by name
	 * 
	 * @param name name of bookmark
	 */
	public void delete(String name) {
		String[] lines = null;
		PrintWriter pw = null;
		try {
			lines = toStringArray(bookmarkfile);
			pw = new PrintWriter(new FileWriter(bookmarkfile, false));
			for (String line : lines) {

				String currentName = null;
				int spaceIndex = line.indexOf(' ');
				if (spaceIndex != -1) {
					// Specific name
					currentName = line.substring(spaceIndex + 1);
				} else {
					// Standard name (the part after the last file separator)
					int lastSeparator = line.lastIndexOf(File.separatorChar);
					currentName = line.substring(lastSeparator + 1);
				}

				currentName = decode(currentName);

				// If the current name differs, write it again in file
				if (!currentName.equals(name)) {
					pw.println(line);
				}
			}
		} catch (Exception e) {
			throw new IOError(e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * Returns all current user's bookmarks (only file bookmarks are currently read.).
	 * 
	 * @return All current user's file bookmarks
	 */
	public List<GtkBookmark> getAll() {
		List<GtkBookmark> bookmarks = new ArrayList<GtkBookmark>();

		if (!bookmarkfile.exists()) {
			return new ArrayList<GtkBookmark>();
		}

		Scanner sc = null;
		try {
			sc = new Scanner(bookmarkfile);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();

				if (line.indexOf("/.Trash/") != -1) {
					// Logically removed bookmark
					continue;
				}

				String name = null;
				String dir = null;
				int spaceIndex = line.indexOf(' ');
				if (spaceIndex != -1) {
					// Specific name
					name = line.substring(spaceIndex + 1);
					dir = line.substring(0, spaceIndex);
				} else {
					// Standard name (the part after the last file separator)
					int lastSeparator = line.lastIndexOf(File.separatorChar);
					name = line.substring(lastSeparator + 1);
					dir = line;
				}

				if (dir.startsWith("file://")) {
					dir = dir.substring("file://".length());

					bookmarks.add(new GtkBookmark(decode(name), decode(dir)));
				}
			}
		} catch (Exception e) {
			throw new IOError(e);
		} finally {
			if (sc != null) {
				sc.close();
			}
		}

		return bookmarks;
	}


	/**
	 * Rename a bookmark
	 * 
	 * @param oldName old name
	 * @param newName new name
	 */
	public void rename(String oldName, String newName) {
		String[] lines = null;
		PrintWriter pw = null;
		try {
			lines = toStringArray(bookmarkfile);
			pw = new PrintWriter(new FileWriter(bookmarkfile, false));
			for (String line : lines) {

				String currentName = null;
				String location = null;
				int spaceIndex = line.indexOf(' ');
				if (spaceIndex != -1) {
					// Specific name
					currentName = line.substring(spaceIndex + 1);
					location = line.substring(0, spaceIndex);
				} else {
					// Standard name (the part after the last file separator)
					int lastSeparator = line.lastIndexOf(File.separatorChar);
					currentName = line.substring(lastSeparator + 1);
					location = line;
				}

				// If the current name differs, write it again in file
				if (currentName.equals(oldName)) {
					pw.println(location + " " + newName);
				} else {
					pw.println(line);
				}
			}
		} catch (Exception e) {
			throw new IOError(e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}



	/**
	 * Load a text file in an Array of Strings, where each element is diffent line
	 * 
	 * @param file
	 *            The file to load
	 * @return The text content of the file as {@link CharSequence}
	 * @throws IOException
	 */
	private String[] toStringArray(File source) throws IOException {
		Scanner sc = new Scanner(source);
		try {
			List<String> lines = new ArrayList<String>();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				lines.add(line);
			}

			return lines.toArray(new String[lines.size()]);
		} finally {
			sc.close();
		}
	}
}
