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
/* HEADER */

package com.sshtools.appframework.actions;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to display an
 * applications options.
 */

public abstract class AbstractHelpContentsAction extends AbstractAppAction {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractHelpContentsAction object.
	 */
	public AbstractHelpContentsAction() {
		putValue(NAME, Messages.getString("AbstractHelpContentsAction.Name"));
		putValue(SMALL_ICON, loadIcon("help-browser", 16));
		putValue(MEDIUM_ICON, loadIcon("help-browser", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractHelpContentsAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractHelpContentsAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('o'));
		putValue(ACTION_COMMAND_KEY, "contents-command");
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "Help");
		putValue(MENU_ITEM_GROUP, new Integer(90));
		putValue(MENU_ITEM_WEIGHT, new Integer(50));
		putValue(ON_TOOLBAR, new Boolean(true));
		putValue(TOOLBAR_GROUP, new Integer(90));
		putValue(TOOLBAR_WEIGHT, new Integer(0));

	}

}