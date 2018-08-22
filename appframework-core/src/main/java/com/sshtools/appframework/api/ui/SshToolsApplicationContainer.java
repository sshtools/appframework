/**
 * Maverick Application Framework - Application framework
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