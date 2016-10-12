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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.ArrowIcon;
import com.sshtools.ui.swing.MenuAction;

@SuppressWarnings("serial")
public abstract class MRUToolBarAction extends AppAction implements
		FocusListener {

	private MRUPopupMenu menu;
	private Component oppositeComponent;
	private MRUListModel model;

	public MRUToolBarAction(boolean onToolBar, MRUListModel model) {
		this.model = model;
		putValue(AppAction.NAME, Messages.getString("MRUToolBarAction.Name"));
		putValue(
				AppAction.SMALL_ICON,
				new ArrowIcon(SwingConstants.SOUTH, UIManager
						.getColor("controlShadow"), UIManager
						.getColor("Button.foreground"), UIManager
						.getColor("controlLtHighlight")));
		putValue(
				AppAction.MEDIUM_ICON,
				new ArrowIcon(SwingConstants.SOUTH, UIManager
						.getColor("controlShadow"), UIManager
						.getColor("Button.foreground"), UIManager
						.getColor("controlLtHighlight")));
		putValue(AppAction.SHORT_DESCRIPTION,
				Messages.getString("MRUToolBarAction.ShortDesc"));
		putValue(AppAction.LONG_DESCRIPTION,
				Messages.getString("MRUToolBarAction.LongDesc"));
		putValue(AppAction.MNEMONIC_KEY, new Integer('r'));
		putValue(AppAction.ACTION_COMMAND_KEY, "recent");
		putValue(AppAction.ON_MENUBAR, Boolean.FALSE);
		putValue(AppAction.TEXT_ON_TOOLBAR, Boolean.FALSE);
		if (onToolBar) {
			putValue(AppAction.ON_TOOLBAR, Boolean.TRUE);
			putValue(AppAction.TOOLBAR_GROUP, new Integer(0));
			putValue(AppAction.TOOLBAR_WEIGHT, new Integer(6));
		}
		menu = createPopupMenu(model);
		menu.addPopupMenuListener(new PopupMenuListener() {

			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				putValue(AppAction.IS_SELECTED, Boolean.FALSE);
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

		});
		menu.addFocusListener(this);
		putValue(MenuAction.MENU, createMenu(model));
		putValue(AppAction.IS_TOGGLE_BUTTON, Boolean.TRUE);
	}

	protected MRUPopupMenu createPopupMenu(MRUListModel model) {
		return new MRUPopupMenu(model, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mruSelected(new File(evt.getActionCommand()));
			}
		});
	}

	protected MRUMenu createMenu(MRUListModel model) {
		return new MRUMenu(this, model);
	}

	public abstract void mruSelected(File file);

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) evt.getSource();
			if ((oppositeComponent == null || oppositeComponent != button)
					&& model.getSize() != 0) {
				putValue(AppAction.IS_SELECTED, Boolean.TRUE);
				menu.show(button, 0, button.getHeight());
			} else {
				oppositeComponent = null;
				putValue(AppAction.IS_SELECTED, Boolean.FALSE);
			}
		}
	}

	public void focusGained(FocusEvent e) {
		oppositeComponent = e.getOppositeComponent();
	}

	public void focusLost(FocusEvent e) {
	}
}