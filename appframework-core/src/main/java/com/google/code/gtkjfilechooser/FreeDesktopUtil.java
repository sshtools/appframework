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

import static com.google.code.gtkjfilechooser.I18N._;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import com.google.code.gtkjfilechooser.RemovableDevice.RemovableDeviceType;


/**
 * Gnome specific utilities
 * 
 * @author c.cerbo
 * 
 */
public class FreeDesktopUtil {
	/**
	 * Kappabyte: 1024 Bytes
	 */
	private static final int KB = 1024;

	/**
	 * Megabyte: 1024^2 Bytes
	 */
	private static final int MB = 1048576;

	/**
	 * Gigabyte: 1024^3 Bytes
	 */
	private static final int GB = 1073741824;

	public enum WellKnownDir {		
		DESKTOP("~/Desktop"), 
		DOWNLOAD("~/Download"), 
		TEMPLATES("~/Templates"), 
		PUBLICSHARE("~/Public"), 
		DOCUMENTS("~/Documents"), 
		MUSIC("~/Music"),
		PICTURES("~/Pictures"), 
		VIDEOS("~/Videos");

		private String defaultPath;

		private WellKnownDir(String defaultPath) {
			this.defaultPath = defaultPath;
		}

		public String getDefaultPath() {
			return defaultPath;
		}
	}

	static private final String HUMAN_READABLE_FMT = "%.1f %s";

	static private Properties userDirsProps;

	/**
	 * Retrieve the path of "well known" user directories like the desktop
	 * folder and the music folder.
	 * 
	 * @param type
	 * @return
	 * 
	 * @see http://freedesktop.org/wiki/Software/xdg-user-dirs
	 */
	static public File getWellKnownDirPath(WellKnownDir type) {
		if (userDirsProps == null) {
			initUserDirsProps();
		}

		String pathname = userDirsProps.getProperty("XDG_" + type + "_DIR");
		String property = expandEnv(pathname != null ? pathname : type.getDefaultPath());
		File path = new File(property);
		return path.exists() ? path : null;
	}

	private static void initUserDirsProps() throws IOError {
		userDirsProps = new Properties();
		File userDirsFile = new File(System.getProperty("user.home") + "/.config/user-dirs.dirs");

		// xdg-user-dirs may be not installed.
		if (userDirsFile.exists()) {
			try {
				FileInputStream is = null;
				try {
					is = new FileInputStream(userDirsFile);
					userDirsProps.load(is);
				} finally {
					if (is != null){
						is.close();
					}
				}
			} catch (IOException e) {
				throw new IOError(e);
			}
		} 		
	}

	/**
	 * Expand the environment variables contained in a string
	 * 
	 * @param str
	 *            The string to expand
	 * @return The expanded string
	 */
	static public String expandEnv(String str) {
		StringBuilder sb = new StringBuilder();

		int i = 0;
		while (i < str.length()) {
			char ch = str.charAt(i);
			if ('$' == ch) {
				// Expand variable
				ch = str.charAt(++i);
				int start = i;
				while (Character.isLetter(ch)) {
					ch = str.charAt(i);
					i++;
				}
				int end = i - 1;
				sb.append(System.getenv(str.substring(start, end)));
				i = end;
			} else if ('\\' == ch) {
				// handle escape chars
				ch = str.charAt(++i);
				sb.append(ch);
			} else if ('"' == ch) {
				// ignore quotes
				i++;
			} else if ('~' == ch) {
				sb.append(System.getProperty("user.home"));
				i++;
			} else {
				sb.append(ch);
				i++;
			}
		}

		return sb.toString();
	}

	/**
	 * Returns the list of the removable devices current mounted.
	 * 
	 * @return The list of the removable devices current mounted.
	 * 
	 * @see <a
	 *      href="http://www.pathname.com/fhs/pub/fhs-2.3.html#MEDIAMOUNTPOINT">Filesystem
	 *      Hierarchy Standard: /media : Mount point for removeable media</a>
	 */
	static public List<RemovableDevice> getRemovableDevices() {		
		List<RemovableDevice> devices = new ArrayList<RemovableDevice>();
		
		//TODO Issue 47
		String[] diskUUIDs = !Platform.isSolaris() ?  new File("/dev/disk/by-uuid/").list() : new String[0];			
		Arrays.sort(diskUUIDs);		

		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(new File(Platform.isSolaris() ? "/etc/mnttab" : "/proc/mounts"));
			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine();
				Scanner lineScanner = null;
				try {
					lineScanner = new Scanner(line);
					String dev = lineScanner.next();
					String location = lineScanner.next();
					if (location.startsWith("/media/")) {
						RemovableDevice device = new RemovableDevice();
						device.setLocation(escapes(location));
						device.setType(RemovableDeviceType.getType(dev));
						String name = location.substring("/media/".length());						
						name = escapes(name);
						if (Arrays.binarySearch(diskUUIDs, name) >= 0) {
							// Removable device without name.
							// Set a generic name with size
							name = humanreadble(new File(location).getTotalSpace(),	GB / 2) + " " + _("File System");
						}

						device.setName(name);

						// add to the results list
						devices.add(device);
					}
				} finally {
					if (lineScanner != null) {
						lineScanner.close();
					}
				}
			}
		} catch (IOException e) {
			throw new IOError(e);
		} finally {
			if (fileScanner != null) {
				fileScanner.close();
			}
		}

		return devices;
	}

	/**
	 * Replace space escape sequences.
	 */
	private static String escapes(String name) {
		name = name.replace("\\040", " ");
		return name;
	}

	/**
	 * 
	 * Format bytes to make them human readable.
	 * 
	 * <p>
	 * For example 1572864 Bytes = 1,5 MB
	 * </p>
	 * 
	 * @param bytes
	 *            A value in bytes
	 * @param roundFactor
	 *            The round factory, for example 1/2 GB.
	 * @return The converted value (in Bytes, KB, MB or GB)
	 */
	public static String humanreadble(long bytes, int roundFactor) {
		long roundedBytes = bytes;

		if (roundFactor > 0) {
			long mod = bytes % roundFactor;
			if (mod != 0) {
				roundedBytes += (roundFactor - mod);
			}
		}

		if (roundedBytes >= GB) {
			return String.format(HUMAN_READABLE_FMT, (roundedBytes / (double) GB), "GB");
		} else if (roundedBytes >= MB) { // 1024^2
			return String.format(HUMAN_READABLE_FMT, (roundedBytes / (double) MB), "MB");
		} else if (roundedBytes >= KB) {
			return String.format(HUMAN_READABLE_FMT, (roundedBytes / (double) KB), "KB");
		} else {
			return roundedBytes + " Bytes";
		}
	}

	static public List<BasicPath> getBasicLocations() {
		List<BasicPath> basicLocations = new ArrayList<BasicPath>();

		basicLocations.add(BasicPath.HOME);
		if (BasicPath.DESKTOP != null) {
			// When the user has deleted the desktop folder.
			basicLocations.add(BasicPath.DESKTOP);
		}		
		basicLocations.add(BasicPath.ROOT);

		return basicLocations;
	}
}
