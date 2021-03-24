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

import javax.swing.Action;
import javax.swing.KeyStroke;

import com.sshtools.appframework.api.ui.SshToolsApplicationContainer;
import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.ui.swing.AppAction;

/**
 * Concrete implementation of an {@link AppAction}that will close the currently
 * selected {@link SshToolsApplicationContainer}implementation.
 */
public class ExitAction extends AbstractAppAction {

	private static final long serialVersionUID = -7212751507401773177L;
	// Private instance
	private SshToolsApplication application;
	private SshToolsApplicationContainer container;

	/**
	 * Creates a new ExitAction.
	 * 
	 * @param application application
	 * @param container context
	 */
	public ExitAction(SshToolsApplication application, SshToolsApplicationContainer container) {
		this.application = application;
		this.container = container;
		putValue(Action.NAME, Messages.getString("ExitAction.Name"));
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("ExitAction.ShortDesc"));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
		putValue(Action.LONG_DESCRIPTION, Messages.getString("ExitAction.LongDesc"));
		putValue(Action.MNEMONIC_KEY, Integer.valueOf('x'));
		putValue(AppAction.ON_MENUBAR, Boolean.valueOf(true));
		putValue(AppAction.MENU_NAME, "File");
		putValue(AppAction.MENU_ITEM_GROUP, Integer.valueOf(90));
		putValue(AppAction.MENU_ITEM_WEIGHT, Integer.valueOf(90));
		putValue(AppAction.ON_TOOLBAR, Boolean.valueOf(false));
		setEmptyIcons();

	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if(container == null)
			application.exit();
		else
			application.closeContainer(container);
	}
}