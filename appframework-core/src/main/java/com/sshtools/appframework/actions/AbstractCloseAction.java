package com.sshtools.appframework.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}than can be used to close a
 * connection.
 */
public abstract class AbstractCloseAction extends AbstractAppAction {
	public final static String VAL_NAME = Messages.getString("AbstractCloseAction.Name");
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractCloseAction.
	 * 
	 * @param onToolBar action on toolbar
	 */
	public AbstractCloseAction(boolean onToolBar) {
		putValue(NAME, Messages.getString("AbstractCloseAction.Name"));
		putValue(SMALL_ICON, loadIcon("process-stop", 16));
		putValue(MEDIUM_ICON, loadIcon("process-stop", 24));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractCloseAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractCloseAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('c'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(0));
		putValue(MENU_ITEM_WEIGHT, new Integer(60));
		putValue(ON_TOOLBAR, new Boolean(onToolBar));
		if (onToolBar) {
			putValue(TOOLBAR_GROUP, new Integer(0));
			putValue(TOOLBAR_WEIGHT, new Integer(10));
			putValue(TEXT_ON_TOOLBAR, Boolean.TRUE);
		}
	}
}