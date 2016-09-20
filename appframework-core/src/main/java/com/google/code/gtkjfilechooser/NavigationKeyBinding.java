/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 ******************************************************************************/
package com.google.code.gtkjfilechooser;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.KeyEvent.*;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Bind key action to a {@link JComponent}. The Key actions dispatch the
 * following signals:
 * <table border="1">
 * <tr>
 * <td>Signal name</td>
 * 
 * <td>Default key combinations</td>
 * </tr>
 * <tr>
 * <td>location-popup</td>
 * <td>
 * <b>Control+L</b> (empty path); <b>/</b> (path of "/") <b>~</b>; (path of "~")
 * </td>
 * 
 * </tr>
 * <tr>
 * <td>up-folder</td>
 * <td>
 * <b>Alt+Up</b>; <b>Backspace</b></td>
 * </tr>
 * <tr>
 * <td>down-folder</td>
 * <td>
 * <b>Alt+Down</b></span></td>
 * </tr>
 * <tr>
 * <td>home-folder</td>
 * <td>
 * 
 * <b>Alt+Home</b></td>
 * </tr>
 * <tr>
 * <td>desktop-folder</td>
 * <td>
 * <b>Alt+D</b></td>
 * </tr>
 * <tr>
 * 
 * <td>quick-bookmark</td>
 * <td>
 * <b>Alt+1</b> through <b>Alt+0</b></td>
 * </tr>
 * </table>
 * 
 * @author c.cerbo
 * 
 */
public class NavigationKeyBinding extends BasicActionDispatcher {

	static final public String LOCATION_POPUP = "location-popup";
	static final public String UP_FOLDER = "up-folder";
	static final public String DOWN_FOLDER = "down-folder";
	static final public String HOME_FOLDER = "home-folder";
	static final public String DESKTOP_FOLDER = "desktop-folder";
	static final public String QUICK_BOOKMARK = "quick-bookmark";

	private JComponent component;

	public NavigationKeyBinding(JComponent component) {
		this.component = component;
		bindKeyAction();
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
}
