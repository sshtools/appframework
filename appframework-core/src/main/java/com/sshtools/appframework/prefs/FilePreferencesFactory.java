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
package com.sshtools.appframework.prefs;

import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * <p>
 * PreferencesFactory implementation that stores the preferences in a
 * user-defined file. To use it, set the system property
 * <code>java.util.prefs.PreferencesFactory</code> to
 * <code>net.infotrek.util.prefs.FilePreferencesFactory</code>
 * </p>
 * <p>
 * The file defaults to [user.home]/.fileprefs, but may be overridden with the
 * system property <code>net.infotrek.util.prefs.FilePreferencesFactory.file</code>
 * </p>
 * 
 * @author David Croft (<a href="http://www.davidc.net">www.davidc.net</a>)
 * @version $Id: FilePreferencesFactory.java 282 2009-06-18 17:05:18Z david $
 */
public class FilePreferencesFactory implements PreferencesFactory {
	public static final String SYSTEM_PROPERTY_FILE = "com.sshtools.appframework.prefs.FilePreferencesFactory.file";

	private static final Logger log = Logger
			.getLogger(FilePreferencesFactory.class.getName());
	private static File preferencesFile;

	public static File getPreferencesFile() {
		if (preferencesFile == null) {
			String prefsFile = System.getProperty(SYSTEM_PROPERTY_FILE);
			if (prefsFile == null || prefsFile.length() == 0) {
				prefsFile = System.getProperty("user.home") + File.separator
						+ ".fileprefs";
			}
			preferencesFile = new File(prefsFile).getAbsoluteFile();
			log.finer("Preferences file is " + preferencesFile);
		}
		return preferencesFile;
	}

	public static void setPreferencesFile(File file) {
		preferencesFile = file;
	}

	Preferences rootPreferences;

	@Override
	public Preferences systemRoot() {
		return userRoot();
	}

	@Override
	public Preferences userRoot() {
		if (rootPreferences == null) {
			log.finer("Instantiating root preferences");
			rootPreferences = new FilePreferences(null, "");
		}
		return rootPreferences;
	}
}