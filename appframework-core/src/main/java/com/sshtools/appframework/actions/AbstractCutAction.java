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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction} that cuts something to the
 * clipboard.
 */

public abstract class AbstractCutAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;

	public AbstractCutAction() {
		this(true);
	}

	public AbstractCutAction(boolean onToolBar) {
		putValue(NAME, Messages.getString("AbstractCutAction.Name"));
		putValue(SMALL_ICON, loadIcon("edit-cut", 16));
		putValue(MEDIUM_ICON, loadIcon("edit-cut", 24));
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		putValue(SHORT_DESCRIPTION,
				Messages.getString("AbstractCutAction.ShortDesc"));
		putValue(LONG_DESCRIPTION,
				Messages.getString("AbstractCutAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('t'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "Edit");
		putValue(MENU_ITEM_GROUP, new Integer(10));
		putValue(MENU_ITEM_WEIGHT, new Integer(10));
		if (onToolBar) {
			putValue(ON_TOOLBAR, new Boolean(true));
			putValue(TOOLBAR_GROUP, new Integer(10));
			putValue(TOOLBAR_WEIGHT, new Integer(10));
		}
		putValue(ON_CONTEXT_MENU, new Boolean(true));
		putValue(CONTEXT_MENU_GROUP, new Integer(10));
		putValue(CONTEXT_MENU_WEIGHT, new Integer(10));

	}

}