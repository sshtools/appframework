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

import javax.swing.Action;
import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sshtools.appframework.ui.ActionToggleButton;
import com.sshtools.appframework.ui.PreferencesStore;
import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.ui.swing.ActionButton;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.ToolBarSeparator;

public class ToolsBuilder<T extends JComponent> {

	class ToolBarActionComparator implements Comparator<AppAction> {
		@Override
		public int compare(AppAction o1, AppAction o2) {
			int i = ((Integer) o1.getValue(AppAction.TOOLBAR_GROUP))
					.compareTo((Integer) o2.getValue(AppAction.TOOLBAR_GROUP));
			return (i == 0) ? ((Integer) o1.getValue(AppAction.TOOLBAR_WEIGHT))
					.compareTo((Integer) o2.getValue(AppAction.TOOLBAR_WEIGHT))
					: i;
		}
	}

	final static Log log = LogFactory.getLog(ToolsBuilder.class);
	private List<AppAction> actions;

	private T container;

	public ToolsBuilder(T container) {
		this(container, null);
	}

	public ToolsBuilder(T container, List<AppAction> actions) {
		this.container = container;
		this.actions = actions;
	}

	public boolean isActionVisible(String name) {
		return true;
	}

	public Collection<AppAction> listActions() {
		return actions;
	}

	public void rebuildActionComponents() {
		log.debug("Rebuilding action components");

		// Determine which actions are available
		List<AppAction> enabledActions = new ArrayList<AppAction>();
		for (AppAction action : listActions()) {
			try {
				String n = (String) action.getValue(Action.NAME);
				if (isActionVisible(n)) {
					enabledActions.add(action);
				}
			} catch (NullPointerException npe) {
			}
		}
		log.debug("There are " + enabledActions.size() + " actions enabled");

		rebuildForActions(enabledActions);

		// Done
		resetActionState();
		log.debug("Rebuilt action components");
	}

	public void resetActionState() {

	}

	protected T getContainer() {
		return container;
	}

	protected void rebuildContainer(Collection<AppAction> enabledActions) {

		container.invalidate();
		container.removeAll();

		boolean useSmallIcons = PreferencesStore.getBoolean(
				SshToolsApplication.PREF_TOOLBAR_SMALL_ICONS, false);
		boolean showSelectiveText = PreferencesStore.getBoolean(
				SshToolsApplication.PREF_TOOLBAR_SHOW_SELECTIVE_TEXT, true);

		// Build the tool bar action list, grouping the actions
		List<AppAction> toolBarActions = new ArrayList<AppAction>();
		for (AppAction action : enabledActions) {
			if (Boolean.TRUE.equals(action.getValue(AppAction.ON_TOOLBAR))) {
				toolBarActions.add(action);
			}
		}
		log.debug("There are " + toolBarActions.size() + " in the toolbar");
		Collections.sort(toolBarActions, new ToolBarActionComparator());

		// Build the tool bar
		Integer grp = null;
		for (AppAction z : toolBarActions) {
			boolean grow = Boolean.TRUE.equals(z.getValue(AppAction.GROW));
			String constraints = grow ? "grow" : null;
			if ((grp != null)
					&& !grp.equals(z.getValue(AppAction.TOOLBAR_GROUP))) {
				container.add(new ToolBarSeparator(), null);
			}
			if (z.getValue(AppAction.COMPONENT) != null) {
				JComponent jc = (JComponent) z.getValue(AppAction.COMPONENT);
				container.add(jc, constraints);
			} else if (Boolean.TRUE.equals(z
					.getValue(AppAction.IS_TOGGLE_BUTTON))) {
				ActionToggleButton tBtn = new ActionToggleButton(z,
						!useSmallIcons, showSelectiveText);
				container.add(tBtn, constraints);
			} else {
				ActionButton btn = new ActionButton(z,
						!useSmallIcons ? AppAction.MEDIUM_ICON
								: Action.SMALL_ICON, showSelectiveText);
				container.add(btn, constraints);
			}
			grp = (Integer) z.getValue(AppAction.TOOLBAR_GROUP);
		}
		container.validate();
		container.repaint();
		if (container.getParent() != null) {
			container.getParent().validate();
		}
	}

	protected void rebuildForActions(List<AppAction> enabledActions) {
		// Rebuild components
		if (container != null) {
			rebuildContainer(enabledActions);
		}
	}
}
