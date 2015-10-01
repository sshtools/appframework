package com.sshtools.appframework.actions;

import com.sshtools.ui.swing.AppAction;

/**
 * Concrete implementation of a {@link AppAction} that will show the license
 * manager.
 */
public abstract class AbstractLicensingAction extends AppAction {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractLicensingAction object.
	 */
	public AbstractLicensingAction() {
		putValue(NAME, Messages.getString("AbstractLicensingAction.Name"));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractLicensingAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractLicensingAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('u'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "Help");
		putValue(MENU_ITEM_GROUP, new Integer(100));
		putValue(MENU_ITEM_WEIGHT, new Integer(90));
		putValue(ON_TOOLBAR, new Boolean(false));
	}

}