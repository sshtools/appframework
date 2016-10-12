/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.ui.wizard;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author magicthize
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Messages {

  private static final String BUNDLE_NAME =
      "com.sshtools.appframework.ui.wizard.Resources"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.
      getBundle(BUNDLE_NAME);

  /**
   *
   */
  private Messages() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param key
   * @return
   */
  public static String getString(String key) {
    // TODO Auto-generated method stub
    try {
      return RESOURCE_BUNDLE.getString(key);
    }
    catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}
