/* HEADER */

package com.sshtools.appframework.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction} that may be used to print
 * something.
 */
public abstract class AbstractPrintAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new AbstractPrintAction
	 */
	public AbstractPrintAction() {
		putValue(NAME, Messages.getString("AbstractPrintAction.Name"));
		putValue(SMALL_ICON, loadIcon("document-print", 16));
		putValue(MEDIUM_ICON, loadIcon("document-print", 24));
		putValue(SHORT_DESCRIPTION,
				Messages.getString("AbstractPrintAction.ShortDesc"));
		putValue(LONG_DESCRIPTION,
				Messages.getString("AbstractPrintAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('p'));
		putValue(
				ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK
						+ InputEvent.CTRL_MASK));
		putValue(ON_MENUBAR, true);
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, 80);
		putValue(MENU_ITEM_WEIGHT, 0);
		putValue(ON_TOOLBAR, false);
		putValue(TOOLBAR_GROUP, 80);
		putValue(TOOLBAR_WEIGHT, 80);
	}

}
