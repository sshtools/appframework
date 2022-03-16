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
package com.sshtools.appframework.actions;

import com.sshtools.ui.swing.AppAction;

/**
 * Concrete implementation of a {@link AppAction} that will show the license
 * manager.
 */
public abstract class AbstractLicensingAction extends AppAction {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractLicensingAction object.
	 */
	public AbstractLicensingAction() {
		putValue(NAME, Messages.getString("AbstractLicensingAction.Name"));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractLicensingAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractLicensingAction.LongDesc"));
		putValue(MNEMONIC_KEY, Integer.valueOf('u'));
		putValue(ON_MENUBAR, Boolean.valueOf(true));
		putValue(MENU_NAME, "Help");
		putValue(MENU_ITEM_GROUP, Integer.valueOf(100));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(90));
		putValue(ON_TOOLBAR, Boolean.valueOf(false));
	}

}