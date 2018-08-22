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
 * Listener interface to implement to receive events from the
 * {@link VirtualSessionManager} about changes in state of the
 * {@link VirtualSession}s it is managing.
 */
public interface VirtualSessionManagerListener extends EventListener {
	/**
	 * The virtual session has changed in some way.
	 *
	 * @param session session that changed
	 */
	void virtualSessionChanged(VirtualSession<?, ?> session);

	/**
	 * A virtual session has been added to the virtual session manager
	 *
	 * @param session session the has been added
	 */
	void virtualSessionAdded(VirtualSession<?, ?> session);

	/**
	 * A virtual session has been removed from the virtual session manager
	 *
	 * @param session session the has been removed
	 */
	void virtualSessionRemoved(VirtualSession<?, ?> session);

	/**
	 * A virtual session has been selected by the user
	 *
	 * @param session session selected
	 */
	void virtualSessionSelected(VirtualSession<?, ?> session);

	/**
	 * A virtual session has been deselected.
	 *
	 * @param session the session deselected
	 */
	void virtualSessionDeselected(VirtualSession<?, ?> session);
}
