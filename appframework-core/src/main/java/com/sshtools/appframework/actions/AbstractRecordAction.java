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
 * Abstract implementation of an {@link AppAction}than can be used to record
 * something. For example, used in the SshTerm application to create an action
 * for capturing the terminal output.
 */

public class AbstractRecordAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractRecordAction object.
	 */
	public AbstractRecordAction() {
		putValue(NAME, Messages.getString("AbstractRecordAction.Name"));
		putValue(SMALL_ICON, loadIcon("media-record", 16));
		putValue(MEDIUM_ICON, loadIcon("media-record", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractRecordAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractRecordAction.LongDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_MASK));
		putValue(MNEMONIC_KEY, new Integer('r'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "Record");
		putValue(MENU_ITEM_GROUP, new Integer(60));
		putValue(MENU_ITEM_WEIGHT, new Integer(20));
		putValue(ON_TOOLBAR, new Boolean(false));
		putValue(TOOLBAR_GROUP, new Integer(60));
		putValue(TOOLBAR_WEIGHT, new Integer(20));

	}

}