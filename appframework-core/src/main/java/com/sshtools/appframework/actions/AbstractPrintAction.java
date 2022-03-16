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
 * Abstract implementation of an {@link AppAction} that may be used to print
 * something.
 */
public abstract class AbstractPrintAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new AbstractPrintAction
	 */
	public AbstractPrintAction() {
		putValue(NAME, Messages.getString("AbstractPrintAction.Name"));
		putValue(SMALL_ICON, loadIcon(CarbonIcons.PRINTER, 16));
		putValue(MEDIUM_ICON, loadIcon(CarbonIcons.PRINTER, 24));
		putValue(SHORT_DESCRIPTION,
				Messages.getString("AbstractPrintAction.ShortDesc"));
		putValue(LONG_DESCRIPTION,
				Messages.getString("AbstractPrintAction.LongDesc"));
		putValue(MNEMONIC_KEY, Integer.valueOf('p'));
		putValue(
				ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK
						+ InputEvent.CTRL_DOWN_MASK));
		putValue(ON_MENUBAR, true);
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, 80);
		putValue(MENU_ITEM_WEIGHT, 0);
		putValue(ON_TOOLBAR, false);
		putValue(TOOLBAR_GROUP, 80);
		putValue(TOOLBAR_WEIGHT, 80);
	}

}
