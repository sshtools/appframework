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
 * Abstract implementation of an {@link AppAction}that can be used to open a
 * connection profile.
 */
public abstract class AbstractOpenAction extends AbstractAppAction {
	public final static String VAL_NAME = Messages.getString("AbstractOpenAction.Name");

	/**
	 * Creates a new AbstractOpenAction.
	 * 
	 * @param onToolBar action on tool bar
	 */
	public AbstractOpenAction(boolean onToolBar) {
		putValue(NAME, VAL_NAME);
		putValue(SMALL_ICON, loadIcon("document-open", 16));
		putValue(MEDIUM_ICON, loadIcon("document-open", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractOpenAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractOpenAction.LongDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK + InputEvent.CTRL_MASK));
		putValue(MNEMONIC_KEY, new Integer('o'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(0));
		putValue(MENU_ITEM_WEIGHT, new Integer(5));
		putValue(ON_TOOLBAR, new Boolean(onToolBar));
		if (onToolBar) {
			putValue(TOOLBAR_GROUP, new Integer(0));
			putValue(TOOLBAR_WEIGHT, new Integer(5));
			putValue(TEXT_ON_TOOLBAR, Boolean.TRUE);
		}
	}
}