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
import javax.swing.JTextField;

import com.sshtools.appframework.api.ui.NumericTextField;
import com.sshtools.appframework.api.ui.SshToolsConnectionTab;
import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.profile.SchemeOptions;
import com.sshtools.profile.URI;
import com.sshtools.profile.URI.MalformedURIException;
import com.sshtools.ui.swing.UIUtil;
import com.sshtools.ui.swing.XTextField;

public class DefaultURIConnectionHostTab<T extends ProfileTransport<?>> extends JPanel implements SshToolsConnectionTab<T> {
	public static final Icon CONNECT_ICON = IconStore.getInstance().getIcon("network-server", 24);

	public static final Icon LARGE_CONNECT_ICON = IconStore.getInstance().getIcon("network-server", 32);
	public final static int OMIT = 0;
	public final static int OPTIONAL = 2;

	public final static int REQUIRED = 1;
	private static final long serialVersionUID = 1L;

	// Private instance variables
	protected XTextField hostnameField = new XTextField();
	protected JPanel mainConnectionDetailsPanel;
	protected XTextField pathField = new XTextField();
	protected NumericTextField portField = new NumericTextField(new Integer(0), new Integer(65535), new Integer(0));

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
		// Create the main connection details panel
		mainConnectionDetailsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(0, 2, 2, 2);
		gbc.weightx = 1.0;
		// Host name
		addHostnameField(showHost, gbc);
		// Port
		addPortField(showPort, gbc);
		// Path
		addPathField(showPath, gbc);
		// User
		addUserField(showUser, gbc);
		//
		gbc.weighty = 1.0;
		UIUtil.jGridBagAdd(mainConnectionDetailsPanel, createAdditionalComponent(), gbc, GridBagConstraints.REMAINDER);
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
		UIUtil.jGridBagAdd(this, mainConnectionDetailsPanel, gbc, GridBagConstraints.REMAINDER);
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
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
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
		if ((showHost == REQUIRED && hostnameField.getText().trim().equals(""))
				|| (showPort == REQUIRED && portField.getValue().intValue() == 0)
				|| (showPath == REQUIRED && pathField.getText().trim().equals(""))
				|| (showUser == REQUIRED && getUserValue().equals(""))) {
			JOptionPane.showMessageDialog(this, "Please enter all details!", "Connect", JOptionPane.OK_OPTION);
			return false;
		}
		return true;
	}

	protected void addHostnameField(int showHost, GridBagConstraints gbc) {
		if (showHost != OMIT) {
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, new JLabel(getHostnameText()), gbc, GridBagConstraints.REMAINDER);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, hostnameField, gbc, GridBagConstraints.REMAINDER);
			focusOn(hostnameField);
		}
	}

	protected void addPathField(int showPath, GridBagConstraints gbc) {
		if (showPath != OMIT) {
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, new JLabel(getPathText()), gbc, GridBagConstraints.REMAINDER);
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, pathField, gbc, GridBagConstraints.REMAINDER);
			focusOn(pathField);
		}
	}

	protected void addPortField(int showPort, GridBagConstraints gbc) {
		if (showPort != OMIT) {
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, new JLabel(getPortText()), gbc, GridBagConstraints.REMAINDER);
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, portField, gbc, GridBagConstraints.REMAINDER);
			focusOn(portField);
		}
	}

	protected void addUserField(int showUser, GridBagConstraints gbc) {
		if (showUser != OMIT) {
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, new JLabel(getUserText()), gbc, GridBagConstraints.REMAINDER);
			UIUtil.jGridBagAdd(mainConnectionDetailsPanel, userField, gbc, GridBagConstraints.REMAINDER);
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

	protected URI getURIForSettings() throws MalformedURIException {
		URI uri = new URI(scheme + "://");
		processHostUriPortion(uri);
		processPortUriPortion(uri);
		processPathUriPortion(uri);
		processUserUriPortion(uri);
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
		if (hostnameField.getText().trim().length() > 0 && ( showPort == REQUIRED || (showPort == OPTIONAL && portField.getValue().intValue() > 0))) {
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
			portField.setText(String.valueOf(profile == null || profile.getURI() == null || profile.getURI().getPort() == -1
					? defaultPort : profile.getURI().getPort()));
		}
		if (showPath != OMIT) {
			String path = getPathValue(profile);
			pathField.setText(path);
		}
		if (showUser != OMIT) {
			userField.setText(profile == null || profile.getUsername() == null ? "" : profile.getUsername());
		}
	}
	
	protected void setupProfile(ResourceProfile<T> profile) throws Exception {
	}

}