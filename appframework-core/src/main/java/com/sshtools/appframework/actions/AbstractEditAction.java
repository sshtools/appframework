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

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;

/**
 * Abstract implementation of an {@link AbstractAppAction} that can be used in
 * the context of editing a connection profile.
 */
public abstract class AbstractEditAction extends AbstractAppAction {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractEditAction object.
	 */
	public AbstractEditAction() {
		putValue(NAME, Messages.getString("AbstractEditAction.Name"));
		putValue(SMALL_ICON, loadIcon(BootstrapIcons.PENCIL_SQUARE, 16));
		putValue(MEDIUM_ICON, loadIcon(BootstrapIcons.PENCIL_SQUARE, 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractEditAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractEditAction.LongDesc"));
		putValue(MNEMONIC_KEY, Integer.valueOf('e'));
		putValue(ACTION_COMMAND_KEY, "edit-command");
		putValue(ON_MENUBAR, Boolean.valueOf(true));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, Integer.valueOf(0));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(6));
		putValue(ON_TOOLBAR, Boolean.valueOf(false));
	}
}