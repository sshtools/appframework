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
 * @param S 
 */

public interface VirtualSession<S> {

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
	 * @param virtualSessionManager
	 *            the virtual session manager session has been added to
	 */
	void init(VirtualSessionManager virtualSessionManager);

	/**
	 * Get the virtual session manager. Will return <code>null</code> until
	 * <code>init</code> has been called.
	 * 
	 * @return the virtual session manager session has been added to
	 */
	VirtualSessionManager getVirtualSessionManager();

	/**
	 * Disconnect this session from the host it is connected to. If
	 * <code>true</code> is passed for the <code>doDisconnect</code> value, the
	 * the transport will also be disconnected.
	 * 
	 * @param doDisconnect
	 *            disconnect the transport
	 * @param exception
	 *            cause if any
	 */
	void disconnect(boolean doDisconnect, Throwable exception);

	/**
	 * Add a <code>VirtualSessionListener</code> to the list that should receive
	 * events such as connection made, disconnected, resizes, data sent and
	 * received etc.
	 * 
	 * @param listener
	 *            listener to add
	 */
	void addVirtualSessionListener(VirtualSessionListener listener);

	/**
	 * Remove a <code>VirtualSessionListener</code> to the list receiving events
	 * such as connection made, disconnected, resizes, data sent and received
	 * etc.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	void removeVirtualSessionListener(VirtualSessionListener listener);

	/**
	 * Connect the session to the streams provided by the transport.
	 * 
	 * @param transport
	 *            transport
	 * @throws IOException
	 *             there may be some I/O involved in connecting the session. An
	 *             exception will be thrown if an error occurs
	 * @throws AuthenticationException
	 *             connecting a session might involve secondary authentication.
	 *             This will be thrown if that fails
	 */
	void connect(ProfileTransport<S> transport) throws IOException, AuthenticationException;

	/**
	 * Get the transport currently in use. This will be set after
	 * <code>connect()</code> has been called and will be <code>null</code> if
	 * not connected.
	 * 
	 * @return transport
	 */
	ProfileTransport<S> getTransport();

	/**
	 * Prior to connection, and when the user applies connection profiles, this
	 * method will be called. The virtual session implementation should
	 * configure itself using any properties the profile provides. For example,
	 * a terminal like implementation would set the background, foreground,
	 * cursor style etc.
	 * 
	 * @param profile
	 *            profile to configure virtual session from
	 */
	void setVirtualSessionProperties(ResourceProfile profile);
}
