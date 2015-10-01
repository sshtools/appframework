/* HEADER */
package com.sshtools.virtualsession.ui.awt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import com.sshtools.profile.URI;
import com.sshtools.ui.awt.TabbedPanel;
import com.sshtools.virtualsession.VirtualSession;
import com.sshtools.virtualsession.VirtualSessionAdapter;
import com.sshtools.virtualsession.VirtualSessionManager;
import com.sshtools.virtualsession.VirtualSessionManagerListener;

/**
 * AWT implementation of a {@link VirtualSessionManagerUI}.
 * 
 * @author $Author: brett $
 * 
 */
public class AWTVirtualSessionManager extends Panel implements
		VirtualSessionManager {

	// Private instance variables
	private TabbedPanel tabs;
	private Vector virtualSessions = new Vector();
	private Vector listenerList = new Vector();
	private int lastSel = -1;
	private Panel vtSwitcher;
	private VirtualSessionAdapter terminalListener;

	/**
	 * Construct a new AWTTerminal
	 */
	public AWTVirtualSessionManager() {
		super();
		terminalListener = new VirtualSessionAdapter() {
			public void titleChanged(VirtualSession session, String title) {
				if (tabs != null) {
					tabs.setTitleAt(virtualSessions.indexOf(session), title);
				}
				fireVTChanged(session);
			}
		};
		setLayout(new BorderLayout());
		//
		vtSwitcher = new Panel(new GridLayout(1, 1)) {
			public Insets getInsets() {
				return new Insets(2, 0, 2, 0);
			}
		};
		add(vtSwitcher, BorderLayout.CENTER);
	}

	public VirtualSession getVirtualSession(int i) {
		return (VirtualSession) virtualSessions.elementAt(i);
	}

	private void setVTSwitcherComponent(Component component) {
		vtSwitcher.invalidate();
		vtSwitcher.removeAll();
		if (component != null) {
			vtSwitcher.add(component);
		}
		vtSwitcher.validate();
		vtSwitcher.repaint();
	}

	public void setSelectedVirtualSession(VirtualSession vt) {
		int idx = virtualSessions.indexOf(vt);
		if (idx != -1 && tabs != null) {
			tabs.setSelectedTab(idx);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.terminal.VirtualSessionManagerUI#getSelectedVirtualTerminal()
	 */
	public VirtualSession getSelectedVirtualSession() {
		return virtualSessions.size() == 0 ? null
				: (tabs == null ? (VirtualSession) virtualSessions.elementAt(0)
						: (VirtualSession) tabs.getComponent(tabs
								.getSelectedTab()));
	}

	public void addVirtualSession(VirtualSession vt) {
		vt.init(this);
		virtualSessions.addElement(vt);
		vt.addVirtualSessionListener(terminalListener);
		if (virtualSessions.size() == 1) {
			setVTSwitcherComponent((Component) vt);
		} else {
			if (virtualSessions.size() == 2) {
				tabs = new TabbedPanel(TabbedPanel.TOP);
				tabs.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (lastSel != -1) {
							fireVTDeselected((VirtualSession) virtualSessions
									.elementAt(lastSel));
						}
						lastSel = tabs.getSelectedTab();
						if (lastSel != -1) {
							fireVTSelected((VirtualSession) virtualSessions
									.elementAt(lastSel));
						}
					}

				});
				setVTSwitcherComponent(tabs);
				VirtualSession firstTab = (VirtualSession) virtualSessions
						.elementAt(0);
				tabs.add(firstTab.getSessionTitle(), (Component) firstTab);
			}
			tabs.add(vt.getSessionTitle(), (Component) vt);
		}
		fireVTAdded(vt);
		if (virtualSessions.size() == 1) {
			lastSel = 0;
			fireVTSelected(vt);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.terminal.VirtualSessionManagerUI#removeVirtualTerminal(com.sshtools.terminal.VirtualSession)
	 */
	public void removeVirtualSession(VirtualSession vt) {
		boolean removingSelected = vt == getSelectedVirtualSession();
		int idx = virtualSessions.indexOf(vt);
		if (idx != -1) {
			vt.removeVirtualSessionListener(terminalListener);
			if (virtualSessions.size() == 2) {
				tabs.remove((Component) vt);
				virtualSessions.removeElement(vt);
				VirtualSession firstVT = (VirtualSession) virtualSessions
						.elementAt(0);
				setVTSwitcherComponent((Component) firstVT);
				tabs = null;
			} else {
				if (virtualSessions.size() == 1) {
					setVTSwitcherComponent(null);
				} else {
					tabs.remove((Component) vt);
				}
				virtualSessions.removeElement(vt);
			}
			fireVTRemoved(vt);
			if (removingSelected && virtualSessions.size() > 0) {
				setSelectedVirtualSession((VirtualSession) virtualSessions
						.elementAt(virtualSessions.size() - 1));
			}
		}
		// if(removingSelected) {
		// }

	}

	public int getSelectedVirtualSessionIndex() {
		return tabs.getSelectedTabIndex();
	}

	private void fireVTSelected(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.elementAt(i))
					.virtualSessionSelected(session);
		}
	}

	private void fireVTDeselected(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.elementAt(i))
					.virtualSessionDeselected(session);
		}
	}

	private void fireVTChanged(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.elementAt(i))
					.virtualSessionChanged(session);
		}
	}

	private void fireVTAdded(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.elementAt(i))
					.virtualSessionAdded(session);
		}
	}

	private void fireVTRemoved(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.elementAt(i))
					.virtualSessionRemoved(session);
		}
	}

	public int getVirtualSessionCount() {
		return tabs == null ? 1 : tabs.getComponentCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.virtualsession.VirtualSessionManager#virtualSessions()
	 */
	public Enumeration virtualSessions() {
		return virtualSessions.elements();
	}

	public void addVirtualSessionManagerListener(
			VirtualSessionManagerListener listener) {
		listenerList.addElement(listener);
	}

	public void removeVirtualSessionManagerListener(
			VirtualSessionManagerListener listener) {
		listenerList.removeElement(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.virtualsession.VirtualSessionManager#getEmbeddedClientTicketURI()
	 */
	public URI getEmbeddedClientTicketURI() {
		// TODO Auto-generated method stub
		return null;
	}
}
