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
package com.sshtools.appframework.util;

import java.io.File;
import java.io.InputStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.PropertyPermission;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

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

	public static String getArtifactVersion(String groupId, String artifactId) {
		String version = null;
		// try to load from maven properties first
		try {
			Properties p = new Properties();
			InputStream is = GeneralUtil.class.getResourceAsStream("/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties");
			if (is != null) {
				p.load(is);
				version = p.getProperty("version", "");
			}
		} catch (Exception e) {
			// ignore
		}
		// fallback to using Java API
		if (version == null) {
			Package aPackage = GeneralUtil.class.getPackage();
			if (aPackage != null) {
				version = aPackage.getImplementationVersion();
				if (version == null) {
					version = aPackage.getSpecificationVersion();
				}
			}
		}
		if (version == null) {
			version = getPOMVersion();
		}
		return version;
	}

	/**
	 * Get the version from the pom.xml in the current directory. Only useful
	 * for tests in development environment.
	 * 
	 * @return POM version
	 */
	public static String getPOMVersion() {
		String version;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("pom.xml"));
			version = doc.getDocumentElement().getElementsByTagName("version").item(0).getTextContent();
		} catch (Exception e) {
			version = "0.0.0";
		}
		return version;
	}
}
