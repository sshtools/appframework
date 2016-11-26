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