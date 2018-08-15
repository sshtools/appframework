package com.sshtools.appframework.actions;

import javax.swing.Icon;

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

	protected Icon loadIcon(String name, int size) {
		return store.getIcon(name, size);
	}

}
