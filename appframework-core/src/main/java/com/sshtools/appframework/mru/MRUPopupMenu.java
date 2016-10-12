/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.mru;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * An extension of a {@link JPopupMenu} that is built using a model of Most
 * Recently Used items from a {@link MRUModel}. When the model changes, the menu
 * will automatically update itself.
 */
@SuppressWarnings("serial")
public class MRUPopupMenu extends JPopupMenu implements ListDataListener,
		ActionListener {

	private MRUListModel model;
	private ActionListener actionListener;

	public MRUPopupMenu(MRUListModel model, ActionListener actionListener) {
		super();
		init(model);
		this.actionListener = actionListener;
	}

	public void cleanUp() {
		removeNotify();
		model.removeListDataListener(this);
	}

	private void init(MRUListModel model) {
		this.model = model;
		rebuildMenu();
		model.addListDataListener(this);
	}

	public void intervalAdded(ListDataEvent e) {
		rebuildMenu();
	}

	public void intervalRemoved(ListDataEvent e) {
		rebuildMenu();
	}

	public void contentsChanged(ListDataEvent e) {
		rebuildMenu();

	}

	public void actionPerformed(ActionEvent evt) {
		actionListener.actionPerformed(evt);
	}

	protected boolean include(File f) {
		return true;
	}

	protected Icon getIconForFavourite(File f) {
		return null;
	}

	protected String getToolTipForFavourite(File f) {
		return f.getAbsolutePath();
	}

	protected String getNameForFavourite(File favourite) {
		String name = favourite.getName();
		if (name.endsWith(".xml")) {
			name = name.substring(0, name.length() - 4);
		}
		return name;
	}

	private void rebuildMenu() {
		Component[] c = getComponents();
		for (int i = 0; (c != null) && (i < c.length); i++) {
			((JMenuItem) c[i]).removeActionListener(this);
			remove(c[i]);
		}
		for (int i = 0; i < model.getSize(); i++) {
			File f = (File) model.getElementAt(i);
			if (include(f)) {
				String name = getNameForFavourite(f);
				JMenuItem m = new JMenuItem(name);
				m.setIcon(getIconForFavourite(f));
				m.setActionCommand(f.getAbsolutePath());
				m.setToolTipText(getToolTipForFavourite(f));
				m.addActionListener(this);
				add(m);
			}
		}
		setEnabled(model.getSize() > 0);
		validate();

	}

}