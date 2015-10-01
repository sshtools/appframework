/* HEADER */

package com.sshtools.appframework.api.ui;

import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.ui.SshToolsApplication;

/**
 * @author $Author: brett $
 */

public interface SshToolsApplicationContainer {

	/**
	 * @param application
	 * @param panel
	 * 
	 * @throws SshToolsApplicationException
	 */

	public void init(SshToolsApplication application,
			SshToolsApplicationPanel panel) throws SshToolsApplicationException;

	/**
	 * @return
	 */

	public SshToolsApplicationPanel getApplicationPanel();

	/**
   *
   */

	public boolean closeContainer();

	/**
   *
   */

	public boolean canCloseContainer();

	/**
	 * @param visible
	 */

	public void setContainerVisible(boolean visible);

	/**
	 * @param title
	 */

	public void setContainerTitle(String title);

	/**
	 * @return
	 */

	public boolean isContainerVisible();

	/**
   *
   */

	public void packContainer()

	throws SshToolsApplicationException;

	/**
   *
   */

	public void updateUI();

}