/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.prefs;

import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * PreferencesFactory implementation that stores the preferences in a
 * user-defined file. To use it, set the system property
 * <tt>java.util.prefs.PreferencesFactory</tt> to
 * <tt>net.infotrek.util.prefs.FilePreferencesFactory</tt>
 * <p/>
 * The file defaults to [user.home]/.fileprefs, but may be overridden with the
 * system property <tt>net.infotrek.util.prefs.FilePreferencesFactory.file</tt>
 * 
 * @author David Croft (<a href="http://www.davidc.net">www.davidc.net</a>)
 * @version $Id: FilePreferencesFactory.java 282 2009-06-18 17:05:18Z david $
 */
public class FilePreferencesFactory implements PreferencesFactory {
	private static final Logger log = Logger
			.getLogger(FilePreferencesFactory.class.getName());

	Preferences rootPreferences;
	public static final String SYSTEM_PROPERTY_FILE = "com.sshtools.appframework.prefs.FilePreferencesFactory.file";

	public Preferences systemRoot() {
		return userRoot();
	}

	public Preferences userRoot() {
		if (rootPreferences == null) {
			log.finer("Instantiating root preferences");
			rootPreferences = new FilePreferences(null, "");
		}
		return rootPreferences;
	}

	private static File preferencesFile;

	public static void setPreferencesFile(File file) {
		preferencesFile = file;
	}

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
}