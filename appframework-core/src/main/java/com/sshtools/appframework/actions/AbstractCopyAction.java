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

@SuppressWarnings("serial")
public abstract class AbstractCopyAction extends AbstractAppAction {

	public AbstractCopyAction() {
		this(true);
	}

	public AbstractCopyAction(boolean onToolBar) {
		putValue(NAME, Messages.getString("AbstractCopyAction.Name"));
		putValue(SMALL_ICON, loadIcon("edit-copy", 16));
		putValue(MEDIUM_ICON, loadIcon("edit-copy", 24));
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
		putValue(SHORT_DESCRIPTION,
				Messages.getString("AbstractCopyAction.ShortDesc"));
		putValue(LONG_DESCRIPTION,
				Messages.getString("AbstractCopyAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('c'));
		putValue(ON_MENUBAR, true);
		putValue(MENU_NAME, "Edit");
		putValue(MENU_ITEM_GROUP, 10);
		putValue(MENU_ITEM_WEIGHT, 0);
		if (onToolBar) {
			putValue(ON_TOOLBAR, true);
			putValue(TOOLBAR_GROUP, 10);
			putValue(TOOLBAR_WEIGHT, 0);
		}
		putValue(ON_CONTEXT_MENU, true);
		putValue(CONTEXT_MENU_GROUP, 10);
		putValue(CONTEXT_MENU_WEIGHT, 0);

	}

}