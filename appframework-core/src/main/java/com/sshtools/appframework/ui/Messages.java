package com.sshtools.appframework.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author magicthize
 *
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Messages {
	private static final String BUNDLE_NAME = "com.sshtools.appframework.ui.locale.Resources"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * Get string.
	 * 
	 * @param key
	 * @return value key
	 */
	public static String getString(String key) {
		// TODO Auto-generated method stub
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 *
	 */
	private Messages() {
		// TODO Auto-generated constructor stub
	}
}
