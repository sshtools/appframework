/* HEADER */
package com.sshtools.appframework.actions;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to open a
 * connection profile.
 */
public abstract class AbstractOpenAction extends AbstractAppAction {
	public final static String VAL_NAME = Messages.getString("AbstractOpenAction.Name");

	/**
	 * Creates a new AbstractOpenAction.
	 */
	public AbstractOpenAction(boolean onToolBar) {
		putValue(NAME, VAL_NAME);
		putValue(SMALL_ICON, loadIcon("document-open", 16));
		putValue(MEDIUM_ICON, loadIcon("document-open", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractOpenAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractOpenAction.LongDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.ALT_MASK + KeyEvent.CTRL_MASK));
		putValue(MNEMONIC_KEY, new Integer('o'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(0));
		putValue(MENU_ITEM_WEIGHT, new Integer(5));
		putValue(ON_TOOLBAR, new Boolean(onToolBar));
		if (onToolBar) {
			putValue(TOOLBAR_GROUP, new Integer(0));
			putValue(TOOLBAR_WEIGHT, new Integer(5));
			putValue(TEXT_ON_TOOLBAR, Boolean.TRUE);
		}
	}
}