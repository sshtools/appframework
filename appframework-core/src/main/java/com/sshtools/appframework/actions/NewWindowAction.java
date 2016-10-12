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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.ui.swing.AppAction;

/**
 * Concrete implementation of an {@link AppAction} that will invoke
 * <code>newContainer()</code> on the current {@link SshToolsApplication}
 * implementation, probably creating a new window or similar for the
 * application.
 */

public class NewWindowAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;
	// Private instance variables

	private SshToolsApplication application;

	/**
	 * Creates a new NewWindowAction.
	 * 
	 * @param application application
	 */
	public NewWindowAction(SshToolsApplication application) {
		this.application = application;
		putValue(NAME, Messages.getString("NewWindowAction.Name"));
		putValue(SMALL_ICON, loadIcon("window-new", 16));
		putValue(MEDIUM_ICON, loadIcon("window-new", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("NewWindowAction.ShortDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK));
		putValue(LONG_DESCRIPTION, Messages.getString("NewWindowAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('w'));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(0));
		putValue(MENU_ITEM_WEIGHT, new Integer(5));

	}

	public void actionPerformed(ActionEvent evt) {
		try {
			application.newContainer();
		} catch (SshToolsApplicationException stae) {
			stae.printStackTrace();
		}
	}

}