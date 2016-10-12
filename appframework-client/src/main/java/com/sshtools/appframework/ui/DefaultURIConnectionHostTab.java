/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/* HEADER */
package com.sshtools.appframework.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.sshtools.appframework.api.ui.NumericTextField;
import com.sshtools.appframework.api.ui.SshToolsConnectionTab;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.profile.SchemeOptions;
import com.sshtools.profile.URI;
import com.sshtools.profile.URI.MalformedURIException;
import com.sshtools.ui.swing.UIUtil;
import com.sshtools.ui.swing.XTextField;

public class DefaultURIConnectionHostTab extends JPanel implements
		SshToolsConnectionTab {
	private static final long serialVersionUID = 1L;

	public final static int OMIT = 0;
	public final static int REQUIRED = 1;
	public final static int OPTIONAL = 2;

	public static final Icon LARGE_CONNECT_ICON = IconStore.getInstance()
			.getIcon("network-server", 32);
	public static final Icon CONNECT_ICON = IconStore.getInstance().getIcon(
			"network-server", 24);

	// Private instance variables
	protected XTextField hostnameField = new XTextField();
	protected XTextField pathField = new XTextField();
	protected XTextField userField = new XTextField();
	protected NumericTextField portField = new NumericTextField(new Integer(0),
			new Integer(65535), new Integer(0));
	
	private ResourceProfile profile;
	private int defaultPort;
	private String scheme;
	private int showHost;
	private int showPort;
	private int showPath;
	private int showUser;
	private Icon smallIcon;
	private Icon largeIcon;
	private String title;
	private String category;
	private char mnemonic;
	private String toolTipText;
	private boolean focusedDefault;
	private String defaultPath;

	/**
	 * Creates a new DefaultURIConnectionHostTab object.
	 * 
	 * @param scheme
	 *            scheme
	 * @param showHost
	 *            show the host option
	 * @param showPort
	 *            show the port option
	 * @param defaultPort
	 *            the default port
	 * @param showPath
	 *            show the path portion
	 * @param showUser
	 *            show the user portion
	 */
	public DefaultURIConnectionHostTab(String scheme, int showHost,
			int showPort, int defaultPort, int showPath, int showUser) {
		this(scheme, showHost, showPort, defaultPort, showPath, showUser,
				LARGE_CONNECT_ICON, CONNECT_ICON, "Connection", "Host",
				"The main host connection details.", 'h');
	}

	public String getDefaultPath() {
		return defaultPath;
	}

	public void setDefaultPath(String defaultPath) {
		this.defaultPath = defaultPath;
	}

	public XTextField getHostnameField() {
		return hostnameField;
	}

	public XTextField getPathField() {
		return pathField;
	}

	public XTextField getUserField() {
		return userField;
	}

	public NumericTextField getPortField() {
		return portField;
	}

	/**
	 * Creates a new DefaultURIConnectionHostTab object.
	 * 
	 * @param scheme
	 *            scheme
	 * @param showHost
	 *            show the host option
	 * @param showPort
	 *            show the port option
	 * @param defaultPort
	 *            the default port
	 * @param showPath
	 *            show the path portion
	 * @param showUser
	 *            show the user portion
	 * @param largeIcon
	 *            large icon
	 * @param smallIcon
	 *            small icon
	 * @param category
	 *            category
	 * @param title
	 *            title
	 * @param toolTipText
	 *            tool tip text
	 * @param mnemonic
	 *            mnemonic
	 */
	public DefaultURIConnectionHostTab(String scheme, int showHost,
			int showPort, int defaultPort, int showPath, int showUser,
			Icon largeIcon, Icon smallIcon, String category, String title,
			String toolTipText, char mnemonic) {
		super();
		this.toolTipText = toolTipText;
		this.mnemonic = mnemonic;
		this.category = category;
		this.title = title;
		this.largeIcon = largeIcon;
		this.smallIcon = smallIcon;
		this.defaultPort = defaultPort;
		this.showHost = showHost;
		this.showPort = showPort;
		this.showPath = showPath;
		this.showUser = showUser;
		this.scheme = scheme;
		// Create the main connection details panel
		JPanel mainConnectionDetailsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(0, 2, 2, 2);
		gbc.weightx = 1.0;
		// Host name
		if (showHost != OMIT) {
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, new JLabel(
					getHostnameText()), gbc, GridBagConstraints.REMAINDER);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, hostnameField, gbc,
					GridBagConstraints.REMAINDER);
			focusOn(hostnameField);
		}
		// Port
		if (showPort != OMIT) {
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, new JLabel(
					getPortText()), gbc, GridBagConstraints.REMAINDER);
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, portField, gbc,
					GridBagConstraints.REMAINDER);
			focusOn(portField);
		}
		// Path
		if (showPath != OMIT) {
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, new JLabel(
					getPathText()), gbc, GridBagConstraints.REMAINDER);
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, pathField, gbc,
					GridBagConstraints.REMAINDER);
			focusOn(pathField);
		}
		// User
		if (showUser != OMIT) {
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, new JLabel(
					getUserText()), gbc, GridBagConstraints.REMAINDER);
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, userField, gbc,
					GridBagConstraints.REMAINDER);
			focusOn(userField);
		}
		//
		gbc.weighty = 1.0;
		UIUtil.jGridBagAdd(mainConnectionDetailsPanel,
				createAdditionalComponent(), gbc, GridBagConstraints.REMAINDER);
		//
		// This panel
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		UIUtil.jGridBagAdd(this, mainConnectionDetailsPanel, gbc,
				GridBagConstraints.REMAINDER);
	}

	protected String getHostnameText() {
		return "Hostname";
	}

	protected String getPortText() {
		return "Port";
	}

	protected String getUserText() {
		return "User";
	}

	protected String getPathText() {
		return "Path";
	}

	protected void focusOn(JComponent component) {
		if (!focusedDefault) {
			component.requestFocusInWindow();
			focusedDefault = true;
		}
	}

	protected JComponent createAdditionalComponent() {
		return new JLabel();
	}

	public void setConnectionProfile(ResourceProfile profile) {
		this.profile = profile;
		setBasicFieldsFromProfile(profile);
	}

	protected void setBasicFieldsFromProfile(ResourceProfile profile) {
		if (showHost != OMIT) {
			hostnameField.setText(profile == null || profile.getURI() == null
					|| profile.getURI().getHost() == null ? "" : profile
					.getURI().getHost());
		}
		if (showPort != OMIT) {
			portField.setText(String.valueOf(profile == null
					|| profile.getURI() == null
					|| profile.getURI().getPort() == -1 ? defaultPort : profile
					.getURI().getPort()));
		}
		if (showPath != OMIT) {
			String path = getPathValue(profile);
			pathField.setText(path);
		}
		if (showUser != OMIT) {
			userField.setText(profile == null ? "" : profile.getUsername());
		}
	}

	protected String getPathValue(ResourceProfile profile) {
		String path = String.valueOf(profile == null
				|| profile.getURI() == null
				|| profile.getURI().getPath() == null ? "" : profile
				.getURI().getPath());
		path = path.equals("") ? defaultPath : path;
		return path;
	}

	@Override
	public boolean requestFocusInWindow() {
		if (showHost != OMIT) {
			return hostnameField.requestFocusInWindow();
		} else if (showPort != OMIT) {
			return portField.requestFocusInWindow();
		}
		if (showPath != OMIT) {
			return pathField.requestFocusInWindow();
		}
		if (showUser != OMIT) {
			return userField.requestFocusInWindow();
		}
		return super.requestFocusInWindow();
	}

	@Override
	protected boolean requestFocusInWindow(boolean arg0) {
		// TODO Auto-generated method stub
		return super.requestFocusInWindow(arg0);
	}

	public void setSchemeOptions(SchemeOptions options) {
	}

	public ResourceProfile getConnectionProfile() {
		return profile;
	}

	public String getTabCategory() {
		return category;
	}

	public Icon getTabIcon() {
		return smallIcon;
	}

	public Icon getTabLargeIcon() {
		return largeIcon;
	}

	public String getTabTitle() {
		return title;
	}

	public String getTabToolTipText() {
		return toolTipText;
	}

	public int getTabMnemonic() {
		return mnemonic;
	}

	public Component getTabComponent() {
		return this;
	}

	public boolean validateTab() {
		// Validate that we have enough information
		if ((showHost == REQUIRED && hostnameField.getText().trim().equals(""))
				|| (showPort == REQUIRED && portField.getValue().intValue() == 0)
				|| (showPath == REQUIRED && pathField.getText().trim()
						.equals(""))
				|| (showUser == REQUIRED && userField.getText().trim()
						.equals(""))) {
			JOptionPane.showMessageDialog(this, "Please enter all details!",
					"Connect", JOptionPane.OK_OPTION);
			return false;
		}
		return true;
	}

	public void applyTab() {
		applyTab(profile);
	}

	public void applyTab(ResourceProfile profile) {
		try {
			URI uri = getURIForSettings();
			profile.setURI(uri);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	protected URI getURIForSettings() throws MalformedURIException {
		URI uri = new URI(scheme + "://");
		if (showHost != OMIT && !hostnameField.getText().trim().equals("")) {
			uri.setHost(hostnameField.getText().trim());
		}
		if (showPort == REQUIRED
				|| (showPort == OPTIONAL && portField.getValue().intValue() > 0)) {
			int portValue = Integer.valueOf(portField.getText()).intValue();
			if (portValue == defaultPort) {
				uri.setPort(-1);
			} else {
				uri.setPort(portValue);
			}
		}
		String path = pathField.getText().trim();
		if (showPath != OMIT && !path.equals("")) {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			uri.setPath(path);
		}
		if (showUser != OMIT && !userField.getText().trim().equals("")) {
			try {
				uri.setUserinfo(URLEncoder.encode(userField.getText().trim(),
						"UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return uri;
	}

	public void tabSelected() {
		hostnameField.requestFocus();
	}

}