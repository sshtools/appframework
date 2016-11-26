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