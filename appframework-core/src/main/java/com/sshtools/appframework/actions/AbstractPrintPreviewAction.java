/* HEADER */

package com.sshtools.appframework.actions;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}that can be used to provide a
 * print preview dialog.
 * 
 * @author $Author: brett $
 */

public abstract class AbstractPrintPreviewAction extends AppAction {

	/**
	 * Constract a new AbstractPrintPreviewAction.
	 */

	public AbstractPrintPreviewAction() {
		putValue(NAME, Messages.getString("AbstractPrintPreviewAction.Name"));
		putValue(SHORT_DESCRIPTION,
				Messages.getString("AbstractPrintPreviewAction.ShortDesc"));
		putValue(LONG_DESCRIPTION,
				Messages.getString("AbstractPrintPreviewAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('r'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(80));
		putValue(MENU_ITEM_WEIGHT, new Integer(10));
		putValue(ON_TOOLBAR, new Boolean(false));
		setEmptyIcons();
	}

}