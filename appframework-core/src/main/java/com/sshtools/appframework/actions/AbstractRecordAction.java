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
 * Abstract implementation of an {@link AppAction}than can be used to record
 * something. For example, used in the SshTerm application to create an action
 * for capturing the terminal output.
 */

public class AbstractRecordAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractRecordAction object.
	 */
	public AbstractRecordAction() {
		putValue(NAME, Messages.getString("AbstractRecordAction.Name"));
		putValue(SMALL_ICON, loadIcon(CarbonIcons.RECORDING_FILLED, 16));
		putValue(MEDIUM_ICON, loadIcon(CarbonIcons.RECORDING_FILLED, 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractRecordAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractRecordAction.LongDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK));
		putValue(MNEMONIC_KEY, Integer.valueOf('r'));
		putValue(ON_MENUBAR, Boolean.valueOf(true));
		putValue(MENU_NAME, "Record");
		putValue(MENU_ITEM_GROUP, Integer.valueOf(60));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(20));
		putValue(ON_TOOLBAR, Boolean.valueOf(false));
		putValue(TOOLBAR_GROUP, Integer.valueOf(60));
		putValue(TOOLBAR_WEIGHT, Integer.valueOf(20));

	}

}