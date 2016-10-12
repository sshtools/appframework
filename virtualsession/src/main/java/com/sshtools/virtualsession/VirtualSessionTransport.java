/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/* HEADER */
package com.sshtools.virtualsession;

import com.sshtools.profile.ProfileTransport;

/**
 * The transport mechanism used by a {@link com.sshtools.virtualsession.VirtualSession}
 * should implement this interface. This simply extends {@link com.sshtools.profile.ProfileTransport}
 * to provide additional session related methods.
 *
 * @author Brett
 */
public interface VirtualSessionTransport
    extends ProfileTransport {

  /**
   * Get if the transport is currently connected to a host
   *
   * @return connected
   */
  public boolean isConnected();

  /**
   * Initialise the transport. Should be called just after instantiation.
   *
   * @param session virtual session
   */

  public void init(VirtualSession session);

  /**
   * Get the virtual session for this connection
   *
   * @return virtual session
   */
  public VirtualSession getVirtualSession();


  /**
   * Return <code>true</code> if a connection is due to be made
   *
   * @return connection pending
   */
  public boolean isConnectionPending();

}