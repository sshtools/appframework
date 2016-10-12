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
 * Abstract implementation of an {@link AppAction} that may be used to print
 * something.
 */
public abstract class AbstractPrintAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new AbstractPrintAction
	 */
	public AbstractPrintAction() {
		putValue(NAME, Messages.getString("AbstractPrintAction.Name"));
		putValue(SMALL_ICON, loadIcon("document-print", 16));
		putValue(MEDIUM_ICON, loadIcon("document-print", 24));
		putValue(SHORT_DESCRIPTION,
				Messages.getString("AbstractPrintAction.ShortDesc"));
		putValue(LONG_DESCRIPTION,
				Messages.getString("AbstractPrintAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('p'));
		putValue(
				ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.ALT_MASK
						+ KeyEvent.CTRL_MASK));
		putValue(ON_MENUBAR, true);
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, 80);
		putValue(MENU_ITEM_WEIGHT, 0);
		putValue(ON_TOOLBAR, false);
		putValue(TOOLBAR_GROUP, 80);
		putValue(TOOLBAR_WEIGHT, 80);
	}

}
