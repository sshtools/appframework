/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
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