/* HEADER */
package com.sshtools.appframework.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to make a
 * connection.
 * 
 */
public abstract class AbstractConnectAction extends AbstractAppAction {
	public final static String VAL_NAME = Messages.getString("AbstractConnectAction.Name");

	/**
	 * Creates a new AbstractConnectAction object.
	 * 
	 * @param onToolBar action on tool bar
	 */
	public AbstractConnectAction(boolean onToolBar) {
		putValue(NAME, VAL_NAME);
		putValue(SMALL_ICON, loadIcon("network-wired", 16));
		putValue(MEDIUM_ICON, loadIcon("network-wired", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractConnectAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractConnectAction.LongDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_MASK + InputEvent.SHIFT_MASK));
		putValue(MNEMONIC_KEY, new Integer('c'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(0));
		putValue(MENU_ITEM_WEIGHT, new Integer(1));
		putValue(ON_TOOLBAR, new Boolean(onToolBar));
		if (onToolBar) {
			putValue(TOOLBAR_GROUP, new Integer(0));
			putValue(TOOLBAR_WEIGHT, new Integer(0));
			putValue(TEXT_ON_TOOLBAR, Boolean.TRUE);
		}
	}
}