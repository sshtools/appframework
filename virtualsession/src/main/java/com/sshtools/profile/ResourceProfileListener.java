/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/* HEADER*/
package com.sshtools.profile;

/**
 * Listener interface to be implemented when you need to watch for changes
 * to a {@link ResourceProfile}.
 *
 * @author $Author: brett $
 */
public interface ResourceProfileListener {

  /**
   * Invoked when something in the profile changes.
   */
  public void profileChanged();

  /**
   * Invoked when the profile is loaded
   */
  public void profileLoaded();

  /**
   * Invoked when the profile is saved
   */
  public void profileSaved();
}
