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

import org.kordamp.ikonli.carbonicons.CarbonIcons;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to make a
 * connection.
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractConnectAction extends AbstractAppAction {
	public final static String VAL_NAME = Messages.getString("AbstractConnectAction.Name");

	/**
	 * Creates a new AbstractConnectAction object.
	 * 
	 * @param onToolBar action on tool bar
	 */
	public AbstractConnectAction(boolean onToolBar) {
		putValue(NAME, VAL_NAME);
		putValue(SMALL_ICON, loadIcon(CarbonIcons.PLUG_FILLED, 16));
		putValue(MEDIUM_ICON, loadIcon(CarbonIcons.PLUG_FILLED, 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractConnectAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractConnectAction.LongDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
		putValue(MNEMONIC_KEY, Integer.valueOf('c'));
		putValue(ON_MENUBAR, Boolean.valueOf(true));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, Integer.valueOf(0));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(1));
		putValue(ON_TOOLBAR, Boolean.valueOf(onToolBar));
		if (onToolBar) {
			putValue(TOOLBAR_GROUP, Integer.valueOf(0));
			putValue(TOOLBAR_WEIGHT, Integer.valueOf(0));
			putValue(TEXT_ON_TOOLBAR, Boolean.TRUE);
		}
	}
}