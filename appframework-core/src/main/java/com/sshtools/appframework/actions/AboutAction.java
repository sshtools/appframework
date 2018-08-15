/* HEADER */

package com.sshtools.appframework.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.ui.swing.AppAction;

/**
 * Concreate implementation of a {@link AppAction}that will invoke the
 * showAbout() method in the {@link SshToolsApplication}implementation.
 * 
 * @author $Author: brett $
 */

public class AboutAction extends AbstractAppAction {

	private SshToolsApplication application;
	private Component parent;

	public AboutAction(Component parent, SshToolsApplication application) {
		this.application = application;
		this.parent = parent;
		putValue(NAME, Messages.getString("AboutAction.Name"));
		putValue(SHORT_DESCRIPTION, Messages.getString("AboutAction.ShortDesc")
				+ " " + application.getApplicationName());
		putValue(LONG_DESCRIPTION, Messages.getString("AboutAction.LongDesc")
				+ " " + application.getApplicationName());
		putValue(MNEMONIC_KEY, new Integer('a'));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_NAME, "Help");
		putValue(MENU_ITEM_GROUP, new Integer(90));
		putValue(MENU_ITEM_WEIGHT, new Integer(90));
		putValue(ON_TOOLBAR, new Boolean(false));
		putValue(TOOLBAR_GROUP, new Integer(90));
		putValue(TOOLBAR_WEIGHT, new Integer(10));
		setEmptyIcons();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		application.showAbout(parent);
	}

}