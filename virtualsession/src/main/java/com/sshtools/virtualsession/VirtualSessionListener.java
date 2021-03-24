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
	default void titleChanged(VirtualSession<?, ?> session, String title) {
	}

	/**
	 * A connection to a host has been made.
	 * 
	 * @param session virtual session connected to.
	 */
	default void connected(VirtualSession<?, ?> session) {
	}

	/**
	 * A connection to a host has been broken
	 * 
	 * @param session virtual session
	 * @param exception cause of disconnect (or <code>null</code> for a normal
	 *            disconnect)
	 */
	default void disconnected(VirtualSession<?, ?> session, Throwable exception) {
	}

	/**
	 * The session has sent data to the host
	 * 
	 * @param session virtual session
	 * @param data data
	 * @param len length of data
	 */
	default void dataSent(VirtualSession<?, ?> session, byte[] data, int len) {
	}

	/**
	 * The host has sent the session data
	 * 
	 * @param session virtual session
	 * @param data data
	 * @param len length of data
	 */
	default void dataReceived(VirtualSession<?, ?> session, byte[] data, int len) {
	}
}
