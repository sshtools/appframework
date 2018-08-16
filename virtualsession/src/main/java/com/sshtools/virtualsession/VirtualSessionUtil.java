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

import com.sshtools.profile.URI;

/**
 * Utilities for VirtualSessions.
 */
public class VirtualSessionUtil {
	/**
	 * Get a title appropriate for a virtual session based on its current
	 * connection state
	 * 
	 * @param session session
	 * @return session title
	 */
	public static String getTitleForVirtualSession(VirtualSession<?, ?> session) {
		if (session == null) {
			return "No session";
		}
		StringBuffer title = new StringBuffer();
		if (session.getTransport() != null && session.getTransport().getProfile() != null) {
			URI uri = session.getTransport().getProfile().getURI();
			if (uri.getHost() != null) {
				title.append(uri.getHost());
				if (uri.getPort() != -1 && uri.getPort() != 22) {
					title.append(':');
					title.append(uri.getPort());
				}
			} else {
				title.append("<New>");
			}
		} else {
			if (session.getTransport() != null && session.getTransport().isConnectionPending()) {
				title.append("<Connecting>");
				if (session.getTransport().getHostDescription() != null && session.getTransport().getHostDescription().equals("")) {
					title.append(" to " + session.getTransport().getHostDescription());
				}
			} else {
				title.append("<Disconnected>");
			}
		}
		return title.toString();
	}
}