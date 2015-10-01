/* HEADER */
package com.sshtools.appframework.ui;

import java.io.IOException;
import java.text.ParseException;

import com.sshtools.appframework.api.ui.SshToolsApplicationContainer;
import com.sshtools.appframework.api.ui.SshToolsApplicationPanel;

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
}