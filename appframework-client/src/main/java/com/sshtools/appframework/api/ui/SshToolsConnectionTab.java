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