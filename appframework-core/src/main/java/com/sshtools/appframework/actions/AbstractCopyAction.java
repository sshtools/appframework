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

@SuppressWarnings("serial")
public abstract class AbstractCopyAction extends AbstractAppAction {

	public AbstractCopyAction() {
		this(true);
	}

	public AbstractCopyAction(boolean onToolBar) {
		putValue(NAME, Messages.getString("AbstractCopyAction.Name"));
		putValue(SMALL_ICON, loadIcon(CarbonIcons.COPY, 16));
		putValue(MEDIUM_ICON, loadIcon(CarbonIcons.COPY, 24));
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		putValue(SHORT_DESCRIPTION,
				Messages.getString("AbstractCopyAction.ShortDesc"));
		putValue(LONG_DESCRIPTION,
				Messages.getString("AbstractCopyAction.LongDesc"));
		putValue(MNEMONIC_KEY, Integer.valueOf('c'));
		putValue(ON_MENUBAR, true);
		putValue(MENU_NAME, "Edit");
		putValue(MENU_ITEM_GROUP, 10);
		putValue(MENU_ITEM_WEIGHT, 0);
		if (onToolBar) {
			putValue(ON_TOOLBAR, true);
			putValue(TOOLBAR_GROUP, 10);
			putValue(TOOLBAR_WEIGHT, 0);
		}
		putValue(ON_CONTEXT_MENU, true);
		putValue(CONTEXT_MENU_GROUP, 10);
		putValue(CONTEXT_MENU_WEIGHT, 0);

	}

}