/* HEADER */
package com.sshtools.appframework.api.ui;

import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.ui.SshToolsApplication;

public interface SshToolsApplicationContainer {
	boolean canCloseContainer();

	boolean closeContainer();

	SshToolsApplicationPanel getApplicationPanel();

	void init(SshToolsApplication application, SshToolsApplicationPanel panel) throws SshToolsApplicationException;

	boolean isContainerVisible();

	void packContainer() throws SshToolsApplicationException;

	void setContainerTitle(String title);

	void setContainerVisible(boolean visible);

	void updateUI();
}