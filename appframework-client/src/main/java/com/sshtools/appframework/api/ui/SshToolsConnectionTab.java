/**
 * Maverick Client Application Framework - Framework for 'client' applications.
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
package com.sshtools.appframework.api.ui;

import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.ui.swing.Tab;

public interface SshToolsConnectionTab<T extends ProfileTransport<?>> extends Tab {

	/**
	 * Apply to a different profile
	 * 
	 * @param profile
	 */
	void applyTab(ResourceProfile<T> profile);

	/**
	 * Get the profile this tab is configured from
	 * 
	 * @return tab
	 */
	ResourceProfile<T> getConnectionProfile();

	/**
	 * Set the connection profile this tab is configured from.
	 * 
	 * @param profile profile
	 */
	void setConnectionProfile(ResourceProfile<T> profile);

}