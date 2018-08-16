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
				KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
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