/*
 *  SSHTools - Java SSH2 API
 *
 *  Copyright (C) 2002 Lee David Painter.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  You may also distribute it and/or modify it under the terms of the
 *  Apache style J2SSH Software License. A copy of which should have
 *  been provided with the distribution.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  License document supplied with your distribution for more details.
 *
 */

package com.sshtools.appframework.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.sshtools.appframework.api.ui.AbstractSshToolsApplicationClientPanel;
import com.sshtools.appframework.api.ui.SshToolsApplicationClientPanel;
import com.sshtools.appframework.util.IOUtil;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.profile.URI;
import com.sshtools.ui.swing.OptionDialog;

/**
 * 
 * 
 * @author $author$
 */

public abstract class SshToolsApplicationClientApplet

extends SshToolsApplicationApplet {

	/** Applet parameter info */

	public final static String[][] CLIENT_PARAMETER_INFO = {
			{ "sshapps.connection.url", "string",
					"The URL of a connection profile to open" },
			{ "sshapps.connection.hostURI", "string",
					"The URI of the host to connect to" },
			{ "sshapps.connection.authenticationMethod", "string",
					"Authentication method. password,publickey etc." },
			{ "sshapps.connection.connectImmediately", "boolean",
					"Connect immediately." },
			{ "sshapps.connection.showConnectionDialog", "boolean",
					"Show connection dialog." },
			{ "sshapps.connection.disableHostKeyVerification", "boolean",
					"Disable the host key verification dialog." } };

	// Private instance variables

	private String connectionProfileLocation;

	private String authenticationMethod;

	private URI uri;

	private boolean connectImmediately;

	private boolean showConnectionDialog;

	private boolean disableHostKeyVerification;

	private ResourceProfile profile;

	/**
	 * 
	 * 
	 * @throws IOException
	 */

	public void initApplet() throws IOException {
		super.initApplet();
		connectionProfileLocation = getParameter(
				"sshapps.connectionProfile.url", "");
		// Get the connection parameters
		uri = new URI(getParameter("sshapps.connection.host", ""));
		authenticationMethod = getParameter(
				"sshapps.connection.authenticationMethod", "");
		connectImmediately = getParameter(
				"sshapps.connection.connectImmediately", "false")
				.equalsIgnoreCase("true");
		showConnectionDialog = getParameter(
				"sshapps.connection.showConnectionDialog", "false")
				.equalsIgnoreCase("true");
		disableHostKeyVerification = getParameter(
				"sshapps.connection.disableHostKeyVerification", "false")
				.equalsIgnoreCase("true");
		buildProfile();

	}

	/**
     * 
     */

	public void startApplet() {
		// Disable the host key verification if requested
		if (connectImmediately) {
			try {
				getLoadingPanel().setStatus("Connecting");
				if (showConnectionDialog) {
					ResourceProfile newProfile = ((SshToolsApplicationClientPanel) getApplicationPanel())
							.newConnectionProfile(profile);
					if (newProfile != null) {
						profile = newProfile;
						((SshToolsApplicationClientPanel) getApplicationPanel())
								.connect(profile, true);
					}
				} else {
					((SshToolsApplicationClientPanel) getApplicationPanel())
							.connect(profile, false);
				}
			} catch (Exception e) {
				OptionDialog.error(this, "Error", e);
			}
		}

	}

	/**
	 * 
	 * 
	 * @throws IOException
	 */

	protected void buildProfile() throws IOException {
		profile = new ResourceProfile();
		// Load the connection profile if specified
		if (!connectionProfileLocation.equals("")) {
			getLoadingPanel().setStatus("Loading connection profile");
			InputStream in = null;
			try {
				URL u = null;
				try {
					u = new URL(connectionProfileLocation);
				} catch (MalformedURLException murle) {
					u = new URL(getCodeBase() + "/" + connectionProfileLocation);
				}
				in = u.openStream();
				profile.load(in);
			} finally {
				IOUtil.closeStream(in);
			}
		}
		if (uri != null) {
			profile.setURI(uri);
			// TODO reimplement
			// if (!authenticationMethod.equals("")) {
			// try {
			// profile.addAuthenticationMethod(authenticationMethod);
			// }
			// catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
		}

	}

	/**
     * 
     */

	public void destroy() {
		if (((AbstractSshToolsApplicationClientPanel) getApplicationPanel())
				.isConnected()) {
			((SshToolsApplicationClientPanel) getApplicationPanel())
					.closeConnection(true);
		}

	}

	/**
	 * 
	 * 
	 * @return
	 */

	public String[][] getParameterInfo() {
		String[][] s = super.getParameterInfo();
		String[][] p = new String[s.length + CLIENT_PARAMETER_INFO.length][];
		System.arraycopy(s, 0, p, 0, s.length);
		System.arraycopy(CLIENT_PARAMETER_INFO, 0, p, s.length,
				CLIENT_PARAMETER_INFO.length);
		return p;

	}

}
