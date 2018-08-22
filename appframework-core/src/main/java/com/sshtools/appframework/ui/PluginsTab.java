/**
 * Maverick Application Framework - Application framework
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