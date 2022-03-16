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
 * Abstract implementation of a {@link AppAction}used to playback a file
 * (possibly previously captured terminal output) to the currently connected
 * host.
 */

public abstract class AbstractPlayAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;

	public AbstractPlayAction() {
		putValue(NAME, Messages.getString("AbstractPlayAction.Name"));
		putValue(SMALL_ICON, loadIcon(CarbonIcons.PLAY_FILLED, 16));
		putValue(MEDIUM_ICON, loadIcon(CarbonIcons.PLAY_FILLED, 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractPlayAction.ShortDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_DOWN_MASK));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractPlayAction.LongDesc"));
		putValue(MNEMONIC_KEY, Integer.valueOf('p'));
		putValue(ON_MENUBAR, Boolean.valueOf(true));
		putValue(MENU_NAME, "Record");
		putValue(MENU_ITEM_GROUP, Integer.valueOf(60));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(0));
		putValue(ON_TOOLBAR, Boolean.valueOf(false));
		putValue(TOOLBAR_GROUP, Integer.valueOf(60));
		putValue(TOOLBAR_WEIGHT, Integer.valueOf(0));

	}

}