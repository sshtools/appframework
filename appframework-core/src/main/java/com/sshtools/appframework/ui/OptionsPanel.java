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
package com.sshtools.appframework.ui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;

import com.sshtools.ui.Option;
import com.sshtools.ui.OptionChooser;
import com.sshtools.ui.swing.OptionDialog;
import com.sshtools.ui.swing.ScrollingPanel.ButtonMode;
import com.sshtools.ui.swing.SideBarTabber;

/**
 * 
 * 
 * @author $author$
 */
@SuppressWarnings("serial")
public class OptionsPanel extends JPanel {
	public static boolean showOptionsDialog(Component parent, OptionsTab[] tabs) {
		final OptionsPanel opts = new OptionsPanel(tabs);
		opts.reset();
		Option opt = OptionDialog.prompt(parent, OptionChooser.UNCATEGORISED, Messages.getString("OptionsPanel.Options"), opts,
				Option.CHOICES_APPLY, Option.CHOICE_APPLY);
		if (opt.equals(Option.CHOICE_APPLY)) {
			opts.applyTabs();
			return true;
		}
		return false;
	}

	//
	/**  */
	//
	private SideBarTabber tabber;

	/**
	 * Creates a new OptionsPanel object.
	 * 
	 * @param optionalTabs tabs to show
	 */
	public OptionsPanel(OptionsTab[] optionalTabs) {
		super();
		tabber = new SideBarTabber();
		tabber.setButtonMode(ButtonMode.VISIBILITY_AND_SIZE);
		tabber.setFixedToolBarWidth(128);
		if (optionalTabs != null) {
			for (int i = 0; i < optionalTabs.length; i++) {
				optionalTabs[i].reset();
				addTab(optionalTabs[i]);
			}
		}
		// Build this panel
		setLayout(new GridLayout(1, 1));
		add(tabber);
	}

	public void addTab(OptionsTab tab) {
		tabber.addTab(tab);
	}

	public void applyTabs() {
		tabber.applyTabs();
	}

	public void reset() {
		for (int i = 0; i < tabber.getTabCount(); i++) {
			((OptionsTab) tabber.getTabAt(i)).reset();
		}
	}

	public boolean validateTabs() {
		return tabber.validateTabs();
	}
}