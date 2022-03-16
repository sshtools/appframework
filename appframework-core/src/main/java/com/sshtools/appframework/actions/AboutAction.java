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

import java.awt.Component;
import java.awt.event.ActionEvent;

import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.ui.swing.AppAction;

/**
 * Concreate implementation of a {@link AppAction}that will invoke the
 * showAbout() method in the {@link SshToolsApplication}implementation.
 * 
 * @author $Author: brett $
 */

@SuppressWarnings("serial")
public class AboutAction extends AbstractAppAction {

	private SshToolsApplication application;
	private Component parent;

	public AboutAction(Component parent, SshToolsApplication application) {
		this.application = application;
		this.parent = parent;
		putValue(NAME, Messages.getString("AboutAction.Name"));
		putValue(SHORT_DESCRIPTION, Messages.getString("AboutAction.ShortDesc")
				+ " " + application.getApplicationName());
		putValue(LONG_DESCRIPTION, Messages.getString("AboutAction.LongDesc")
				+ " " + application.getApplicationName());
		putValue(MNEMONIC_KEY, Integer.valueOf('a'));
		putValue(ON_MENUBAR, Boolean.valueOf(true));
		putValue(MENU_NAME, "Help");
		putValue(MENU_ITEM_GROUP, Integer.valueOf(90));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(90));
		putValue(ON_TOOLBAR, Boolean.valueOf(false));
		putValue(TOOLBAR_GROUP, Integer.valueOf(90));
		putValue(TOOLBAR_WEIGHT, Integer.valueOf(10));
		setEmptyIcons();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		application.showAbout(parent);
	}

}