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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.appframework.api.ui.SshToolsApplicationPanel;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.EmptyIcon;

/**
 * Concrete implementation of an {@link AppAction} that will toggles whether the
 * toolbar is visible or not.
 */
public class ToggleToolBarAction extends AppAction {
	// Private instance variables
	private SshToolsApplicationPanel panel;

	/**
	 * Creates a new ToggleToolBarAction.
	 *
	 * @param panel application
	 */
	public ToggleToolBarAction(SshToolsApplicationPanel panel) {
		this.panel = panel;
		putValue(NAME, Messages.getString("ToggleToolBarAction.Name"));
		putValue(SHORT_DESCRIPTION, Messages.getString("ToggleToolBarAction.ShortDesc"));
		putValue(SMALL_ICON, new EmptyIcon(16, 16));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.ALT_MASK | InputEvent.CTRL_MASK));
		putValue(LONG_DESCRIPTION, Messages.getString("ToggleToolBarAction.LongDesc"));
		putValue(MNEMONIC_KEY, Integer.valueOf('t'));
		putValue(ON_MENUBAR, Boolean.valueOf(true));
		putValue(MENU_NAME, "View");
		putValue(MENU_ITEM_GROUP, Integer.valueOf(90));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(0));
		putValue(IS_TOGGLE_BUTTON, Boolean.TRUE);
		putValue(IS_SELECTED, Boolean.valueOf(panel.isToolBarVisible()));
		putValue(AppAction.ON_CONTEXT_MENU, Boolean.TRUE);
		putValue(AppAction.CONTEXT_MENU_GROUP, Integer.valueOf(95));
		putValue(AppAction.CONTEXT_MENU_WEIGHT, Integer.valueOf(10));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		boolean sel = !(Boolean.TRUE.equals(getValue(AppAction.IS_SELECTED)));
		panel.setToolBarVisible(sel);
		putValue(AppAction.IS_SELECTED, Boolean.valueOf(sel));
	}
}