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

/**
 * Convenient adapter for VirtualSessionManagerListener
 */
public abstract class VirtualSessionManagerAdapter implements VirtualSessionManagerListener {
	@Override
	public void virtualSessionChanged(VirtualSession<?, ?> session) {
	}

	@Override
	public void virtualSessionAdded(VirtualSession<?, ?> session) {
	}

	@Override
	public void virtualSessionRemoved(VirtualSession<?, ?> session) {
	}

	@Override
	public void virtualSessionSelected(VirtualSession<?, ?> session) {
	}

	@Override
	public void virtualSessionDeselected(VirtualSession<?, ?> session) {
	}
}
