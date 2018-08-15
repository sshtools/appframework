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

import java.util.Locale;


/**
 * Wrapper around {@link GettextResource}. You should use this class as entry
 * point for the i18n instead of {@link GettextResource}.
 * 
 * @author Costantino Cerbo
 * 
 */
public class I18N {
	static private GettextResource resources = null;
	static private boolean resourcesNotFound = false;
	static {
		init(Locale.getDefault());
	}
	
	/**
	 * Returns the mnemonic associate with this message, or 0 if none.
	 * 
	 * @param msgid id
	 * @return the mnemonic associate with this message, or 0 if none.
	 */
	static final public int getMnemonic(String msgid) {
		String msg = getString(msgid);
		int indexOf = msg.indexOf('_');
		if (indexOf >= 0) {
			return msg.charAt(indexOf + 1);
		}
		return 0;
	}

	/**
	 * Returns the translated string without mnemonics.
	 * 
	 * @param msgid id
	 * @return the translated string without mnemonics.
	 */
	static final public String i18n(String msgid) {
		// Return a string without mnemonic
		return getString(msgid).replace("_", "");
	}

	/**
	 * @param msgid id
	 * @param args args
	 * @return message
	 * @see String#format(String, Object...)
	 */
	static final public String i18n(String msgid, Object args) {
		return String.format(i18n(msgid), args);
	}

	static public void init(Locale locale) {
		if (GettextResource.hasTranslation(locale, "gtk20")) {
			resources = new GettextResource(locale, "gtk20");
		} else if (GettextResource.hasTranslation(locale, "/usr/share/locale-langpack", "gtk20")) {
			//Ubuntu uses /usr/share/locale-langpack
			resources = new GettextResource(locale, "/usr/share/locale-langpack", "gtk20");
		} else if (GettextResource.hasTranslation(locale, "/usr/share/locale-bundle", "gtk20")) {
			//Suse uses /usr/share/locale-bundle
			resources = new GettextResource(locale, "/usr/share/locale-bundle", "gtk20");
		} else {
			resourcesNotFound = true;
		}
	}

	/**
	 * When RESOURCE is null or msgstr is empty, return the msgid (without classifier) otherwise the found msgstr.
	 */
	private static String getString(String msgid) {
		if (resourcesNotFound) {
			int indexOf = msgid.indexOf('|');
			if (indexOf < 0) {
				return msgid;
			}

			return msgid.substring(indexOf + 1);
		}
		
		if (resources == null) {
			init(Locale.getDefault());
		}
		
		return resources.getString(msgid);

		
	}
}
