/* HEADER */
package com.sshtools.virtualsession;

import java.util.Enumeration;

import com.sshtools.profile.URI;

/**
 * Manages any number of {@link com.sshtools.virtualsession.VirtualSession}'s
 * and governs how they are displayed. For example this interface could be
 * implemented by a panel with an embedded tab component that displays each
 * session in an individual tab or a panel containing left sided view of all the
 * available sessions in a tree and a right handed view of the selected session.
 *
 */
public interface VirtualSessionManager {

	/**
	 * Add a new VirtualSession to this manager.
	 *
	 * @param vs
	 *            virtual session to add
	 */
	public void addVirtualSession(VirtualSession vs);

	/**
	 * Remove a VirtualSession from this terminal
	 *
	 * @param vs
	 *            virtual session to remove
	 */
	public void removeVirtualSession(VirtualSession vs);

	/**
	 * Return the number of virtual sessions.
	 *
	 * @return virtual sessions
	 */
	public int getVirtualSessionCount();

	/**
	 * Return the currently selected virtual session
	 *
	 * @return selected session
	 */
	public VirtualSession getSelectedVirtualSession();

	/**
	 * Return the currently selected virtual index
	 *
	 * @return selected terminal index
	 */
	public int getSelectedVirtualSessionIndex();

	/**
	 * Return the virtual session at the specified index
	 *
	 * @param i
	 *            index
	 * @return virtual terminal
	 */
	public VirtualSession getVirtualSession(int i);

	/**
	 * Make the provided virtual session the selected one
	 *
	 * @param vs
	 *            virtual session to select
	 */
	public void setSelectedVirtualSession(VirtualSession vs);

	/**
	 * Add a <code>VirtualSessionManagerListener</code> to the list that should
	 * receive events about changes in the state of the virtual sessions managed
	 * by this virtual session manager.
	 *
	 * @param listener
	 *            listener to add
	 */
	public void addVirtualSessionManagerListener(VirtualSessionManagerListener listener);

	/**
	 * Removes a <code>VirtualSessionManagerListener</code> from the list that
	 * should receive events about changes in the state of the virtual sessions
	 * managed by this virtual session manager.
	 *
	 * @param listener
	 *            listener to remove
	 */
	public void removeVirtualSessionManagerListener(VirtualSessionManagerListener listener);

	/**
	 * Return an enumeration of virtual sessions
	 *
	 * @return enumeration of virtual session
	 */
	public Enumeration virtualSessions();

	/**
	 * The applet container may have supplied an SSL Explorer embedded client
	 * ticket URI that may be used to initialise and authenticate a secure
	 * proxied connection. <code>null</code> will be returned if no such ticket
	 * has been provided.
	 *
	 * @return ticket URI
	 */
	public URI getEmbeddedClientTicketURI();
}