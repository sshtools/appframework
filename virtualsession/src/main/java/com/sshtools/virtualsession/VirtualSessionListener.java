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

import java.util.EventListener;

/**
 * Implementations receive events about activity in the virtual sessions, such
 * as connection events, resizes, data sent and received.
 * 
 * @author $Author: brett $
 */

public interface VirtualSessionListener extends EventListener {

	/**
	 * The title of the virtual session has changed.
	 * 
	 * @param session session
	 * @param title title
	 */
	public void titleChanged(VirtualSession session, String title);

	/**
	 * A connection to a host has been made.
	 * 
	 * @param session virtual session connected to.
	 */
	public void connected(VirtualSession session);

	/**
	 * A connection to a host has been broken
	 * 
	 * @param session virtual session
	 * @param exception cause of disconnect (or <code>null</code> for a normal
	 *            disconnect)
	 */
	public void disconnected(VirtualSession session, Throwable exception);

	/**
	 * The session has sent data to the host
	 * 
	 * @param session virtual session
	 * @param data data
	 * @param len length of data
	 */
	public void dataSent(VirtualSession session, byte[] data, int len);

	/**
	 * The host has sent the session data
	 * 
	 * @param session virtual session
	 * @param data data
	 * @param len length of data
	 */
	public void dataReceived(VirtualSession session, byte[] data, int len);
}
