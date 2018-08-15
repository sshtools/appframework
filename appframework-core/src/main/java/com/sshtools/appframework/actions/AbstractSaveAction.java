/* HEADER */
package com.sshtools.appframework.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to save
 * something.
 */
public abstract class AbstractSaveAction extends AbstractAppAction {
	public final static String VAL_NAME = Messages.getString("AbstractSaveAction.Name");
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractSaveAction object.
	 * 
	 * @param onToolBar action on tool bar
	 */
	public AbstractSaveAction(boolean onToolBar) {
		putValue(NAME, VAL_NAME);
		putValue(SMALL_ICON, loadIcon("document-save", 16));
		putValue(MEDIUM_ICON, loadIcon("document-save", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractSaveAction.ShortDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK + InputEvent.SHIFT_MASK));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractSaveAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('s'));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(0));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_ITEM_WEIGHT, new Integer(50));
		putValue(ON_TOOLBAR, new Boolean(onToolBar));
		if (onToolBar) {
			putValue(TOOLBAR_GROUP, new Integer(0));
			putValue(TOOLBAR_WEIGHT, new Integer(20));
			putValue(TEXT_ON_TOOLBAR, Boolean.TRUE);
		}
	}
}