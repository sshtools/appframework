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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sshtools.appframework.ui.PreferencesStore;
import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.ui.swing.ActionToolBar;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.MenuAction;

public abstract class ActionBuilder extends ToolsBuilder<ActionToolBar> {
	class ContextActionComparator implements Comparator<AppAction> {
		@Override
		public int compare(AppAction o1, AppAction o2) {
			int i = ((Integer) o1.getValue(AppAction.CONTEXT_MENU_GROUP))
					.compareTo((Integer) o2.getValue(AppAction.CONTEXT_MENU_GROUP));
			return (i == 0) ? ((Integer) o1.getValue(AppAction.CONTEXT_MENU_WEIGHT))
					.compareTo((Integer) o2.getValue(AppAction.CONTEXT_MENU_WEIGHT)) : i;
		}
	}

	class MenuItemActionComparator implements Comparator<AppAction> {
		@Override
		public int compare(AppAction o1, AppAction o2) {
			int i = ((Integer) o1.getValue(AppAction.MENU_ITEM_GROUP)).compareTo((Integer) o2.getValue(AppAction.MENU_ITEM_GROUP));
			return (i == 0) ? ((Integer) o1.getValue(AppAction.MENU_ITEM_WEIGHT))
					.compareTo((Integer) o2.getValue(AppAction.MENU_ITEM_WEIGHT)) : i;
		}
	}

	final static Logger log = LoggerFactory.getLogger(ActionBuilder.class);
	protected JPopupMenu contextMenu;
	protected JMenuBar menuBar;

	public ActionBuilder(JMenuBar menuBar, ActionToolBar toolBar, JPopupMenu contextMenu) {
		super(toolBar);
		this.menuBar = menuBar;
		this.contextMenu = contextMenu;
	}

	public abstract Collection<ActionMenu> listActionMenus();

	@Override
	protected void rebuildContainer(Collection<AppAction> enabledActions) {
		getContainer().setWrap(PreferencesStore.getBoolean(SshToolsApplication.PREF_TOOLBAR_WRAP, false));
		super.rebuildContainer(enabledActions);
	}

	protected void rebuildContextMenu(Collection<AppAction> enabledActions) {
		contextMenu.invalidate();
		// Build the context menu action list
		List<AppAction> contextMenuActions = new ArrayList<AppAction>();
		contextMenu.removeAll();
		for (AppAction action : enabledActions) {
			if (Boolean.TRUE.equals(action.getValue(AppAction.ON_CONTEXT_MENU))) {
				contextMenuActions.add(action);
			}
		}
		log.debug("There are " + contextMenuActions.size() + " on the context menu");
		Collections.sort(contextMenuActions, new ContextActionComparator());
		// Build the context menu
		Integer grp = null;
		for (AppAction action : contextMenuActions) {
			if ((grp != null) && !grp.equals(action.getValue(AppAction.CONTEXT_MENU_GROUP))) {
				contextMenu.addSeparator();
			}
			if (Boolean.TRUE.equals(action.getValue(AppAction.IS_TOGGLE_BUTTON))) {
				final JCheckBoxMenuItem item = ActionJCheckboxMenuItem.fixLAF(new JCheckBoxMenuItem(action));
				action.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals(AppAction.IS_SELECTED)) {
							item.setSelected(((Boolean) evt.getNewValue()).booleanValue());
						}
					}
				});
				contextMenu.add(item);
				item.setSelected(Boolean.TRUE.equals(action.getValue(AppAction.IS_SELECTED)));
			} else {
				contextMenu.add(action);
			}
			grp = (Integer) action.getValue(AppAction.CONTEXT_MENU_GROUP);
		}
		contextMenu.validate();
		contextMenu.repaint();
	}

	@Override
	protected void rebuildForActions(List<AppAction> enabledActions) {
		super.rebuildForActions(enabledActions);
		if (contextMenu != null) {
			rebuildContextMenu(enabledActions);
		}
		if (menuBar != null) {
			rebuildMenuBar(enabledActions);
		}
	}

	protected void rebuildMenuBar(Collection<AppAction> enabledActions) {
		menuBar.invalidate();
		// Build the menu bar action list
		menuBar.removeAll();
		List<AppAction> menuBarActions = new ArrayList<AppAction>();
		for (AppAction action : enabledActions) {
			if (Boolean.TRUE.equals(action.getValue(AppAction.ON_MENUBAR))) {
				menuBarActions.add(action);
			}
		}
		log.debug("There are " + menuBarActions.size() + " on the menubar");
		// Build the menu bar
		List<ActionMenu> menus = new ArrayList<ActionMenu>(listActionMenus());
		Collections.sort(menus);
		Map<String, List<AppAction>> map = new HashMap<String, List<AppAction>>();
		for (AppAction z : menuBarActions) {
			String menuName = (String) z.getValue(AppAction.MENU_NAME);
			if (menuName == null) {
			} else {
				;
				String m = (String) z.getValue(AppAction.MENU_NAME);
				ActionMenu menu = getActionMenu(menus.iterator(), m);
				if (menu != null) {
					List<AppAction> x = map.get(menu.getName());
					if (x == null) {
						x = new ArrayList<AppAction>();
						map.put(menu.getName(), x);
					}
					x.add(z);
				}
			}
		}
		// Create the menu components
		for (ActionMenu m : menus) {
			List<AppAction> x = map.get(m.getName());
			if (x != null) {
				Collections.sort(x, new MenuItemActionComparator());
				JMenu menu = new JMenu(m.getDisplayName());
				menu.setMnemonic(m.getWeight());
				Integer grp = null;
				for (AppAction a : x) {
					Integer g = (Integer) a.getValue(AppAction.MENU_ITEM_GROUP);
					if ((grp != null) && !g.equals(grp)) {
						menu.addSeparator();
					}
					grp = g;
					if (a instanceof MenuAction) {
						JMenu mnu = (JMenu) a.getValue(MenuAction.MENU);
						menu.add(mnu);
					} else {
						if (Boolean.TRUE.equals(a.getValue(AppAction.IS_TOGGLE_BUTTON))) {
							menu.add(new ActionJCheckboxMenuItem(a));
						} else {
							JMenuItem item = new JMenuItem(a);
							menu.add(item);
						}
					}
				}
				menuBar.add(menu);
			}
		}
		menuBar.validate();
		menuBar.repaint();
	}

	private ActionMenu getActionMenu(Iterator<ActionMenu> actions, String actionMenuName) {
		while (actions.hasNext()) {
			ActionMenu a = actions.next();
			if (a.getName().equals(actionMenuName)) {
				return a;
			}
		}
		return null;
	}
}
