/* HEADER */
package com.sshtools.appframework.actions;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to edit a
 * connection profile.
 */
public abstract class AbstractConnectionPropertiesAction extends AbstractAppAction {

	public final static String VAL_NAME = Messages.getString("AbstractConnectionPropertiesAction.Name");

	/**
	 * 
	 * Creates a new AbstractConnectionPropertiesAction object.
	 * 
	 */
	public AbstractConnectionPropertiesAction() {
		putValue(NAME, VAL_NAME);
		putValue(SMALL_ICON, loadIcon("document-properties", 16));
		putValue(MEDIUM_ICON, loadIcon("document-properties", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractConnectionPropertiesAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractConnectionPropertiesAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('t'));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.ALT_MASK | KeyEvent.CTRL_MASK));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "Edit");
		putValue(MENU_ITEM_GROUP, new Integer(80));
		putValue(MENU_ITEM_WEIGHT, new Integer(10));
		putValue(ON_TOOLBAR, new Boolean(true));
		putValue(TOOLBAR_GROUP, new Integer(85));
		putValue(TOOLBAR_WEIGHT, new Integer(60));
	}
}