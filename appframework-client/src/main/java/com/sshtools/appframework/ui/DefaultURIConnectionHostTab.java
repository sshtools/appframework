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
/* HEADER */
package com.sshtools.appframework.ui;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;

import com.sshtools.appframework.api.ui.NumericTextField;
import com.sshtools.appframework.api.ui.SshToolsConnectionTab;
import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.profile.SchemeOptions;
import com.sshtools.profile.URI;
import com.sshtools.profile.URI.MalformedURIException;
import com.sshtools.ui.swing.TabValidationException;
import com.sshtools.ui.swing.XTextField;

import net.miginfocom.swing.MigLayout;

public class DefaultURIConnectionHostTab<T extends ProfileTransport<?>> extends JPanel implements SshToolsConnectionTab<T> {
	public static final Icon CONNECT_ICON = IconStore.getInstance().getIcon(BootstrapIcons.SERVER, 24);
	public static final Icon LARGE_CONNECT_ICON = IconStore.getInstance().getIcon(BootstrapIcons.SERVER, 32);
	public final static int OMIT = 0;
	public final static int OPTIONAL = 2;
	public final static int REQUIRED = 1;
	private static final long serialVersionUID = 1L;
	// Private instance variables
	protected XTextField hostnameField = new XTextField();
	protected XTextField pathField = new XTextField();
	protected NumericTextField portField = new NumericTextField(Integer.valueOf(0), Integer.valueOf(65535), Integer.valueOf(0));
	protected JTextField userField = new XTextField();
	private String category;
	private String defaultPath;
	private int defaultPort;
	private boolean focusedDefault;
	private Icon largeIcon;
	private char mnemonic;
	private ResourceProfile<T> profile;
	private String scheme;
	private int showHost;
	private int showPath;
	private int showPort;
	private int showUser;
	private Icon smallIcon;
	private String title;
	private String toolTipText;

	/**
	 * Creates a new DefaultURIConnectionHostTab object.
	 * 
	 * @param scheme scheme
	 * @param showHost show the host option
	 * @param showPort show the port option
	 * @param defaultPort the default port
	 * @param showPath show the path portion
	 * @param showUser show the user portion
	 */
	public DefaultURIConnectionHostTab(String scheme, int showHost, int showPort, int defaultPort, int showPath, int showUser) {
		this(scheme, showHost, showPort, defaultPort, showPath, showUser, LARGE_CONNECT_ICON, CONNECT_ICON, "Connection", "Host",
				"The main host connection details.", 'h');
	}

	/**
	 * Creates a new DefaultURIConnectionHostTab object.
	 * 
	 * @param scheme scheme
	 * @param showHost show the host option
	 * @param showPort show the port option
	 * @param defaultPort the default port
	 * @param showPath show the path portion
	 * @param showUser show the user portion
	 * @param largeIcon large icon
	 * @param smallIcon small icon
	 * @param category category
	 * @param title title
	 * @param toolTipText tool tip text
	 * @param mnemonic mnemonic
	 */
	public DefaultURIConnectionHostTab(String scheme, int showHost, int showPort, int defaultPort, int showPath, int showUser,
			Icon largeIcon, Icon smallIcon, String category, String title, String toolTipText, char mnemonic) {
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
		setLayout(new MigLayout("wrap 1", "14[fill, grow]", "[]"));
		// Host name
		addHostnameField(showHost);
		// Port
		addPortField(showPort);
		// Path
		addPathField(showPath);
		// User
		addUserField(showUser);
		//
		add(createAdditionalComponent());
	}

	@Override
	public void applyTab() {
		applyTab(profile);
	}

	@Override
	public void applyTab(ResourceProfile<T> profile) {
		try {
			URI uri = getURIForSettings();
			profile.setURI(uri);
			setupProfile(profile);
		} finally {
			setAvailable();
		}
	}

	@Override
	public ResourceProfile<T> getConnectionProfile() {
		return profile;
	}

	public String getDefaultPath() {
		return defaultPath;
	}

	public XTextField getHostnameField() {
		return hostnameField;
	}

	public XTextField getPathField() {
		return pathField;
	}

	public NumericTextField getPortField() {
		return portField;
	}

	@Override
	public String getTabCategory() {
		return category;
	}

	@Override
	public Component getTabComponent() {
		return this;
	}

	@Override
	public Icon getTabIcon() {
		return smallIcon;
	}

	@Override
	public Icon getTabLargeIcon() {
		return largeIcon;
	}

	@Override
	public int getTabMnemonic() {
		return mnemonic;
	}

	@Override
	public String getTabTitle() {
		return title;
	}

	@Override
	public String getTabToolTipText() {
		return toolTipText;
	}

	public JTextField getUserField() {
		return userField;
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
	public void setConnectionProfile(ResourceProfile<T> profile) {
		this.profile = profile;
		setBasicFieldsFromProfile(profile);
		setAvailable();
	}

	public void setDefaultPath(String defaultPath) {
		this.defaultPath = defaultPath;
	}

	public void setSchemeOptions(SchemeOptions options) {
	}

	@Override
	public void tabSelected() {
		hostnameField.requestFocus();
	}

	@Override
	public boolean validateTab() {
		// Validate that we have enough information
		if (showHost == REQUIRED && hostnameField.getText().trim().equals(""))
			throw new TabValidationException(this, hostnameField);
		if (showPort == REQUIRED && portField.getValue().intValue() == 0)
			throw new TabValidationException(this, portField);
		if (showPath == REQUIRED && pathField.getText().trim().equals(""))
			throw new TabValidationException(this, pathField);
		if (showUser == REQUIRED && getUserValue().equals(""))
			throw new TabValidationException(this, userField);
		return true;
	}

	protected void addHostnameField(int showHost) {
		if (showHost != OMIT) {
			add(new JLabel(getHostnameText()));
			add(hostnameField);
			focusOn(hostnameField);
		}
	}

	protected void addPathField(int showPath) {
		if (showPath != OMIT) {
			add(new JLabel(getPathText()));
			add(pathField);
			focusOn(pathField);
		}
	}

	protected void addPortField(int showPort) {
		if (showPort != OMIT) {
			add(new JLabel(getPortText()));
			add(portField);
			focusOn(portField);
		}
	}

	protected void addUserField(int showUser) {
		if (showUser != OMIT) {
			add(new JLabel(getUserText()));
			add(userField);
			focusOn(userField);
		}
	}

	protected JComponent createAdditionalComponent() {
		return new JLabel();
	}

	protected void focusOn(JComponent component) {
		if (!focusedDefault) {
			component.requestFocusInWindow();
			focusedDefault = true;
		}
	}

	protected String getHostnameText() {
		return "Hostname";
	}

	protected String getPathText() {
		return "Path";
	}

	protected String getPathValue(ResourceProfile<T> profile) {
		String path = String.valueOf(profile == null || profile.getURI() == null || profile.getURI().getPath() == null ? ""
				: profile.getURI().getPath());
		path = path.equals("") ? defaultPath : path;
		return path;
	}

	protected String getPortText() {
		return "Port";
	}

	protected URI getURIForSettings() throws TabValidationException {
		URI uri;
		try {
			uri = new URI(scheme + "://");
			processHostUriPortion(uri);
		} catch (MalformedURIException e) {
			throw new TabValidationException(this, hostnameField);
		}
		try {
			processPortUriPortion(uri);
		} catch (MalformedURIException e) {
			throw new TabValidationException(this, portField);
		}
		try {
			processPathUriPortion(uri);
		} catch (MalformedURIException e) {
			throw new TabValidationException(this, pathField);
		}
		try {
			processUserUriPortion(uri);
		} catch (MalformedURIException e) {
			throw new TabValidationException(this, userField);
		}
		return uri;
	}

	protected String getUserText() {
		return "User";
	}

	protected String getUserValue() {
		return userField.getText().trim();
	}

	protected void processHostUriPortion(URI uri) throws MalformedURIException {
		if (showHost != OMIT && !hostnameField.getText().trim().equals("")) {
			uri.setHost(hostnameField.getText().trim());
		}
	}

	protected void processPathUriPortion(URI uri) throws MalformedURIException {
		String path = pathField.getText().trim();
		if (showPath != OMIT && !path.equals("")) {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			uri.setPath(path);
		}
	}

	protected void processPortUriPortion(URI uri) throws MalformedURIException {
		if (hostnameField.getText().trim().length() > 0
				&& (showPort == REQUIRED || (showPort == OPTIONAL && portField.getValue().intValue() > 0))) {
			int portValue = Integer.valueOf(portField.getText()).intValue();
			if (portValue == defaultPort) {
				uri.setPort(-1);
			} else {
				uri.setPort(portValue);
			}
		}
	}

	protected void processUserUriPortion(URI uri) throws MalformedURIException {
		if (showUser != OMIT && !getUserValue().equals("")) {
			try {
				uri.setUserinfo(URLEncoder.encode(getUserValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
	}

	protected void setAvailable() {
	}

	protected void setBasicFieldsFromProfile(ResourceProfile<T> profile) {
		if (showHost != OMIT) {
			hostnameField.setText(profile == null || profile.getURI() == null || profile.getURI().getHost() == null ? ""
					: profile.getURI().getHost());
		}
		if (showPort != OMIT) {
			portField.setText(
					String.valueOf(profile == null || profile.getURI() == null || profile.getURI().getPort() == -1 ? defaultPort
							: profile.getURI().getPort()));
		}
		if (showPath != OMIT) {
			String path = getPathValue(profile);
			pathField.setText(path);
		}
		if (showUser != OMIT) {
			userField.setText(profile == null || profile.getUsername() == null ? "" : profile.getUsername());
		}
	}

	protected void setupProfile(ResourceProfile<T> profile) throws TabValidationException {
	}
}