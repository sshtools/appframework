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

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to provide a
 * print preview dialog.
 * 
 * @author $Author: brett $
 */

@SuppressWarnings("serial")
public abstract class AbstractPrintPreviewAction extends AppAction {

	/**
	 * Constract a new AbstractPrintPreviewAction.
	 */

	public AbstractPrintPreviewAction() {
		putValue(NAME, Messages.getString("AbstractPrintPreviewAction.Name"));
		putValue(SHORT_DESCRIPTION,
				Messages.getString("AbstractPrintPreviewAction.ShortDesc"));
		putValue(LONG_DESCRIPTION,
				Messages.getString("AbstractPrintPreviewAction.LongDesc"));
		putValue(MNEMONIC_KEY, Integer.valueOf('r'));
		putValue(ON_MENUBAR, Boolean.valueOf(true));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, Integer.valueOf(80));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(10));
		putValue(ON_TOOLBAR, Boolean.valueOf(false));
		setEmptyIcons();
	}

}