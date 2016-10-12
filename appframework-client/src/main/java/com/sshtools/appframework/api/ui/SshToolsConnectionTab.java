/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.api.ui;

import com.sshtools.profile.ResourceProfile;
import com.sshtools.ui.swing.Tab;

public interface SshToolsConnectionTab extends Tab {

	/**
	 * Set the connection profile this tab is configured from.
	 * 
	 * @param profile profile
	 */
	void setConnectionProfile(ResourceProfile profile);

	/**
	 * Apply to a different profile
	 * 
	 * @param profile
	 */
	void applyTab(ResourceProfile profile);

	/**
	 * Get the profile this tab is configured from
	 * 
	 * @return tab
	 */
	ResourceProfile getConnectionProfile();

}