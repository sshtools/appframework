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
	private Icon icon, largeIcon;
	private PluginManagerPane plugins;

	public PluginsTab(PluginManager manager, PluginHostContext context) {
		setLayout(new BorderLayout());
		plugins = new PluginManagerPane(manager, context, true);
		IconStore iconStore = IconStore.getInstance();
		icon = iconStore.getIcon("system-installer", 24);
		largeIcon = iconStore.getIcon("system-installer", 32);
		add(plugins, BorderLayout.CENTER);
	}

	@Override
	public void applyTab() {
		plugins.cleanUp();
	}

	@Override
	public String getTabCategory() {
		return "General";
	}

	@Override
	public Component getTabComponent() {
		return this;
	}

	@Override
	public Icon getTabIcon() {
		return icon;
	}

	@Override
	public Icon getTabLargeIcon() {
		return largeIcon;
	}

	@Override
	public int getTabMnemonic() {
		return 'p';
	}

	@Override
	public String getTabTitle() {
		return Messages.getString("SshToolsApplication.Title");
	}

	@Override
	public String getTabToolTipText() {
		return Messages.getString("SshToolsApplication.Tooltip");
	}

	@Override
	public void reset() {
	}

	@Override
	public void tabSelected() {
	}

	@Override
	public boolean validateTab() {
		return true;
	}
}