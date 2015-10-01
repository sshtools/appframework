/* HEADER */
package com.sshtools.virtualsession.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sshtools.profile.URI;
import com.sshtools.ui.swing.ClosableTabbedPane;
import com.sshtools.virtualsession.VirtualSession;
import com.sshtools.virtualsession.VirtualSessionAdapter;
import com.sshtools.virtualsession.VirtualSessionManager;
import com.sshtools.virtualsession.VirtualSessionManagerListener;

/**
 * @author Lee David Painter
 */
public class SwingVirtualSessionManager extends JPanel implements
		VirtualSessionManager {
	private Vector listenerList = new Vector();
	private Vector actionListenerList = new Vector();
	private VirtualSession lastSel = null;
	private JPanel vtSwitcher;
	private VirtualSessionAdapter terminalListener;
	private boolean hideSingleTabHeading;
	protected JTabbedPane tabs;
	private ChangeListener changeListener;
	private boolean hideTabs;
	private int selectedVirtualSessionIndex;
	protected Vector virtualSessions = new Vector();
	private boolean tabMoving;

	/**
	 * 
	 */
	public SwingVirtualSessionManager() {
		super();
		terminalListener = new VirtualSessionAdapter() {
			public void titleChanged(final VirtualSession session,
					final String title) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (tabs != null) {
							int idx = getTabIndexForVirtualSession(session);
							if (idx != -1 && idx <= (tabs.getTabCount() - 1)) {
								tabs.setTitleAt(idx, title);
								tabs.revalidate();
								tabs.repaint();
							}
						}
						fireChanged(session);
					}
				});
			}
		};
		changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!isAdjusting()) {
					if (lastSel != null) {
						fireDeselected(lastSel);
					}
					lastSel = getSelectedVirtualSession();
					if (lastSel != null) {
						tabSelected(lastSel);
					}
				}
			}
		};
		setLayout(new BorderLayout());
		vtSwitcher = new JPanel(new GridLayout(1, 1)) {
			public Insets getInsets() {
				return new Insets(0, 0, 0, 0);
			}
		};
		add(vtSwitcher, BorderLayout.CENTER);
	}

	protected VirtualSession getVirtualSessionForTabIndex(int selectedIndex) {
		return (VirtualSession) tabs.getComponentAt(selectedIndex);
	}

	protected int getTabIndexForVirtualSession(VirtualSession session) {
		return tabs == null ? -1 : tabs.indexOfComponent((Component) session);
	}

	protected boolean isAdjusting() {
		return false;
	}

	/**
	 * Get the actual tab component being used.
	 * 
	 * @return tab component
	 */
	public JTabbedPane getTabComponent() {
		return tabs;
	}

	/**
	 * Set whether a single virtual terminal tab header should be hidden until
	 * more virtual terminals are added. By default this is <code>false</code>
	 * 
	 * @param hideSingleTab
	 *            hide a single tab
	 */
	public void setHideSingleTabHeading(boolean hideSingleTabHeading) {
		this.hideSingleTabHeading = hideSingleTabHeading;
		rebuildComponent();
	}

	/**
	 * Get whether a single virtual terminal tab header should be hidden until
	 * more virtual terminals are added. By default this is <code>false</code>
	 * 
	 * @return hide a single tabs heading
	 */
	public boolean isHideSingleTabHeading() {
		return hideSingleTabHeading;
	}

	/**
	 * Add a listener of ActionEvents fired when the use clicks on the close
	 * icon on the tab header.
	 * 
	 * @param l
	 *            listener to add
	 */
	public void addActionListener(ActionListener l) {
		actionListenerList.addElement(l);
	}

	/**
	 * Remove a listener of ActionEvents fired when the use clicks on the close
	 * icon on the tab header.
	 * 
	 * @param l
	 *            listener to add
	 */
	public void removeActionListener(ActionListener l) {
		actionListenerList.removeElement(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sshtools.virtualsession.VirtualSessionManager#setSelectedVirtualSession
	 * (com.sshtools.virtualsession.VirtualSession)
	 */
	public void setSelectedVirtualSession(VirtualSession vt) {
		if (vt == null && lastSel != null || vt != null && lastSel == null
				|| vt != lastSel) {
			int idx = getTabIndexForVirtualSession(vt);
			if (idx != -1 && tabs != null) {
				lastSel = vt;
				tabs.setSelectedIndex(idx);
			}
			fireSelected(vt);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sshtools.virtualsession.VirtualSessionManager#
	 * addVirtualSessionManagerListener
	 * (com.sshtools.virtualsession.VirtualSessionManagerListener)
	 */
	public void addVirtualSessionManagerListener(
			VirtualSessionManagerListener listener) {
		if (listener != null)
			listenerList.addElement(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sshtools.virtualsession.VirtualSessionManager#
	 * removeVirtualSessionManagerListener
	 * (com.sshtools.virtualsession.VirtualSessionManagerListener)
	 */
	public void removeVirtualSessionManagerListener(
			VirtualSessionManagerListener listener) {
		if (listener != null)
			listenerList.removeElement(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sshtools.virtualsession.VirtualSessionManager#getSelectedVirtualSession
	 * ()
	 */
	public VirtualSession getSelectedVirtualSession() {
		if (tabs != null) {
			int sel = tabs.getSelectedIndex();
			if (sel > -1) {
				return getVirtualSessionForTabIndex(sel);
			}
		}
		return virtualSessions.size() > 0 ? (VirtualSession) virtualSessions
				.get(0) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sshtools.virtualsession.VirtualSessionManager#addVirtualSession(com
	 * .sshtools.virtualsession.VirtualSession)
	 */
	public void addVirtualSession(VirtualSession vt) {
		vt.init(this);
		virtualSessions.addElement(vt);
		vt.addVirtualSessionListener(terminalListener);
		if (virtualSessions.size() == 1 && hideSingleTabHeading) {
			setSwitcherComponent(createSingleTabComponent(vt));
		} else {
			if ((virtualSessions.size() == 2 && hideSingleTabHeading)
					|| (virtualSessions.size() == 1 && !hideSingleTabHeading)) {
				if (tabs != null) {
					tabs.removeAll();
					tabs.removeChangeListener(changeListener);
				}
				/*
				 * tabs.addActionListener(new ActionListener() { public void
				 * actionPerformed(ActionEvent evt) { fireActionPerformed(evt);
				 * } });
				 */
				tabs = createTabPane();

				tabs.addChangeListener(changeListener);
				setSwitcherComponent(tabs);
				if (hideSingleTabHeading) {
					VirtualSession firstTab = (VirtualSession) virtualSessions
							.elementAt(0);
					addImpl(firstTab);
				}
			}
			addImpl(vt);
		}
		fireAdded(vt);
		if (virtualSessions.size() == 1) {
			lastSel = vt;
			tabSelected(vt);
		}
	}

	protected JTabbedPane createTabPane() {
		return new ClosableTabbedPane();
	}

	public void setHideTabs(boolean hideTabs) {
		this.hideTabs = hideTabs;
		rebuildComponent();
	}

	private void rebuildComponent() {
		VirtualSession sel = getSelectedVirtualSession();
		// Determine whether or not to show the tabs
		boolean showTabs = !hideTabs;
		if (showTabs) {
			if (hideSingleTabHeading && virtualSessions.size() < 2) {
				showTabs = false;
			}
		}
		if (showTabs) {
			if (tabs != null) {
				tabs.removeAll();
				tabs.removeChangeListener(changeListener);
			}
			tabs = createTabPane();
			tabs.addChangeListener(changeListener);
			for (Iterator i = virtualSessions.iterator(); i.hasNext();) {
				addImpl((VirtualSession) i.next());
			}
			lastSel = null;
			setSelectedVirtualSession(sel);
			setSwitcherComponent(tabs);
		} else {
			if (tabs != null) {
				tabs.removeAll();
				tabs = null;
			}
			setSwitcherComponent(createSingleTabComponent(sel));
		}
	}

	protected JComponent createSingleTabComponent(VirtualSession sel) {
		return (JComponent) sel;
	}

	protected void addImpl(VirtualSession session) {
		tabs.add(session.getSessionTitle(), createSingleTabComponent(session));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sshtools.virtualsession.VirtualSessionManager#removeVirtualSession
	 * (com.sshtools.virtualsession.VirtualSession)
	 */
	public void removeVirtualSession(VirtualSession vt) {
		int idx = getTabIndexForVirtualSession(vt);
		boolean removingSelected = vt == getSelectedVirtualSession();
		if (idx != -1) {
			vt.removeVirtualSessionListener(terminalListener);
			if (virtualSessions.size() == 2 && hideSingleTabHeading) {
				if (tabs != null) {
					tabs.removeTabAt(idx);
				}
				virtualSessions.removeElement(vt);
				VirtualSession first = (VirtualSession) virtualSessions
						.elementAt(0);
				setSwitcherComponent(createSingleTabComponent(first));
				if (tabs != null) {
					tabs.removeChangeListener(changeListener);
					tabs = null;
				}
			} else {
				if (virtualSessions.size() == 1) {
					lastSel = null;
					setSwitcherComponent(null);
				} else {
					if (tabs != null) {
						tabs.removeTabAt(idx);
					}
				}
				virtualSessions.removeElement(vt);
			}
			fireRemoved(vt);
			if (removingSelected && virtualSessions.size() > 0) {
				fireDeselected(vt);
				setSelectedVirtualSession((VirtualSession) virtualSessions
						.elementAt(virtualSessions.size() - 1));
			}
			if (tabs != null && virtualSessions.size() == 0) {
				tabs.removeChangeListener(changeListener);
				tabs = null;
				lastSel = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sshtools.virtualsession.VirtualSessionManager#
	 * getSelectedVirtualSessionIndex()
	 */
	public int getSelectedVirtualSessionIndex() {
		if (tabs != null) {
			return virtualSessions.indexOf(getVirtualSessionForTabIndex(tabs
					.getSelectedIndex()));
		} else {
			return virtualSessions.size() > 0 ? virtualSessions
					.indexOf(vtSwitcher.getComponent(0)) : -1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sshtools.virtualsession.VirtualSessionManager#getVirtualSessionCount
	 * ()
	 */
	public int getVirtualSessionCount() {
		return virtualSessions.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sshtools.virtualsession.VirtualSessionManager#getVirtualSession(int)
	 */
	public VirtualSession getVirtualSession(int i) {
		return (VirtualSession) virtualSessions.elementAt(i);
	}

	protected void tabSelected(VirtualSession session) {
		fireSelected(session);
	}

	private void setSwitcherComponent(JComponent component) {
		vtSwitcher.invalidate();
		vtSwitcher.removeAll();
		if (component != null) {
			vtSwitcher.add(component);
		}
		vtSwitcher.validate();
		vtSwitcher.repaint();
	}

	protected void fireSelected(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.elementAt(i))
					.virtualSessionSelected(session);
		}
	}

	/*
	 * private void fireActionPerformed(ActionEvent evt) { ActionEvent e = null;
	 * for (int i = actionListenerList.size() - 1; i >= 0; i--) { if (e == null)
	 * { e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, (
	 * (VirtualSession) evt.getSource()).getTitle()); } ( (ActionListener)
	 * actionListenerList.elementAt(i)).actionPerformed(e); } }
	 */
	protected void fireDeselected(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.elementAt(i))
					.virtualSessionDeselected(session);
		}
	}

	protected void fireAdded(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.elementAt(i))
					.virtualSessionAdded(session);
		}
	}

	protected void fireRemoved(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.elementAt(i))
					.virtualSessionRemoved(session);
		}
	}

	protected void fireChanged(VirtualSession session) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			((VirtualSessionManagerListener) listenerList.elementAt(i))
					.virtualSessionChanged(session);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.virtualsession.VirtualSessionManager#virtualSessions()
	 */
	public Enumeration virtualSessions() {
		return virtualSessions.elements();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sshtools.virtualsession.VirtualSessionManager#getEmbeddedClientTicketURI
	 * ()
	 */
	public URI getEmbeddedClientTicketURI() {
		// TODO Auto-generated method stub
		return null;
	}
}