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
package com.google.code.gtkjfilechooser;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_0;
import static java.awt.event.KeyEvent.VK_9;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_HOME;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_NUMPAD0;
import static java.awt.event.KeyEvent.VK_NUMPAD9;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Bind key action to a {@link JComponent}. 
 * 
 * @author c.cerbo
 * 
 */
public class NavigationKeyBinding extends BasicActionDispatcher {

	static final public String DESKTOP_FOLDER = "desktop-folder";
	static final public String DOWN_FOLDER = "down-folder";
	static final public String HOME_FOLDER = "home-folder";
	static final public String LOCATION_POPUP = "location-popup";
	static final public String QUICK_BOOKMARK = "quick-bookmark";
	static final public String UP_FOLDER = "up-folder";

	private JComponent component;

	public NavigationKeyBinding(JComponent component) {
		this.component = component;
		bindKeyAction();
	}

	private void bind(KeyStroke key, final int id, final String actionName) {
		if (actionName == null) {
			throw new IllegalArgumentException("The action must have a name.");
		}

		this.component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, actionName + "_" + id);
		this.component.getActionMap().put(actionName + "_" + id, new AbstractAction(actionName) {
			@Override
			public void actionPerformed(ActionEvent e) {
				ActionEvent evt = new ActionEvent(NavigationKeyBinding.this, id,
						actionName);
				fireActionEvent(evt);
			}
		});
	}

	private void bindKeyAction() {
		// location-popup Control+L
		KeyStroke ctrlL = KeyStroke.getKeyStroke(VK_L, CTRL_DOWN_MASK);
		bind(ctrlL, -1, LOCATION_POPUP);

		// up-folder Alt+Up
		KeyStroke altUp = KeyStroke.getKeyStroke(VK_UP, ALT_DOWN_MASK);
		bind(altUp, -1, UP_FOLDER);

		// down-folder Alt+Down
		KeyStroke altDown = KeyStroke.getKeyStroke(VK_DOWN, ALT_DOWN_MASK);
		bind(altDown, -1, DOWN_FOLDER);

		// Home-folder: Alt+Home
		KeyStroke altHome = KeyStroke.getKeyStroke(VK_HOME, ALT_DOWN_MASK);
		bind(altHome, -1, HOME_FOLDER);

		// Desktop-folder: Alt+D
		KeyStroke altD = KeyStroke.getKeyStroke(VK_D, ALT_DOWN_MASK);
		bind(altD, -1, DESKTOP_FOLDER);

		// quick-bookmark Alt+1 through Alt+0
		for (int i = VK_0; i <= VK_9; i++) {
			KeyStroke altDigit = KeyStroke.getKeyStroke(i, ALT_DOWN_MASK);
			bind(altDigit, i - VK_0, QUICK_BOOKMARK);
		}

		for (int i = VK_NUMPAD0; i <= VK_NUMPAD9; i++) {
			KeyStroke altDigit = KeyStroke.getKeyStroke(i, ALT_DOWN_MASK);
			bind(altDigit, i - VK_NUMPAD0, QUICK_BOOKMARK);
		}

	}
}
