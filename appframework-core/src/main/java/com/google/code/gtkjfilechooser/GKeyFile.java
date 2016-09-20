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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

/**
 * {@code GKeyFile} lets you parse, edit or create files containing groups of
 * key-value pairs, which we call <em>key files</em> for lack of a better name.
 * Several freedesktop.org specifications use key files now, e.g the <a
 * class="ulink" href="http://freedesktop.org/Standards/desktop-entry-spec"
 * target="_top">Desktop Entry Specification</a> and the <a class="ulink"
 * href="http://freedesktop.org/Standards/icon-theme-spec" target="_top">Icon
 * Theme Specification</a>. </p>
 * 
 * <p>
 * The syntax of key files is described in detail in the <a class="ulink"
 * href="http://freedesktop.org/Standards/desktop-entry-spec"
 * target="_top">Desktop Entry Specification</a>, here is a quick summary: Key
 * files consists of groups of key-value pairs, interspersed with comments.
 * </p>
 * <div class="informalexample">
 * 
 * <pre class="programlisting"> # this is just an example # there can be
 * comments before the first group [First Group] Name=Key File Example\tthis
 * value shows\nescaping # localized strings are stored in multiple key-value
 * pairs Welcome=Hello Welcome[de]=Hallo Welcome[fr_FR]=Bonjour Welcome[it]=Ciao
 * Welcome[be@latin]=Hello [Another Group] Numbers=2;20;-200;0
 * Booleans=true;false;true;true </pre>
 * 
 * </div>
 * <p>
 * Lines beginning with a '#' and blank lines are considered comments.
 * </p>
 * <p>
 * Groups are started by a header line containing the group name enclosed in '['
 * and ']', and ended implicitly by the start of the next group or the end of
 * the file. Each key-value pair must be contained in a group.
 * </p>
 * <p>
 * Key-value pairs generally have the form <code
 * class="literal">key=value</code>, with the exception of localized strings,
 * which have the form
 * 
 * <code class="literal">key[locale]=value</code>, with a locale identifier of
 * the form <code class="literal">lang_COUNTRY<em class="parameter">
 * <code>MODIFIER</code> </em></code> where <code class="literal">COUNTRY</code>
 * and <code class="literal">MODIFIER</code> are optional. Space before and
 * after the '=' character are ignored. Newline, tab, carriage return and
 * backslash characters in value are escaped as \n, \t, \r, and \\,
 * respectively. To preserve leading spaces in values, these can also be escaped
 * as \s.
 * </p>
 * <p>
 * Key files can store strings (possibly with localized variants), integers,
 * booleans and lists of these. Lists are separated by a separator character,
 * typically ';' or ','. To use the list separator character in a value in a
 * list, it has to be escaped by prefixing it with a backslash.
 * </p>
 * <p>
 * 
 * This syntax is obviously inspired by the <code class="filename">.ini</code>
 * files commonly met on Windows, but there are some important differences:
 * </p>
 * <div class="itemizedlist"> <ul type="disc"> <li>
 * <p>
 * <code class="filename">.ini</code> files use the ';' character to begin
 * comments, key files use the '#' character.
 * </p>
 * </li> <li>
 * <p>
 * Key files do not allow for ungrouped keys meaning only comments can precede
 * the first group.
 * </p>
 * </li> <li>
 * <p>
 * Key files are always encoded in UTF-8.
 * </p>
 * </li>
 * 
 * <li>
 * <p>
 * Key and Group names are case-sensitive, for example a group called <code
 * class="literal">[GROUP]</code> is a different group from <code
 * class="literal">[group]</code>.
 * </p>
 * </li> <li>
 * <p>
 * <code class="filename">.ini</code> files don't have a strongly typed boolean
 * entry type, they only have <code class="literal">GetProfileInt</code>. In
 * <span class="structname">GKeyFile</span> only
 * 
 * <code class="literal">true</code> and <code class="literal">false</code> (in
 * lower case) are allowed.
 * </p>
 * </li> </ul> </div>
 * <p>
 * </p>
 * <p>
 * Note that in contrast to the <a class="ulink"
 * href="http://freedesktop.org/Standards/desktop-entry-spec"
 * target="_top">Desktop Entry Specification</a>, groups in key files may
 * contain the same key multiple times; the last entry wins. Key files may also
 * contain multiple groups with the same name; they are merged together. Another
 * difference is that keys and group names in key files are not restricted to
 * ASCII characters.
 * </p>
 * 
 * @author c.cerbo
 * @see http
 *      ://library.gnome.org/devel/glib/stable/glib-Key-value-file-parser.html
 * @see http://git.gnome.org/cgit/glib/tree/glib/gkeyfile.c
 * @see http://git.gnome.org/cgit/glib/tree/glib/tests/keyfile.c
 */
public class GKeyFile {
	private File gkeyfile;
	private Map<String, Group> groups;

	public GKeyFile(File gkeyfile) throws IOException {
		this.gkeyfile = gkeyfile;
		this.groups = new LinkedHashMap<String, Group>();

		if (!gkeyfile.exists()) {
			//create the file, if not exists
			if (!gkeyfile.getParentFile().exists()) {
				//create the parent dirs
				gkeyfile.getParentFile().mkdirs();
			}
			gkeyfile.createNewFile();
		}

		load();
	}

	public File getGkeyfile() {
		return gkeyfile;
	}

	public void load() throws IOException {
		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(gkeyfile);
			String currentGroupName = null;
			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine().trim();
				if (line.isEmpty() || line.startsWith("#")) {
					// skip empty or comment lines
					continue;
				}

				if (isGroupHeader(line)) {
					currentGroupName = line.substring(1, line.length() - 1);
					Group group = new Group(currentGroupName);
					groups.put(currentGroupName, group);
					continue;
				}

				Group group = groups.get(currentGroupName);
				int separatorIndex = line.indexOf('=');
				String key = line.substring(0, separatorIndex).trim();
				String value = line.substring(separatorIndex + 1, line.length()).trim();
				group.setValue(key, decodeEscape(value));
			}
		} finally {
			if (fileScanner != null) {
				fileScanner.close();
			}
		}
	}

	public void save() throws IOException {
		FileWriter w = null;
		try {
			w = new FileWriter(gkeyfile);
			save(w);
		} finally {
			if (w != null) {
				w.close();
			}
		}
	}

	// package visibility for testing
	void save(Writer w) throws IOException {
		for (Entry<String, Group> entry : groups.entrySet()) {
			w.write("\n[" + entry.getKey() + "]\n");
			Group group = entry.getValue();
			for (Entry<String, String> element : group.backingMap.entrySet()) {
				w.write(element.getKey() + "=" + encodeEscape(element.getValue()) + "\n");
			}
		}
	}

	/**
	 * The escape sequences \s, \n, \t, \r and \\ are supported meaning ASCII
	 * space, newline, tab, carriage return, backslash respectively.
	 */
	private String decodeEscape(String value) {
		StringBuilder sb = new StringBuilder();
		int len = value.length();
		boolean skip = false;
		for (int i = 0; i < len; i++) {
			if (skip) {
				skip = false;
				continue;
			}
			char ch = value.charAt(i);
			if (ch == '\\' && i < (len - 1)) {
				char nextChar = value.charAt(i + 1);
				switch (nextChar) {
				case 's':
					sb.append(' ');
					skip = true;
					continue;
				case 'n':
					sb.append('\n');
					skip = true;
					continue;
				case 't':
					sb.append('\t');
					skip = true;
					continue;
				case 'r':
					sb.append('\r');
					skip = true;
					continue;
				case '\\':
					sb.append('\\');
					skip = true;
					continue;
				}
			}

			sb.append(ch);
		}

		return sb.toString();
	}

	/**
	 * @see #decodeEscape(String)
	 */
	private String encodeEscape(String value) {
		StringBuilder sb = new StringBuilder();
		int len = value.length();
		for (int i = 0; i < len; i++) {
			char ch = value.charAt(i);

			// encode leading spaces
			if (ch == ' ' && (i == 0 || i == len - 1)) {
				sb.append("\\s");
				continue;
			}
			switch (ch) {
			case '\n':
				sb.append("\\n");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\r':
				sb.append("\\r");
				break;

			default:
				sb.append(ch);
				break;
			}
		}

		return sb.toString();
	}

	private boolean isGroupHeader(String line) {
		return line.charAt(0) == '[' && line.charAt(line.length() - 1) == ']';
	}

	/**
	 * Create and return a new group in the current {@link GKeyFile}.
	 * 
	 * @param name
	 *            The name of the new group
	 * @return The newly created group.
	 */
	public Group createGroup(String name) {
		Group group = new Group(name);
		groups.put(name, group);
		return group;
	}

	public Group getGroup(String name) {
		Group group = groups.get(name);		
		return group;
	}

	/**
	 * Inner Class for Groups of Settings
	 * 
	 * @author c.cerbo
	 * 
	 */
	public class Group {
		private Map<String, String> backingMap;
		private String name;

		public Group(String name) {
			this.name = name;
			this.backingMap = new LinkedHashMap<String, String>();
		}

		public String getString(String key) {
			return backingMap.get(key);
		}

		/**
		 * Get a String property value.
		 * 
		 * @param key
		 * @param def a Default value, if no value is found.
		 * @return The String property value for the given key.
		 */
		public String getString(String key, String def) {
			String value = getString(key);
			if (value == null) {
				value = def;
				setValue(key, value);
			}

			return value;
		}

		public Integer getInteger(String key) {
			return valueOf(Integer.class, backingMap.get(key), key);
		}

		public Integer getInteger(String key, Integer def) {
			Integer value = getInteger(key);
			if (value == null) {
				value = def;
				setValue(key, value);
			}

			return value;
		}

		/**
		 * If no entry is found, it's returned {@code false}.
		 *  
		 * @param key
		 * @return
		 */
		public Boolean getBoolean(String key) {
			return valueOf(Boolean.class, backingMap.get(key), key);
		}

		public Boolean getBoolean(String key, Boolean def) {
			Boolean value = getBoolean(key);
			if (value == null) {
				value = def;
				setValue(key, value);
			}

			return value;
		}

		public Double getDouble(String key) {
			return valueOf(Double.class, backingMap.get(key), key);
		}

		public Double getDouble(String key, Double def) {
			Double value = getDouble(key);
			if (value == null) {
				value = def;
				setValue(key, value);
			}

			return value;
		}

		public List<String> getStringList(String key) {
			return getListInternal(key, String.class);
		}

		public List<String> getStringList(String key, List<String> def) {
			List<String> value = getStringList(key);
			if (value == null) {
				value = def;
				setValue(key, value);
			}

			return value;
		}

		public List<Integer> getIntegerList(String key) {
			return getListInternal(key, Integer.class);
		}

		public List<Integer> getIntegerList(String key, List<Integer> def) {
			List<Integer> value = getIntegerList(key);
			if (value == null) {
				value = def;
				setValue(key, value);
			}

			return value;
		}

		public List<Boolean> getBooleanList(String key) {
			return getListInternal(key, Boolean.class);
		}

		public List<Boolean> getBooleanList(String key, List<Boolean> def) {
			List<Boolean> value = getBooleanList(key);
			if (value == null) {
				value = def;
				setValue(key, value);
			}

			return value;
		}

		public List<Double> getDoubleList(String key) {
			return getListInternal(key, Double.class);
		}

		public List<Double> getDoubleList(String key, List<Double> def) {
			List<Double> value = getDoubleList(key);
			if (value == null) {
				value = def;
				setValue(key, value);
			}

			return value;
		}

		private <T> List<T> getListInternal(String key, Class<T> cls) {
			List<T> list = new ArrayList<T>();
			String value = getString(key);

			int len = value.length();
			int beginIndex = 0;
			int endIndex = 0;
			for (int i = 1; i < len; i++) {
				if ((value.charAt(i) == ',' || value.charAt(i) == ';')
						&& value.charAt(i - 1) != '\\') {
					endIndex = i;
					// The escape sequences \, and \; are supported meaning
					// comma and semicomma respectively.
					list.add(valueOf(cls, value.substring(beginIndex, endIndex).replace(
							"\\", "").trim(), key));
					beginIndex = endIndex + 1;
				}
			}

			// Last entry
			list.add(valueOf(cls, value.substring(beginIndex, len).replace("\\", "")
					.trim(), key));

			return list;
		}

		@SuppressWarnings("unchecked")
		private <T> T valueOf(Class<T> cls, String value, String key) {
			if (value == null) {
				return (T) (cls.equals(Boolean.class) ? Boolean.FALSE : null);
			}

			if (cls.equals(String.class)) {
				return (T) value;
			}

			try {
				Method method = cls.getMethod("valueOf", String.class);
				T ret = (T) method.invoke(null, value);
				return ret;
			} catch (Exception e) {
				throw new IllegalArgumentException("ValueOf exception for class '"
						+ cls.getName() + "' key '" + key + "' and value <" + value
						+ ">.", e);
			}
		}

		public String getLocaleString(String key, Locale locale) {
			return backingMap.get(key + "[" + locale + "]");
		}

		/**
		 * Set a value in this group.
		 * 
		 * @param key
		 * @param value
		 *            It can be a {@link List} or a {@link String},
		 *            {@link Integer}, {@link Boolean} or {@link Double}.
		 */
		private void setValue(String key, Object value) {
			if (value instanceof List<?>) {
				List<?> list = (List<?>) value;
				Iterator<?> iter = list.iterator();

				StringBuilder sb = new StringBuilder();
				while (iter.hasNext()) {
					String str = String.valueOf(iter.next());
					// in a list, prefix the separator character(; or ,) with a
					// backslash.
					str = str.replace(",", "\\,");
					str = str.replace(";", "\\;");
					sb.append(str);
					if (iter.hasNext()) {
						sb.append(", ");
					}
				}

				backingMap.put(key, sb.toString());
			} else {
				backingMap.put(key, String.valueOf(value));
			}
		}

		public void setString(String key, String value) {
			setValue(key, value);
		}

		public void setStringList(String key, List<String> value) {
			setValue(key, value);
		}

		public void setInteger(String key, Integer value) {
			setValue(key, value);
		}

		public void setIntegerList(String key, List<Integer> value) {
			setValue(key, value);
		}

		public void setBoolean(String key, Boolean value) {
			setValue(key, value);
		}

		public void setBooleanList(String key, List<Boolean> value) {
			setValue(key, value);
		}

		public void setDouble(String key, Double value) {
			setValue(key, value);
		}

		public void setDoubleList(String key, List<Double> value) {
			setValue(key, value);
		}

		public void removeEntry(String key) {
			backingMap.remove(key);
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public String toString() {
			return "Group [" + name + "], Entries: " + backingMap.toString();
		}
	}

}
