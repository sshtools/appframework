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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.sshtools.ui.swing.UIUtil;

/**
 * Swing component implementation of a {@link OptionsTab} that can be used to
 * change global settings in the J2SSH Application Framework.
 * 
 * @author $Author: brett $
 */

public class GlobalOptionsTab extends JPanel implements OptionsTab {

	private static final long serialVersionUID = 1L;
	// Private statics

	private final static Icon LARGE_GLOBAL_ICON = IconStore.getInstance().getIcon("applications-internet", 32);
	private final static Icon GLOBAL_ICON = IconStore.getInstance().getIcon("applications-internet", 24);

	// Private instance variables.

	private JComboBox lafChooser;
	private JCheckBox toolBarSmallIcons = new JCheckBox(Messages.getString("GlobalOptionsTab.SmallIcons"));
	private JCheckBox toolBarShowSelectiveText = new JCheckBox(Messages.getString("GlobalOptionsTab.SelectiveText"));
	private JCheckBox wrapToolBar = new JCheckBox("Wrap tool bar icons");
	private JCheckBox stayRunning = new JCheckBox("Stay running on closing last window");
	private SshToolsApplication application;

	/**
	 * Creates a new GlobalOptionsTab object.
	 */

	public GlobalOptionsTab(SshToolsApplication application) {
		super();
		this.application = application;
		Insets ins = new Insets(2, 2, 2, 2);
		Insets ins2 = new Insets(2, 40, 2, 2);
		JPanel s = new JPanel(new GridBagLayout());
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.weighty = 1.0;
		gbc1.insets = ins;
		gbc1.anchor = GridBagConstraints.WEST;
		gbc1.fill = GridBagConstraints.HORIZONTAL;
		gbc1.weightx = 0.0;
		UIUtil.jGridBagAdd(s, new JLabel(Messages.getString("GlobalOptionsTab.LAF")), gbc1, GridBagConstraints.RELATIVE);
		gbc1.weightx = 1.0;

		lafChooser = new JComboBox(new LookAndFeelModel());
		lafChooser.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setText(((UIManager.LookAndFeelInfo) value).getName());
				return this;
			}

		});

		UIUtil.jGridBagAdd(s, lafChooser, gbc1, GridBagConstraints.REMAINDER);
		gbc1.weightx = 2.0;
		UIUtil.jGridBagAdd(s, Box.createVerticalStrut(12), gbc1, GridBagConstraints.REMAINDER);
		gbc1.insets = ins2;
		toolBarSmallIcons.setMnemonic('i');
		UIUtil.jGridBagAdd(s, toolBarSmallIcons, gbc1, GridBagConstraints.REMAINDER);
		toolBarShowSelectiveText.setMnemonic('s');
		UIUtil.jGridBagAdd(s, toolBarShowSelectiveText, gbc1, GridBagConstraints.REMAINDER);

		wrapToolBar.setMnemonic('w');
		UIUtil.jGridBagAdd(s, wrapToolBar, gbc1, GridBagConstraints.REMAINDER);
		stayRunning.setMnemonic('r');
		UIUtil.jGridBagAdd(s, stayRunning, gbc1, GridBagConstraints.REMAINDER);

		// This tab
		setLayout(new BorderLayout());
		add(s, BorderLayout.NORTH);
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		reset();

	}

	public void reset() {
		LookAndFeelModel model = (LookAndFeelModel) lafChooser.getModel();
		lafChooser.setSelectedItem(model.getElementForName(PreferencesStore.get(SshToolsApplication.PREF_LAF, UIManager
			.getLookAndFeel().getClass().getName())));
		toolBarShowSelectiveText.setSelected(PreferencesStore
			.getBoolean(SshToolsApplication.PREF_TOOLBAR_SHOW_SELECTIVE_TEXT, true));
		toolBarSmallIcons.setSelected(PreferencesStore.getBoolean(SshToolsApplication.PREF_TOOLBAR_SMALL_ICONS, false));
		wrapToolBar.setSelected(PreferencesStore.getBoolean(SshToolsApplication.PREF_TOOLBAR_WRAP, false));
		stayRunning.setSelected(PreferencesStore.getBoolean(SshToolsApplication.PREF_STAY_RUNNING, false));
	}

	public String getTabCategory() {
		return "General";
	}

	public Icon getTabIcon() {
		return GLOBAL_ICON;
	}

	public Icon getTabLargeIcon() {
		return LARGE_GLOBAL_ICON;
	}

	public String getTabTitle() {
		return Messages.getString("GlobalOptionsTab.Title");
	}

	public String getTabToolTipText() {
		return Messages.getString("GlobalOptionsTab.Tooltip");
	}

	public int getTabMnemonic() {
		return 'g';
	}

	public Component getTabComponent() {
		return this;
	}

	public boolean validateTab() {
		return true;
	}

	public void applyTab() {
		UIManager.LookAndFeelInfo laf = (UIManager.LookAndFeelInfo) lafChooser.getSelectedItem();
		String newLaf = laf == null ? "" : laf.getClassName();
		boolean changed = false;
		if (!newLaf.equals(PreferencesStore.get(SshToolsApplication.PREF_LAF, ""))) {
			PreferencesStore.put(SshToolsApplication.PREF_LAF, newLaf);
			changed = true;
		}
		PreferencesStore.putBoolean(SshToolsApplication.PREF_TOOLBAR_SHOW_SELECTIVE_TEXT, toolBarShowSelectiveText.isSelected());
		PreferencesStore.putBoolean(SshToolsApplication.PREF_TOOLBAR_SMALL_ICONS, toolBarSmallIcons.isSelected());
		PreferencesStore.putBoolean(SshToolsApplication.PREF_TOOLBAR_WRAP, wrapToolBar.isSelected());
		PreferencesStore.putBoolean(SshToolsApplication.PREF_STAY_RUNNING, stayRunning.isSelected());
		if (changed) {
			application.setLookAndFeel(laf);
		}
	}

	public void tabSelected() {
	}

}