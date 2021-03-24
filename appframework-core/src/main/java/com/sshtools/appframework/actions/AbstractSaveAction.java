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

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to save
 * something.
 */
public abstract class AbstractSaveAction extends AbstractAppAction {
	public final static String VAL_NAME = Messages.getString("AbstractSaveAction.Name");
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractSaveAction object.
	 * 
	 * @param onToolBar action on tool bar
	 */
	public AbstractSaveAction(boolean onToolBar) {
		putValue(NAME, VAL_NAME);
		putValue(SMALL_ICON, loadIcon(BootstrapIcons.FILE_WORD_FILL, 16));
		putValue(MEDIUM_ICON, loadIcon(BootstrapIcons.FILE_WORD_FILL, 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractSaveAction.ShortDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK + InputEvent.SHIFT_MASK));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractSaveAction.LongDesc"));
		putValue(MNEMONIC_KEY, Integer.valueOf('s'));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, Integer.valueOf(0));
		putValue(ON_MENUBAR, Boolean.valueOf(true));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(50));
		putValue(ON_TOOLBAR, Boolean.valueOf(onToolBar));
		if (onToolBar) {
			putValue(TOOLBAR_GROUP, Integer.valueOf(0));
			putValue(TOOLBAR_WEIGHT, Integer.valueOf(20));
			putValue(TEXT_ON_TOOLBAR, Boolean.TRUE);
		}
	}
}