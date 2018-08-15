/* HEADER */
package com.sshtools.appframework.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to make a
 * connection.
 */
@SuppressWarnings("serial")
public abstract class AbstractNewAction extends AbstractAppAction {
	public final static String VAL_NAME = Messages.getString("AbstractNewAction.Name");

	/**
	 * Creates a new AbstractConnectAction object.
	 * 
	 * @param onToolBar action on tool bar
	 */
	public AbstractNewAction(boolean onToolBar) {
		putValue(NAME, VAL_NAME);
		putValue(SMALL_ICON, loadIcon("document-new", 16));
		putValue(MEDIUM_ICON, loadIcon("document-new", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractNewAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractNewAction.LongDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		putValue(MNEMONIC_KEY, new Integer('n'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(0));
		putValue(MENU_ITEM_WEIGHT, new Integer(0));
		putValue(ON_TOOLBAR, new Boolean(onToolBar));
		if (onToolBar) {
			putValue(TOOLBAR_GROUP, new Integer(0));
			putValue(TOOLBAR_WEIGHT, new Integer(0));
			putValue(TEXT_ON_TOOLBAR, Boolean.TRUE);
		}
	}
}