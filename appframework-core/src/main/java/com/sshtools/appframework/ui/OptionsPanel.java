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

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;

import com.sshtools.ui.Option;
import com.sshtools.ui.OptionChooser;
import com.sshtools.ui.swing.OptionDialog;
import com.sshtools.ui.swing.SideBarTabber;

/**
 * 
 * 
 * @author $author$
 */
public class OptionsPanel extends JPanel {
	public static boolean showOptionsDialog(Component parent, OptionsTab[] tabs) {
		final OptionsPanel opts = new OptionsPanel(tabs);
		opts.reset();
		Option opt = OptionDialog.prompt(parent, OptionChooser.UNCATEGORISED, Messages.getString("OptionsPanel.Options"), opts,
				Option.CHOICES_OK_CANCEL, Option.CHOICE_CANCEL);
		if (opt.equals(Option.CHOICE_OK)) {
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
		tabber.setFixedToolBarWidth(72);
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