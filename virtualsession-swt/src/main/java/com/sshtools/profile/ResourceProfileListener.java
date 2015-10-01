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
