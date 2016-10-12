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
package com.sshtools.virtualsession.status.swing;

import javax.swing.Icon;

import com.sshtools.ui.swing.ResourceIcon;
import com.sshtools.virtualsession.VirtualSession;
import com.sshtools.virtualsession.VirtualSessionAdapter;
import com.sshtools.virtualsession.VirtualSessionManager;
import com.sshtools.virtualsession.VirtualSessionManagerAdapter;

/**
 * Implementation of a <code>StatusElement</code> that monitors the
 * connections to and disconnections from hots.
 * 
 * @author $Author: brett $
 */
public class SwingStatusConnectionMonitor extends SwingStatusLabel {

	/**
	 * Connected icon
	 */
	public final static Icon CONNECTED = new ResourceIcon("/images/connected-16x16.png");
	/**
	 * Disconnected icon
	 */
	public final static Icon DISCONNECTED = new ResourceIcon("/images/disconnected-16x16.png");

	// private instance variables
	private ConnectionStatusListener connectionStatusListener;

	/**
	 * Construct a new <code>SwingStatusConnectionMonitor</code> component.
	 * 
	 * @param session session
	 */
	public SwingStatusConnectionMonitor(final VirtualSessionManager sessionManager) {
		super(0);
		connectionStatusListener = new ConnectionStatusListener();
		sessionManager.addVirtualSessionManagerListener(new VirtualSessionManagerAdapter() {

			public void virtualSessionAdded(VirtualSession session) {
				session.addVirtualSessionListener(connectionStatusListener);
			}

			public void virtualSessionRemoved(VirtualSession session) {
				session.removeVirtualSessionListener(connectionStatusListener);
				sessionManager.removeVirtualSessionManagerListener(this);
			}

			public void virtualSessionSelected(VirtualSession session) {
				setText(session != null && session.isConnected() ? "Connected" : "Disconnected");
				setIcon(session != null && session.isConnected() ? CONNECTED : DISCONNECTED);
			}

		});
		setIcon(DISCONNECTED);
		setText("Disconnected");
	}

	class ConnectionStatusListener extends VirtualSessionAdapter {
		public void connected(VirtualSession session) {
			setText("Connected");
			setIcon(CONNECTED);
		}

		public void disconnected(VirtualSession session, Throwable exception) {
			setText("Disconnected");
			setIcon(DISCONNECTED);
		}
	}
}