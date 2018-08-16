/**
 * Maverick Application Framework - Application framework
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/* HEADER*/

package com.sshtools.appframework.actions;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction} than can be used the the
 * connection profile to a file other than the current open one.
 */
public abstract class AbstractSaveAsAction extends AbstractAppAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractSaveAsAction object.
	 */

	public AbstractSaveAsAction() {
		putValue(NAME, Messages.getString("AbstractSaveAsAction.Name"));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractSaveAsAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractSaveAsAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('v'));
		putValue(ACTION_COMMAND_KEY, "saveas-command");
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(0));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_ITEM_WEIGHT, new Integer(51));
		putValue(ON_TOOLBAR, new Boolean(false));
		setEmptyIcons();

	}

}