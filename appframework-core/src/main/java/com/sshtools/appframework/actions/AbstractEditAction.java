/* HEADER */
package com.sshtools.appframework.actions;

/**
 * Abstract implementation of an {@link AbstractAppAction} that can be used in
 * the context of editing a connection profile.
 */
public abstract class AbstractEditAction extends AbstractAppAction {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractEditAction object.
	 */
	public AbstractEditAction() {
		putValue(NAME, Messages.getString("AbstractEditAction.Name"));
		putValue(SMALL_ICON, loadIcon("accessories-text-editor", 16));
		putValue(MEDIUM_ICON, loadIcon("accessories-text-editor", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractEditAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractEditAction.LongDesc"));
		putValue(MNEMONIC_KEY, new Integer('e'));
		putValue(ACTION_COMMAND_KEY, "edit-command");
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(0));
		putValue(MENU_ITEM_WEIGHT, new Integer(6));
		putValue(ON_TOOLBAR, new Boolean(false));
	}
}