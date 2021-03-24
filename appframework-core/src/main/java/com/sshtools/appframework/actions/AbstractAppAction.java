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
package com.sshtools.appframework.actions;

import javax.swing.Icon;

import org.kordamp.ikonli.Ikon;

import com.sshtools.appframework.ui.IconStore;
import com.sshtools.ui.swing.AppAction;

public class AbstractAppAction extends AppAction {

	private final static IconStore store = IconStore.getInstance();

	public AbstractAppAction() {
		super();
	}

	public AbstractAppAction(String name) {
		super(name);
	}

	public AbstractAppAction(String name, Icon smallIcon) {
		super(name, smallIcon);
	}

	protected Icon loadIconX(String name, int size) {
		return store.getIcon(name, size);
	}

	protected Icon loadIcon(Ikon ikon, int size) {
		return store.getIcon(ikon, size);
	}

}
