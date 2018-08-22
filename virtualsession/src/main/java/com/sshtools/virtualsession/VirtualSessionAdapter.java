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
 * Convenient adapter implementation of a {@link VirtualSessionListener}.
 */
public abstract class VirtualSessionAdapter implements VirtualSessionListener {
	@Override
	public void connected(VirtualSession<?, ?> session) {
	}

	@Override
	public void dataReceived(VirtualSession<?, ?> session, byte[] data, int len) {
	}

	@Override
	public void dataSent(VirtualSession<?, ?> session, byte[] data, int len) {
	}

	@Override
	public void disconnected(VirtualSession<?, ?> session, Throwable exception) {
	}

	@Override
	public void titleChanged(VirtualSession<?, ?> session, String title) {
	}
}
