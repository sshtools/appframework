/* HEADER */
package com.sshtools.virtualsession;

import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.URI;

/**
 * Manages any number of {@link com.sshtools.virtualsession.VirtualSession}'s
 * and governs how they are displayed. For example this interface could be
 * implemented by a panel with an embedded tab component that displays each
 * session in an individual tab or a panel containing left sided view of all the
 * available sessions in a tree and a right handed view of the selected session.
 * 
 * @param <S> type of session
 *
 */
public interface VirtualSessionManager<S extends VirtualSession<? extends ProfileTransport<?>, ? extends VirtualSessionManager<?>>> {
	/**
	 * Add a new VirtualSession to this manager.
	 *
	 * @param vs virtual session to add
	 */
	void addVirtualSession(S vs);

	/**
	 * Remove a VirtualSession from this terminal
	 *
	 * @param vs virtual session to remove
	 */
	void removeVirtualSession(S vs);

	/**
	 * Return the number of virtual sessions.
	 *
	 * @return virtual sessions
	 */
	int getVirtualSessionCount();

	/**
	 * Return the currently selected virtual session
	 *
	 * @return selected session
	 */
	S getSelectedVirtualSession();

	/**
	 * Return the currently selected virtual index
	 *
	 * @return selected terminal index
	 */
	int getSelectedVirtualSessionIndex();

	/**
	 * Return the virtual session at the specified index
	 *
	 * @param i index
	 * @return virtual terminal
	 */
	S getVirtualSession(int i);

	/**
	 * Make the provided virtual session the selected one
	 *
	 * @param vs virtual session to select
	 */
	void setSelectedVirtualSession(S vs);

	/**
	 * Add a <code>VirtualSessionManagerListener</code> to the list that should
	 * receive events about changes in the state of the virtual sessions managed
	 * by this virtual session manager.
	 *
	 * @param listener listener to add
	 */
	void addVirtualSessionManagerListener(VirtualSessionManagerListener listener);

	/**
	 * Removes a <code>VirtualSessionManagerListener</code> from the list that
	 * should receive events about changes in the state of the virtual sessions
	 * managed by this virtual session manager.
	 *
	 * @param listener listener to remove
	 */
	void removeVirtualSessionManagerListener(VirtualSessionManagerListener listener);

	/**
	 * Return an enumeration of virtual sessions
	 *
	 * @return enumeration of virtual session
	 */
	public Iterable<S> virtualSessions();

	/**
	 * The applet container may have supplied an SSL Explorer embedded client
	 * ticket URI that may be used to initialise and authenticate a secure
	 * proxied connection. <code>null</code> will be returned if no such ticket
	 * has been provided.
	 *
	 * @return ticket URI
	 */
	URI getEmbeddedClientTicketURI();
}