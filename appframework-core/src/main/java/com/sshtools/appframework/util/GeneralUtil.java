package com.sshtools.appframework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.PropertyPermission;

public class GeneralUtil {

	private static SecureRandom rnd;
	static {
		rnd = new SecureRandom();
		rnd.nextInt();
	}

	/**
	 * 
	 * Get a system property given its name, but make sure the security
	 * 
	 * manager will allow read access first. If system properties cannot
	 * 
	 * be read, the default value will be returned.
	 * 
	 * @param property property name
	 * 
	 * @param defaultValue default value to return if not allowed to read
	 *            sysprops *
	 * 
	 * @return value
	 * 
	 */
	public static String checkAndGetProperty(String property, String defaultValue) {
		// Check for access to sshtools.platform
		try {
			if (System.getSecurityManager() != null) {
				AccessController.checkPermission(new PropertyPermission(property, "read"));
			}
			return System.getProperty(property, defaultValue);
		} catch (AccessControlException ace) {
			return defaultValue;
		}
	}

	/**
	 * 
	 * Return an instance of a secure random number generate.
	 * 
	 * @return secure random number generator
	 * 
	 */
	public static SecureRandom getRND() {
		return rnd;
	}

	/**
	 * 
	 * <p>
	 * Locate the applications version properties file. This should be included
	 * 
	 * in the classpath as a resource called
	 * <code>[application-name].properties</code>.
	 * 
	 * This file must include at least 4 properties :-
	 * </p>
	 * 
	 * 
	 * 
	 * <ul>
	 * 
	 * <li>[application-name].version.major</li>
	 * 
	 * <li>[application-name].version.minor</li>
	 * 
	 * <li>[application-name].version.build</li>
	 * 
	 * <li>[application-name].project.type</li>
	 * 
	 * </ul>
	 * 
	 * @param projectname project name
	 * 
	 * @param clazz class to use to retrieve the resource
	 * 
	 * @return verson string
	 * 
	 */
	public static String getVersionString(String projectname, Class clazz) {
		Properties properties = new Properties();
		String version = "";
		InputStream in = null;
		try {
			String resource = "/" + projectname.toLowerCase() + ".properties";
			in = clazz.getResourceAsStream(resource);
			if (in == null) {
				File file = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator
					+ projectname.toLowerCase() + ".properties");
				in = new FileInputStream(file);
			}
			properties.load(in);
			String project = projectname.toLowerCase();
			String major = properties.getProperty("version.major");
			String minor = properties.getProperty("version.minor");
			String build = properties.getProperty("version.build");
			String tag = properties.getProperty("version.tag");
			if ((major != null) && (minor != null) && (build != null)) {
				version += (major + "." + minor + "." + build);
			}
			if (tag != null && tag.trim().length() != 0) {
				version += ("-" + tag);
			}
		} catch (Exception e) {
			return "0.0.0";
		}
		return version;
	}
}
