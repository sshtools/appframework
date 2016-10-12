/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/* HEADER */
package com.sshtools.virtualsession;

import java.util.EventListener;

/**
 * Listener interface to implement to receive events from the {@link VirtualSessionManager}
 * about changes in state of the {@link VirtualSession}s it is managing.
 *
 * @author $Author: brett $
 */

public interface VirtualSessionManagerListener
    extends EventListener {

  /**
   * The virtual session has changed in some way.
   *
   * @param session session that changed
   */
  public void virtualSessionChanged(VirtualSession session);

  /**
   * A virtual session has been added to the virtual session manager
   *
   * @param session session the has been added
   */
  public void virtualSessionAdded(VirtualSession session);

  /**
   * A virtual session has been removed from the virtual session manager
   *
   * @param session session the has been removed
   */
  public void virtualSessionRemoved(VirtualSession session);

  /**
   * A virtual session has been selected by the user
   *
   * @param session session selected
   */
  public void virtualSessionSelected(VirtualSession session);

  /**
   * A virtual session has been deselected.
   *
   * @param session the session deselected
   */
  public void virtualSessionDeselected(VirtualSession session);
}
