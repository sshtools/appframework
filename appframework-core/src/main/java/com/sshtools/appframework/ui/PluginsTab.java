/**
 * 
 */
package com.sshtools.appframework.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;

import plugspud.PluginHostContext;
import plugspud.PluginManager;
import plugspud.PluginManagerPane;

public class PluginsTab extends JPanel implements OptionsTab {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PluginManagerPane plugins;
	private Icon icon, largeIcon;

	public PluginsTab(PluginManager manager, PluginHostContext context) {
		setLayout(new BorderLayout());
		plugins = new PluginManagerPane(manager, context, true);
		IconStore iconStore = IconStore.getInstance();
		icon = iconStore.getIcon("system-installer", 24);
		largeIcon = iconStore.getIcon("system-installer", 32);
		add(plugins, BorderLayout.CENTER);
	}

	public void tabSelected() {
	}

	public Icon getTabIcon() {
		return icon;
	}

	public String getTabTitle() {
		return Messages.getString("SshToolsApplication.Title");
	}

	public String getTabToolTipText() {
		return Messages.getString("SshToolsApplication.Tooltip");
	}

	public int getTabMnemonic() {
		return 'p';
	}

	public Component getTabComponent() {
		return this;
	}

	public boolean validateTab() {
		return true;
	}

	public void applyTab() {
		plugins.cleanUp();
	}

	public String getTabCategory() {
		return "General";
	}

	public Icon getTabLargeIcon() {
		return largeIcon;
	}

	public void reset() {
	}
}