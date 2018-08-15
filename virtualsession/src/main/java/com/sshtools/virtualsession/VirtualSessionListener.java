/* HEADER */
package com.sshtools.virtualsession;

import java.util.EventListener;

/**
 * Implementations receive events about activity in the virtual sessions, such
 * as connection events, resizes, data sent and received.
 */
public interface VirtualSessionListener extends EventListener {
	/**
	 * The title of the virtual session has changed.
	 * 
	 * @param session session
	 * @param title title
	 */
	void titleChanged(VirtualSession<?, ?> session, String title);

	/**
	 * A connection to a host has been made.
	 * 
	 * @param session virtual session connected to.
	 */
	void connected(VirtualSession<?, ?> session);

	/**
	 * A connection to a host has been broken
	 * 
	 * @param session virtual session
	 * @param exception cause of disconnect (or <code>null</code> for a normal
	 *            disconnect)
	 */
	void disconnected(VirtualSession<?, ?> session, Throwable exception);

	/**
	 * The session has sent data to the host
	 * 
	 * @param session virtual session
	 * @param data data
	 * @param len length of data
	 */
	void dataSent(VirtualSession<?, ?> session, byte[] data, int len);

	/**
	 * The host has sent the session data
	 * 
	 * @param session virtual session
	 * @param data data
	 * @param len length of data
	 */
	void dataReceived(VirtualSession<?, ?> session, byte[] data, int len);
}
