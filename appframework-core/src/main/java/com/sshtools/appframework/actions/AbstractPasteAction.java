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

package com.sshtools.appframework.actions;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used when pasteing
 * the contents of the clipboard into a view.
 */

@SuppressWarnings("serial")
public abstract class AbstractPasteAction extends AbstractAppAction {

	public AbstractPasteAction() {
	}

	public AbstractPasteAction(boolean onToolBar) {
		putValue(NAME, Messages.getString("AbstractPasteAction.Name"));

		putValue(SMALL_ICON, loadIcon("edit-paste", 16));
		putValue(MEDIUM_ICON, loadIcon("edit-paste", 24));
		putValue(SHORT_DESCRIPTION,
				Messages.getString("AbstractPasteAction.ShortDesc"));
		putValue(LONG_DESCRIPTION,
				Messages.getString("AbstractPasteAction.LongDesc"));
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
		putValue(MNEMONIC_KEY, new Integer('p'));
		putValue(MENU_NAME, "Edit");
		putValue(ON_MENUBAR, true);
		putValue(MENU_ITEM_GROUP, 10);
		putValue(MENU_ITEM_WEIGHT, 20);
		if (onToolBar) {
			putValue(ON_TOOLBAR, true);
			putValue(TOOLBAR_GROUP, 10);
			putValue(TOOLBAR_WEIGHT, 20);
		}
		putValue(ON_CONTEXT_MENU, true);
		putValue(CONTEXT_MENU_GROUP, 10);
		putValue(CONTEXT_MENU_WEIGHT, 20);

	}

}