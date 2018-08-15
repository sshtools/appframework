/* HEADER*/
package com.sshtools.profile;

import java.io.IOException;

/**
 * ProfileTransport implementations are responsible for creating and maintaing a
 * connection to a resource. The resource {@link URI} is determined from the
 * {@link ResourceProfile} provided when <code>connect()</code> is called.
 * 
 * @param <S>
 */
public interface ProfileTransport<S> {
	/**
	 * The session this transport will be used with.
	 *
	 * @param session the session this transport is attached to
	 */
	void init(S session);

	/**
	 * The session this transport is used with.
	 *
	 * @return the session this transport is attached to
	 */
	S getHandler();

	/**
	 * Connect to a host (or resource) using the URI provided in the
	 * {@link ResourceProfile}. The URI will supply such details as host, user
	 * etc. Any protocol specific authentication will also be performed.
	 *
	 * @param profile profile
	 * @param parentUIComponent TODO
	 * @return <code>true</code> if connected OK
	 * @throws ProfileException on any connection error
	 * @throws AuthenticationException if authentication fails
	 */
	boolean connect(ResourceProfile<? extends ProfileTransport<S>> profile, Object parentUIComponent)
			throws ProfileException, AuthenticationException;

	/**
	 * Disconnect from the currently connected resource. If a connection is not
	 * currently being maintained, an <code>IOException</code> will be thrown.
	 *
	 * @throws IOException on any disconnection error
	 */
	void disconnect() throws IOException;

	/**
	 * Get if a connection is currently being maintained.
	 *
	 * @return connected
	 */
	boolean isConnected();

	/**
	 * Get if a connection is currently pending, e.g. unauthenticated.
	 *
	 * @return connection pending
	 */
	boolean isConnectionPending();

	/**
	 * Return the underlying provider of the terminal. This should be used to
	 * return the underlying object that creates and maintains the terminal
	 * transport, its purpose is to allow additional features such as SFTP to be
	 * implemented in the terminal.
	 * 
	 * @return provider
	 */
	Object getProvider();

	/**
	 * Some transports may be capable of cloning the current virtual session
	 * (SSH for example). Such transports should return <code>true</code> for
	 * this method.
	 *
	 * @return transport supports cloning
	 */
	boolean isCloneTransportSupported();

	/**
	 * Get the profile that was used to connect this transport. This will be
	 * <code>null</code> if disconnected.
	 *
	 * @return profile
	 */
	ResourceProfile<? extends ProfileTransport<S>> getProfile();

	/**
	 * If the host has provided some information about itself, this method will
	 * return it. Otherwise <code>null</code> will be returned.
	 *
	 * @return host description
	 */
	String getHostDescription();

	/**
	 * Return a short description of the protocol, for example "Telnet" or
	 * "SSH2"
	 * 
	 * @return protocol description
	 */
	String getProtocolDescription();

	/**
	 * Is the protocol secure?
	 * 
	 * @return secure transport
	 */
	boolean isProtocolSecure();

	/**
	 * Return a short description of the transport, for example "Socket" or
	 * "SOCKS5"
	 * 
	 * @return description
	 */
	String getTransportDescription();

	/**
	 * Is the transport layer secure?
	 * 
	 * @return secure transport
	 */
	boolean isTransportSecure();

	/**
	 * Clone the current session. The current connection should be cloned and a
	 * new instance of the appropriate transport will be returned.
	 *
	 * @param session the session that is going to manage this newly cloned
	 *            connection
	 * @return the cloned connection
	 * @throws CloneNotSupportedException if cloning cannot take place
	 * @throws ProfileException on any errors that may occur during cloning.
	 */
	ProfileTransport<S> cloneTransport(S session) throws CloneNotSupportedException, ProfileException;
}
