/**
 * Maverick Virtual Session - Framework for a tabbed user interface of connections to some local or remote resources.
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/* HEADER */
package com.sshtools.virtualsession;

import java.io.IOException;

import com.sshtools.profile.AuthenticationException;
import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.ResourceProfile;

/**
 * A single virtual session provides some kind of <i>display</i> or 'screen' for
 * a single connection to a host. and will usually be added to a
 * {@link VirtualSessionManager} implementation that manages all of the virtual
 * terminals.
 * 
 * @param <T> type of transport
 * @param <M> type of session manager
 */
public interface VirtualSession<T extends ProfileTransport<?>, M extends VirtualSessionManager<? extends VirtualSession<?, ?>>> {
	/**
	 * Reset the session back to its initial state (e.g. clear the scree, clear
	 * the buffer, reset the cursor etc)
	 * 
	 */
	void reset();

	/**
	 * Return the title of this virtual session. There may be more than one
	 * session with the same title.
	 * 
	 * @return virtual session title
	 */
	String getSessionTitle();

	/**
	 * Get if this virtual session is currently connected
	 * 
	 * @return connected
	 */
	boolean isConnected();

	/**
	 * Initialise the virtual session. Called after it has been added to the
	 * virtual session manager.
	 * 
	 * @param virtualSessionManager the virtual session manager session has been
	 *            added to
	 */
	void init(M virtualSessionManager);

	/**
	 * Get the virtual session manager. Will return <code>null</code> until
	 * <code>init</code> has been called.
	 * 
	 * @return the virtual session manager session has been added to
	 */
	M getVirtualSessionManager();

	/**
	 * Disconnect this session from the host it is connected to. If
	 * <code>true</code> is passed for the <code>doDisconnect</code> value, the
	 * the transport will also be disconnected.
	 * 
	 * @param doDisconnect disconnect the transport
	 * @param exception cause if any
	 */
	void disconnect(boolean doDisconnect, Throwable exception);

	/**
	 * Add a <code>VirtualSessionListener</code> to the list that should receive
	 * events such as connection made, disconnected, resizes, data sent and
	 * received etc.
	 * 
	 * @param listener listener to add
	 */
	void addVirtualSessionListener(VirtualSessionListener listener);

	/**
	 * Remove a <code>VirtualSessionListener</code> to the list receiving events
	 * such as connection made, disconnected, resizes, data sent and received
	 * etc.
	 * 
	 * @param listener listener to remove
	 */
	void removeVirtualSessionListener(VirtualSessionListener listener);

	/**
	 * Connect the session to the streams provided by the transport.
	 * 
	 * @param transport transport
	 * @throws IOException there may be some I/O involved in connecting the
	 *             session. An exception will be thrown if an error occurs
	 * @throws AuthenticationException connecting a session might involve
	 *             secondary authentication. This will be thrown if that fails
	 */
	void connect(T transport) throws IOException, AuthenticationException;

	/**
	 * Get the transport currently in use. This will be set after
	 * <code>connect()</code> has been called and will be <code>null</code> if
	 * not connected.
	 * 
	 * @return transport
	 */
	T getTransport();

	/**
	 * Prior to connection, and when the user applies connection profiles, this
	 * method will be called. The virtual session implementation should
	 * configure itself using any properties the profile provides. For example,
	 * a terminal like implementation would set the background, foreground,
	 * cursor style etc.
	 * 
	 * @param profile profile to configure virtual session from
	 */
	void setVirtualSessionProperties(ResourceProfile<T> profile);
}
