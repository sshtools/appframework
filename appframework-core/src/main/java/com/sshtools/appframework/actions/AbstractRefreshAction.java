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

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.kordamp.ikonli.carbonicons.CarbonIcons;

import com.sshtools.appframework.ui.IconStore;
import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of a {@link AppAction}that can be used to "refresh"
 * something. For example, in SshTerm it is used to refresh the terminal
 * display.
 * 
 * @author $Author: brett $
 */

@SuppressWarnings("serial")
public abstract class AbstractRefreshAction extends AppAction {

	/**
	 * Creates a new AbstractRefreshAction object.
	 */

	public AbstractRefreshAction() {
		putValue(NAME, Messages.getString("AbstractRefreshAction.Name"));
		IconStore iconStore = IconStore.getInstance();
		putValue(SMALL_ICON, iconStore.icon(CarbonIcons.RENEW, 16));
		putValue(MEDIUM_ICON, iconStore.icon(CarbonIcons.RENEW, 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractRefreshAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractRefreshAction.LongDesc"));
		putValue(MNEMONIC_KEY, Integer.valueOf('r'));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		putValue(MENU_NAME, "View");
		putValue(ON_MENUBAR, Boolean.valueOf(true));
		putValue(MENU_ITEM_GROUP, Integer.valueOf(20));
		putValue(MENU_ITEM_WEIGHT, Integer.valueOf(10));
		putValue(ON_TOOLBAR, Boolean.valueOf(true));
		putValue(TOOLBAR_GROUP, Integer.valueOf(80));
		putValue(TOOLBAR_WEIGHT, Integer.valueOf(20));

	}

}