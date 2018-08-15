/* HEADER */

package com.sshtools.appframework.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.ui.swing.AppAction;

/**
 * Abstract implementation of an {@link AppAction}than can be used to record
 * something. For example, used in the SshTerm application to create an action
 * for capturing the terminal output.
 */

public class AbstractRecordAction extends AbstractAppAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new AbstractRecordAction object.
	 */
	public AbstractRecordAction() {
		putValue(NAME, Messages.getString("AbstractRecordAction.Name"));
		putValue(SMALL_ICON, loadIcon("media-record", 16));
		putValue(MEDIUM_ICON, loadIcon("media-record", 24));
		putValue(SHORT_DESCRIPTION, Messages.getString("AbstractRecordAction.ShortDesc"));
		putValue(LONG_DESCRIPTION, Messages.getString("AbstractRecordAction.LongDesc"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
		putValue(MNEMONIC_KEY, new Integer('r'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "Record");
		putValue(MENU_ITEM_GROUP, new Integer(60));
		putValue(MENU_ITEM_WEIGHT, new Integer(20));
		putValue(ON_TOOLBAR, new Boolean(false));
		putValue(TOOLBAR_GROUP, new Integer(60));
		putValue(TOOLBAR_WEIGHT, new Integer(20));

	}

}