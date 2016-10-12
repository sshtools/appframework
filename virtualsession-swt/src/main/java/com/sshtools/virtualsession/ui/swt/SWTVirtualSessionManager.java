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
package com.sshtools.virtualsession.ui.swt;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import com.sshtools.virtualsession.AbstractVirtualSessionManager;
import com.sshtools.virtualsession.VirtualSession;

public class SWTVirtualSessionManager extends AbstractVirtualSessionManager {
	protected CTabFolder tabs;

	public SWTVirtualSessionManager(Composite parent, int style) {
		super();
		tabs = new CTabFolder(parent, style);
		tabs.setSimple(true);
		tabs.setMinimizeVisible(true);
		tabs.setMaximizeVisible(true);
		tabs.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				setSelectedVirtualSession(getVirtualSession(tabs.getSelectionIndex()));
			}
		});
	}

	protected void titleChanged(final VirtualSession session, final String title) {
		tabs.getDisplay().asyncExec(new Runnable() {
			public void run() {
				int idx = virtualSessions.indexOf(session);
				if (idx != -1 && idx < tabs.getItemCount()) {
					tabs.getItem(idx).setText(title);
				}
				fireChanged(session);
			}
		});
	}

	public CTabFolder getTabs() {
		return tabs;
	}

	public void selectVirtualSession(int index) {
		if (tabs != null) {
			tabs.setSelection(index);
		}
	}

	@Override
	protected void addImpl(VirtualSession session) {
	}
	protected void removeImpl(VirtualSession session) {
	}
}
