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

import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.ui.swing.AppAction;

/**
 * Concrete implementation of an {@link AppAction} that will invoke
 * <code>newContainer()</code> on the current {@link SshToolsApplication}
 * implementation, probably creating a new window or similar for the
 * application.
 */

public class NewWindowAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;
	// Private instance variables

	private SshToolsApplication application;

	/**
	 * Creates a new NewWindowAction.
	 * 
	 * @param application application
	 */
	public NewWindowAction(SshToolsApplication application) {
		this.application = application;
		putValue(NAME, Messages.getString("NewWindowAction.Name"));
		putValue(SHORT_DESCRIPTION, Messages.getString("NewWindowAction.ShortDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
		putValue(LONG_DESCRIPTION, Messages.getString("NewWindowAction.LongDesc"));
		putValue(MNEMONIC_KEY, Integer.valueOf('w'));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, Integer.valueOf(0));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(5));

	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			application.newContainer();
		} catch (SshToolsApplicationException stae) {
			stae.printStackTrace();
		}
	}

}