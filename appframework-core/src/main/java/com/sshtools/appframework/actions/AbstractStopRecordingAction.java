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
 * Abstract implementation of an {@link AppAction}than can be used to stop
 * something. For example, used in the SshTerm application to create an action
 * to stop capturing output from the terminal.
 */

public abstract class AbstractStopRecordingAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractStopRecordingAction object.
	 */
	public AbstractStopRecordingAction() {
		putValue(NAME, Messages.getString("AbstractStopRecordingAction.Name"));
		putValue(SMALL_ICON, loadIcon("media-playback-stop", 16));
		putValue(MEDIUM_ICON, loadIcon("media-playback-stop", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractStopRecordingAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractStopRecordingAction.LongDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.ALT_MASK));
		putValue(MNEMONIC_KEY, new Integer('t'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "Record");
		putValue(MENU_ITEM_GROUP, new Integer(60));
		putValue(MENU_ITEM_WEIGHT, new Integer(10));
		putValue(ON_TOOLBAR, new Boolean(false));
		putValue(TOOLBAR_GROUP, new Integer(60));
		putValue(TOOLBAR_WEIGHT, new Integer(10));

	}

}