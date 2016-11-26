/* HEADER */
package com.sshtools.virtualsession;

import java.util.EventListener;

/**
 * Listener interface to implement to receive events from the
 * {@link VirtualSessionManager} about changes in state of the
 * {@link VirtualSession}s it is managing.
 */
public interface VirtualSessionManagerListener extends EventListener {
	/**
	 * The virtual session has changed in some way.
	 *
	 * @param session session that changed
	 */
	void virtualSessionChanged(VirtualSession<?, ?> session);

	/**
	 * A virtual session has been added to the virtual session manager
	 *
	 * @param session session the has been added
	 */
	void virtualSessionAdded(VirtualSession<?, ?> session);

	/**
	 * A virtual session has been removed from the virtual session manager
	 *
	 * @param session session the has been removed
	 */
	void virtualSessionRemoved(VirtualSession<?, ?> session);

	/**
	 * A virtual session has been selected by the user
	 *
	 * @param session session selected
	 */
	void virtualSessionSelected(VirtualSession<?, ?> session);

	/**
	 * A virtual session has been deselected.
	 *
	 * @param session the session deselected
	 */
	void virtualSessionDeselected(VirtualSession<?, ?> session);
}
