/**
 * Maverick Client Application Framework - Framework for 'client' applications.
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/* HEADER */
package com.sshtools.appframework.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.sshtools.appframework.actions.AbstractNewAction;
import com.sshtools.appframework.api.ui.AbstractSshToolsApplicationClientPanel;
import com.sshtools.appframework.api.ui.SshToolsApplicationContainer;
import com.sshtools.appframework.api.ui.SshToolsApplicationPanel;
import com.sshtools.ui.swing.AppAction;

/**
 * An abstract extension of {@link SshToolsApplication} that should be used for
 * applications that wish to make use of the applications framework built in
 * J2SSH Maverick SSH2 support.
 */
public abstract class SshToolsClientApplication extends SshToolsApplication {
	public static String PREF_LAST_PRIVATE_KEY_LOCATION = "apps.client.lastPrivateKeyFile";

	/**
	 * Construct a new SshToolsClientApplication
	 * 
	 * @param panelClass class of class
	 * @param defaultContainerClass class of container
	 * @throws ParseException
	 * @throws IOException
	 */
	public SshToolsClientApplication(Class<? extends SshToolsApplicationPanel> panelClass,
			Class<? extends SshToolsApplicationContainer> defaultContainerClass) throws IOException, ParseException {
		super(panelClass, defaultContainerClass);
	}

	@Override
	protected List<AppAction> getTrayActions() {
		List<AppAction> actions = super.getTrayActions();
		actions.add(new AbstractNewAction(false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent evt) {
				open();
				((AbstractSshToolsApplicationClientPanel<?>)getContainerAt(0).getApplicationPanel()).newConnection();
			}
		});
		return actions;
	}
}