/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/* HEADER*/
package com.sshtools.profile;

import java.io.IOException;

import com.sshtools.virtualsession.VirtualSession;

/**
 * ProfileTransport implementations are responsible for creating and
 * maintaing a connection to a resource. The resource {@link URI} is
 * determined from the {@link ResourceProfile} provided when
 * <code>connect()</code> is called.
 *
 * @author $Author: brett $
 */

public interface ProfileTransport {

  /**
   * Connect to a host (or resource) using the URI provided
   * in the ResourceProfile}. The URI will supply such details as host,
   * user etc. Any protocol specific authentication will also be
   * performed.
   *
   * @param profile profile
 * @param parentUIComponent TODO
   * @return <code>true</code> if connected OK
   * @throws ProfileException on any connection error
   * @throws AuthenticationException if authentication fails
   */
  public boolean connect(ResourceProfile profile, Object parentUIComponent) throws ProfileException,
      AuthenticationException;

  /**
   * Disconnect from the currently connected resource. If a connection
   * is not currently being maintained, an <code>IOException</code> will
   * be thrown.
   *
   * @throws IOException on any disconnection error
   */
  public void disconnect() throws IOException;

  /**
   * Get if a connection is currently being maintained.
   *
   * @return connected
   */
  public boolean isConnected();

  /**
   * Get if a connection is currently pending, e.g. unauthenticated.
   *
   * @return connection pending
   */
  public boolean isConnectionPending();

  /**
   * Return the underlying provider of the terminal. This should be used
   * to return the underlying object that creates and maintains the terminal
   * transport, its purpose is to allow additional features such as SFTP to
   * be implemented in the terminal.
   * @return
   */
  public Object getProvider();

  /**
   * Some transports may be capable of cloning the current virtual session (SSH
   * for example). Such transports should return <code>true</code> for this
   * method.
   *
   * @return transport supports cloning
   */
  public boolean isCloneVirtualSessionSupported();

  /**
   * Get the profile that was used to connect this transport. This will be
   * <code>null</code> if disconnected.
   *
   * @return profile
   */
  public ResourceProfile getProfile();

  /**
   * If the host has provided some information about itself, this method
   * will return it. Otherwise <code>null</code> will be returned.
   *
   * @return host description
   */
  public String getHostDescription();


  /**
   * Return a short description of the protocol, for example "Telnet" or "SSH2"
   * @return
   */
  public String getProtocolDescription();

  /**
   * Is the protocol secure?
   * @return
   */
  public boolean isProtocolSecure();


  /**
   * Return a short description of the transport, for example "Socket" or "SOCKS5"
   * @return
   */
  public String getTransportDescription();

  /**
   * Is this transport secure?
   * @return
   */
  public boolean isTransportSecure();

  /**
   * Clone the current virtual session. The current connection should be
   * cloned and a new instance of the appropriate transport will be returned.
   *
   * @param session the virtual session that is going to manage this newly cloned connection
   * @return the cloned connection
   * @throws CloneNotSupportedException if cloning cannot take place
   * @throws ProfileException on any errors that may occur during cloning.
   */
  public ProfileTransport cloneVirtualSession(VirtualSession session) throws
      CloneNotSupportedException, ProfileException;
}
