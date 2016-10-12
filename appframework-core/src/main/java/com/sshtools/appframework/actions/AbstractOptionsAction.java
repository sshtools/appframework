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

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to display an
 * applications options.
 * 
 * @author $Author: brett $
 */

public abstract class AbstractOptionsAction extends AbstractAppAction {

	/**
	 * Creates a new AbstractOptionsAction object.
	 */

	public AbstractOptionsAction() {
		putValue(NAME, Messages.getString("AbstractOptionsAction.Name"));
		putValue(SMALL_ICON, loadIcon("preferences-desktop", 16));
		putValue(MEDIUM_ICON, loadIcon("preferences-desktop", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractOptionsAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractOptionsAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('o'));
		putValue(ACTION_COMMAND_KEY, "options-command");
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "Tools");
		putValue(MENU_ITEM_GROUP, new Integer(90));
		putValue(MENU_ITEM_WEIGHT, new Integer(99));
		putValue(ON_TOOLBAR, new Boolean(false));
		putValue(TOOLBAR_GROUP, new Integer(90));
		putValue(TOOLBAR_WEIGHT, new Integer(0));

	}

}