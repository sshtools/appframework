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
