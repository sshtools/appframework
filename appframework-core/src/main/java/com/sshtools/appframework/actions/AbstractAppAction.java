/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.actions;

import javax.swing.Icon;

import com.sshtools.appframework.ui.IconStore;
import com.sshtools.ui.swing.AppAction;

public class AbstractAppAction extends AppAction {

	private final static IconStore store = IconStore.getInstance();

	public AbstractAppAction() {
		super();
	}

	public AbstractAppAction(String name, Icon smallIcon) {
		super(name, smallIcon);
	}

	public AbstractAppAction(String name) {
		super(name);
	}

	protected Icon loadIcon(String name, int size) {
		return store.getIcon(name, size);
	}

}
