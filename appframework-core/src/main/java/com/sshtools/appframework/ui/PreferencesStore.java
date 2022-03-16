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

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author $author$
 */
public class PreferencesStore {
	final static Logger log = LoggerFactory.getLogger(PreferencesStore.class);
	//
	private static File file;
	private static Properties preferences;
	private static boolean storeAvailable;
	// Intialise the preferences
	static {
		preferences = new Properties();
	}

	public static String get(String name, String def) {
		return preferences.getProperty(name, def);
	}

	public static boolean getBoolean(String name, boolean def) {
		return get(name, String.valueOf(def)).equals("true");
	}

	public static double getDouble(String name, double def) {
		String s = preferences.getProperty(name);
		if ((s != null) && !s.equals("")) {
			try {
				return Double.parseDouble(s);
			} catch (NumberFormatException nfe) {
				System.err.println("Preference is " + name + " is badly formatted");
				nfe.printStackTrace();
			}
		}
		return def;
	}

	public static int getInt(String name, int def) {
		String s = preferences.getProperty(name);
		if ((s != null) && !s.equals("")) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException nfe) {
				System.err.println("Preference is " + name + " is badly formatted");
				nfe.printStackTrace();
			}
		}
		return def;
	}

	public static Rectangle getRectangle(String name, Rectangle def) {
		String s = preferences.getProperty(name);
		if ((s == null) || s.equals("")) {
			return def;
		}
		StringTokenizer st = new StringTokenizer(s, ",");
		Rectangle r = new Rectangle();
		try {
			r.x = Integer.parseInt(st.nextToken());
			r.y = Integer.parseInt(st.nextToken());
			r.width = Integer.parseInt(st.nextToken());
			r.height = Integer.parseInt(st.nextToken());
		} catch (NumberFormatException nfe) {
		}
		return r;
	}

	public static void init(File file) {
		PreferencesStore.file = file;
		// Make sure the preferences directory exists, creating it if it doesn't
		File dir = file.getParentFile();
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				System.err.println("Preferences directory " + dir.getAbsolutePath() + " could not be created. "
						+ "Preferences will not be stored");
			}
		}
		storeAvailable = dir.exists();
		// If the preferences file exists, then load it
		if (storeAvailable) {
			if (file.exists()) {
				InputStream in = null;
				try {
					in = new FileInputStream(file);
					preferences.load(in);
					storeAvailable = true;
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ioe) {
						}
					}
				}
			}
			// Otherwise create it
			else {
				savePreferences();
			}
		} else {
			System.err.println("WARNING! Preferences store not available.");
		}
	}

	public static boolean isStoreAvailable() {
		return storeAvailable;
	}

	public static boolean preferenceExists(String name) {
		return preferences.containsKey(name);
	}

	public static void put(String name, String val) {
		preferences.put(name, val);
	}

	public static void putBoolean(String name, boolean val) {
		preferences.put(name, String.valueOf(val));
	}

	public static void putDouble(String name, double val) {
		preferences.put(name, String.valueOf(val));
	}

	public static void putInt(String name, int val) {
		preferences.put(name, String.valueOf(val));
	}

	public static void putRectangle(String name, Rectangle val) {
		preferences.put(name, (val == null) ? "" : (val.x + "," + val.y + "," + val.width + "," + val.height));
	}

	public static boolean removePreference(String name) {
		boolean exists = preferenceExists(name);
		preferences.remove(name);
		return exists;
	}

	/**
	 * Restore a table.
	 *
	 * @param table         table
	 * @param pref          pref
	 * @param defaultWidths default widths
	 * @throws IllegalArgumentException on error
	 */
	public static void restoreTableMetrics(JTable table, String pref, int[] defaultWidths) {
		log.info("Restore table metrics " + pref);

		// Check the table columns may be resized correctly
		if (table.getAutoResizeMode() != JTable.AUTO_RESIZE_OFF) {
			throw new IllegalArgumentException("Table AutoResizeMode must be JTable.AUTO_RESIZE_OFF");
		}
		// Restore the table column widths and positions
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			try {
				int pos = table.convertColumnIndexToView(getInt(pref + ".column." + i + ".position", i));
				table.moveColumn(pos, i);
				table.getColumnModel().getColumn(i).setMinWidth(10);
				int width = (defaultWidths == null || defaultWidths[i] == -1)
						? table.getColumnModel().getColumn(i).getPreferredWidth()
						: defaultWidths[i];
				
				table.getColumnModel().getColumn(i).setPreferredWidth(getInt(pref + ".column." + i + ".width", width));

				log.info("    " + i + ": " + width + " " + pos);
			} catch (NumberFormatException nfe) {
			}
		}
	}

	public static void savePreferences() {
		if (file == null) {
			System.err.println("Preferences not saved as PreferencesStore has not been initialise.");
		} else {
			OutputStream out = null;
			try {
				out = new FileOutputStream(file);
				preferences.store(out, "SSHTerm preferences");
				storeAvailable = true;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException ioe) {
					}
				}
			}
		}
	}

	public static void saveTableMetrics(JTable table, String pref) {
		log.info("Saving table metrics " + pref);
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			int w = table.getColumnModel().getColumn(i).getWidth();
			put(pref + ".column." + i + ".width", String.valueOf(w));
			put(pref + ".column." + i + ".position", String.valueOf(table.convertColumnIndexToModel(i)));
			log.info("    " + i + ": " + w + " " + String.valueOf(table.convertColumnIndexToModel(i)));
		}
	}
}