/**
 * Maverick Virtual Session - Framework for a tabbed user interface of connections to some local or remote resources.
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
package com.sshtools.virtualsession;

import java.util.ArrayList;
import java.util.List;

import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.URI;

/**
 * Abstract implementation of a {@link VirtualSessionManager} that provides
 * basic support for listeners and a list of sessions.
 *
 * @param <S> type of virtual session
 */
public abstract class AbstractVirtualSessionManager<S extends VirtualSession<? extends ProfileTransport<S>, AbstractVirtualSessionManager<S>>>
		implements VirtualSessionManager<S> {
	private S lastSel = null;
	private List<VirtualSessionManagerListener> listenerList = new ArrayList<VirtualSessionManagerListener>();
	private VirtualSessionListener terminalListener;
	protected List<S> virtualSessions = new ArrayList<S>();

	/**
	 * Constructor.
	 */
	public AbstractVirtualSessionManager() {
		terminalListener = new VirtualSessionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void titleChanged(VirtualSession<?, ?> session, final String title) {
				AbstractVirtualSessionManager.this.titleChanged((S) session, title);
			}
		};
	}

	protected abstract void addImpl(S session);

	@Override
	public void addVirtualSession(S vt) {
		AbstractVirtualSessionManager<S> abstractVirtualSessionManager = this;
		vt.init(abstractVirtualSessionManager);
		virtualSessions.add(vt);
		vt.addVirtualSessionListener(terminalListener);
		addImpl(vt);
		fireAdded(vt);
		if (lastSel == null) {
			setSelectedVirtualSession(vt);
		}
	}

	@Override
	public void addVirtualSessionManagerListener(VirtualSessionManagerListener listener) {
		if (listener != null) {
			listenerList.add(listener);
		}
	}

	protected void fireAdded(S session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			listenerList.get(i).virtualSessionAdded(session);
		}
	}

	protected void fireChanged(S session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			listenerList.get(i).virtualSessionChanged(session);
		}
	}

	protected void fireDeselected(S session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			listenerList.get(i).virtualSessionDeselected(session);
		}
	}

	protected void fireRemoved(S session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			listenerList.get(i).virtualSessionRemoved(session);
		}
	}

	protected void fireSelected(S session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			listenerList.get(i).virtualSessionSelected(session);
		}
	}

	@Override
	public URI getEmbeddedClientTicketURI() {
		return null;
	}

	@Override
	public S getSelectedVirtualSession() {
		return lastSel;
	}

	@Override
	public int getSelectedVirtualSessionIndex() {
		return lastSel == null ? -1 : virtualSessions.indexOf(lastSel);
	}

	@Override
	public S getVirtualSession(int i) {
		return virtualSessions.get(i);
	}

	@Override
	public int getVirtualSessionCount() {
		return virtualSessions.size();
	}

	protected abstract void removeImpl(S session);

	@Override
	public void removeVirtualSession(S vt) {
		int idx = virtualSessions.indexOf(vt);
		boolean removingSelected = vt == getSelectedVirtualSession();
		if (idx != -1) {
			vt.removeVirtualSessionListener(terminalListener);
			virtualSessions.remove(vt);
			removeImpl(vt);
			fireRemoved(vt);
			if (removingSelected && virtualSessions.size() > 0) {
				fireDeselected(vt);
				setSelectedVirtualSession(virtualSessions.get(virtualSessions.size() - 1));
			}
		}
	}

	@Override
	public void removeVirtualSessionManagerListener(VirtualSessionManagerListener listener) {
		if (listener != null) {
			listenerList.remove(listener);
		}
	}

	protected abstract void selectVirtualSession(int index);

	@Override
	public final void setSelectedVirtualSession(S vt) {
		if (vt == null && lastSel != null || vt != null && lastSel == null || vt != lastSel) {
			int idx = virtualSessions.indexOf(vt);
			if (idx != -1) {
				selectVirtualSession(idx);
			}
			fireSelected(vt);
		}
	}

	protected void tabSelected(S session) {
		fireSelected(session);
	}

	protected abstract void titleChanged(S session, String title);

	@Override
	public Iterable<S> virtualSessions() {
		return virtualSessions;
	}
}
