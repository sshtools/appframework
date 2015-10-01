/* HEADER */

package com.sshtools.appframework.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import com.sshtools.appframework.api.ui.SshToolsApplicationContainer;
import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.ui.swing.AppAction;

/**
 * Concrete implementation of an {@link AppAction}that will close the currently
 * selected {@link SshToolsApplicationContainer}implementation.
 */
public class ExitAction extends AbstractAppAction {

	private static final long serialVersionUID = -7212751507401773177L;
	// Private instance
	private SshToolsApplication application;
	private SshToolsApplicationContainer container;

	/**
	 * Creates a new ExitAction.
	 * 
	 * @param application application
	 * @param container context
	 */
	public ExitAction(SshToolsApplication application, SshToolsApplicationContainer container) {
		this.application = application;
		this.container = container;
		putValue(Action.NAME, Messages.getString("ExitAction.Name"));
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("ExitAction.ShortDesc"));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.ALT_MASK));
		putValue(Action.LONG_DESCRIPTION, Messages.getString("ExitAction.LongDesc"));
		putValue(Action.MNEMONIC_KEY, new Integer('x'));
		putValue(AppAction.ON_MENUBAR, new Boolean(true));
		putValue(AppAction.MENU_NAME, "File");
		putValue(AppAction.MENU_ITEM_GROUP, new Integer(90));
		putValue(AppAction.MENU_ITEM_WEIGHT, new Integer(90));
		putValue(AppAction.ON_TOOLBAR, new Boolean(false));
		setEmptyIcons();

	}

	public void actionPerformed(ActionEvent evt) {
		application.closeContainer(container);
	}
}