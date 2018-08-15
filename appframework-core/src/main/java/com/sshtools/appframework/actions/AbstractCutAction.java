/* HEADER */

package com.sshtools.appframework.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction} that cuts something to the
 * clipboard.
 */

public abstract class AbstractCutAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;

	public AbstractCutAction() {
		this(true);
	}

	public AbstractCutAction(boolean onToolBar) {
		putValue(NAME, Messages.getString("AbstractCutAction.Name"));
		putValue(SMALL_ICON, loadIcon("edit-cut", 16));
		putValue(MEDIUM_ICON, loadIcon("edit-cut", 24));
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		putValue(SHORT_DESCRIPTION,
				Messages.getString("AbstractCutAction.ShortDesc"));
		putValue(LONG_DESCRIPTION,
				Messages.getString("AbstractCutAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('t'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "Edit");
		putValue(MENU_ITEM_GROUP, new Integer(10));
		putValue(MENU_ITEM_WEIGHT, new Integer(10));
		if (onToolBar) {
			putValue(ON_TOOLBAR, new Boolean(true));
			putValue(TOOLBAR_GROUP, new Integer(10));
			putValue(TOOLBAR_WEIGHT, new Integer(10));
		}
		putValue(ON_CONTEXT_MENU, new Boolean(true));
		putValue(CONTEXT_MENU_GROUP, new Integer(10));
		putValue(CONTEXT_MENU_WEIGHT, new Integer(10));

	}

}