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
/* HEADER*/
package com.sshtools.profile;

/**
 * Listener interface to be implemented when you need to watch for changes to a
 * {@link ResourceProfile}.
 *
 * @author $Author: brett $
 */
public interface ResourceProfileListener {
	/**
	 * Invoked when something in the profile changes.
	 */
	public void profileChanged();

	/**
	 * Invoked when the profile is loaded
	 */
	public void profileLoaded();

	/**
	 * Invoked when the profile is saved
	 */
	public void profileSaved();
}
