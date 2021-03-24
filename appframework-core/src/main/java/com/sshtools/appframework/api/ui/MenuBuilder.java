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
package com.sshtools.appframework.api.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.MenuAction;

public class MenuBuilder extends ToolsBuilder<JMenu> {

	class MenuItemActionComparator implements Comparator<AppAction> {
		@Override
		public int compare(AppAction o1, AppAction o2) {
			int i = ((Integer) o1.getValue(AppAction.MENU_ITEM_GROUP)).compareTo((Integer) o2.getValue(AppAction.MENU_ITEM_GROUP));
			return (i == 0) ? ((Integer) o1.getValue(AppAction.MENU_ITEM_WEIGHT))
					.compareTo((Integer) o2.getValue(AppAction.MENU_ITEM_WEIGHT)) : i;
		}
	}
	
	public MenuBuilder(JMenu menu) {
		super(menu);
	}

	@Override
	protected void rebuildContainer(Collection<AppAction> enabledActions) {
		container.invalidate();
		// Build the menu bar action list
		container.removeAll();
		List<AppAction> menuBarActions = new ArrayList<AppAction>();
		for (AppAction action : enabledActions) {
			if (Boolean.TRUE.equals(action.getValue(AppAction.ON_MENUBAR))) {
				menuBarActions.add(action);
			}
		}
		log.debug("There are " + menuBarActions.size() + " on the menubar");
		// Build the menu bar
		// Create the menu components
		Collections.sort(menuBarActions, new MenuItemActionComparator());
		Integer grp = null;
		for (AppAction a : menuBarActions) {
			Integer g = (Integer) a.getValue(AppAction.MENU_ITEM_GROUP);
			if ((grp != null) && !g.equals(grp)) {
				container.addSeparator();
			}
			grp = g;
			if (a instanceof MenuAction) {
				JMenu mnu = (JMenu) a.getValue(MenuAction.MENU);
				container.add(mnu);
			} else {
				if (Boolean.TRUE.equals(a.getValue(AppAction.IS_TOGGLE_BUTTON))) {
					container.add(new ActionJCheckboxMenuItem(a));
				} else {
					JMenuItem item = new JMenuItem(a);
					container.add(item);
				}
			}
		}
		container.validate();
		container.repaint();
	}
}
