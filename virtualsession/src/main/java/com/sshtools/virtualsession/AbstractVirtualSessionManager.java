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
package com.sshtools.virtualsession;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.sshtools.profile.URI;

public abstract class AbstractVirtualSessionManager implements VirtualSessionManager {
	private List listenerList = new ArrayList();
	private VirtualSession lastSel = null;
	private VirtualSessionAdapter terminalListener;
	protected Vector virtualSessions = new Vector();
	
	protected abstract void selectVirtualSession(int index);	
	protected abstract void addImpl(VirtualSession session);
	protected abstract void removeImpl(VirtualSession session);
	protected abstract void titleChanged(VirtualSession session, String title);
	
	public AbstractVirtualSessionManager() {

		terminalListener = new VirtualSessionAdapter() {
			public void titleChanged(final VirtualSession session, final String title) {
				AbstractVirtualSessionManager.this.titleChanged(session, title);
			}
		};
	}
	
	public final void setSelectedVirtualSession(VirtualSession vt) {
		if (vt == null && lastSel != null || vt != null && lastSel == null || vt != lastSel) {
			int idx = virtualSessions.indexOf(vt);
			if (idx != -1) {
				selectVirtualSession(idx);
			}
			fireSelected(vt);
		}
	}

	public void addVirtualSessionManagerListener(VirtualSessionManagerListener listener) {
		if (listener != null) {
			listenerList.add(listener);
		}
	}

	public void removeVirtualSessionManagerListener(VirtualSessionManagerListener listener) {
		if (listener != null) {
			listenerList.remove(listener);
		}
	}

	public VirtualSession getSelectedVirtualSession() {
		return lastSel;
	}
	
	public int getSelectedVirtualSessionIndex() {
		return lastSel == null ? -1 : virtualSessions.indexOf(lastSel);
	}

	public void addVirtualSession(VirtualSession vt) {
		vt.init(this);
		virtualSessions.addElement(vt);
		vt.addVirtualSessionListener(terminalListener);
		addImpl(vt);
		fireAdded(vt);
		if(lastSel == null) {
			setSelectedVirtualSession(vt);
		}
	}

	
	public void removeVirtualSession(VirtualSession vt) {
		int idx = virtualSessions.indexOf(vt);
		boolean removingSelected = vt == getSelectedVirtualSession();
		if (idx != -1) {
			vt.removeVirtualSessionListener(terminalListener);
			// tabs.removeTraverseListener(idx);
			virtualSessions.removeElement(vt);
			removeImpl(vt);
			fireRemoved(vt);
			if (removingSelected && virtualSessions.size() > 0) {
				fireDeselected(vt);
				setSelectedVirtualSession((VirtualSession) virtualSessions.elementAt(virtualSessions.size() - 1));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.virtualsession.VirtualSessionManager#getVirtualSessionCount()
	 */
	public int getVirtualSessionCount() {
		return virtualSessions.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.virtualsession.VirtualSessionManager#getVirtualSession(int)
	 */
	public VirtualSession getVirtualSession(int i) {
		return (VirtualSession) virtualSessions.elementAt(i);
	}

	protected void tabSelected(VirtualSession session) {
		fireSelected(session);
	}

	protected void fireSelected(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.get(i)).virtualSessionSelected(session);
		}
	}

	protected void fireDeselected(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.get(i)).virtualSessionDeselected(session);
		}
	}

	protected void fireAdded(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.get(i)).virtualSessionAdded(session);
		}
	}

	protected void fireRemoved(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.get(i)).virtualSessionRemoved(session);
		}
	}

	protected void fireChanged(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.get(i)).virtualSessionChanged(session);
		}
	}

	public Enumeration virtualSessions() {
		return virtualSessions.elements();
	}

	public URI getEmbeddedClientTicketURI() {
		return null;
	}
}
