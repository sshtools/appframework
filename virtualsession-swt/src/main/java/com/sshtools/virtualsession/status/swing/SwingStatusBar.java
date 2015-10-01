/* HEADER */
package com.sshtools.virtualsession.status.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.sshtools.ui.swing.UIUtil;
import com.sshtools.virtualsession.VirtualSessionManager;
import com.sshtools.virtualsession.status.StatusBar;
import com.sshtools.virtualsession.status.StatusElement;
import com.sshtools.virtualsession.ui.commonawt.CommonAWTVirtualSessionComponent;

/**
 * Implementation of a <code>StatusBar</code> that uses a Swing components.
 * 
 * @author Brett Smith
 */
public class SwingStatusBar extends JPanel implements StatusBar, CommonAWTVirtualSessionComponent {

	// Private instance variables

	private GridBagLayout layout;
	private GridBagConstraints gbc;
	private boolean separators;
	private Vector elements;

	//
	final static Dimension ZERO_SIZE = new Dimension(0, 0);
	private VirtualSessionManager display;

	/**
	 * 
	 */
	public SwingStatusBar() {
		super();
		setLayout(layout = new GridBagLayout());
		elements = new Vector();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
	}

	/**
	 * Set whether seperator bars will be placed between each element
	 * 
	 * @param seperators
	 */
	public void setSeparators(boolean seperators) {
		this.separators = seperators;
		rebuildBar();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.terminal.StatusBar#addElement(com.sshtools.terminal.StatusElement)
	 */
	public void addElement(StatusElement element) throws IllegalArgumentException {
		if (!(element instanceof Component)) {
			throw new IllegalArgumentException("Element must be a " + Component.class.getName());
		}
		elements.addElement(element);
		rebuildBar();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.terminal.StatusBar#removeAllElements(com.sshtools.terminal.StatusElement)
	 */
	public void removeAllElements() {
		elements.clear();
		rebuildBar();
	}

	protected void rebuildBar() {

		// Rebuild the panel
		invalidate();
		removeAll();
		int t = (elements.size() - 1) + elements.size();
		int z = 0;
		for (Iterator i = elements.iterator(); i.hasNext();) {
			StatusElement el = (StatusElement) i.next();
			int pos = (z == t - 1) ? GridBagConstraints.REMAINDER : ((z == t - 2) ? GridBagConstraints.RELATIVE : 1);
			gbc.weightx = el.getWeight();
			UIUtil.jGridBagAdd(this, (Component) el, gbc, pos);
			z++;
			if (separators && (z + 1) < t) {
				gbc.weightx = 0;
				pos = ((z == t - 2) ? GridBagConstraints.RELATIVE : 1);
				UIUtil.jGridBagAdd(this, new JSeparator(JSeparator.VERTICAL), gbc, pos);
				z++;
			}
		}
		validate();
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.terminal.VirtualSessionComponent#getComponent()
	 */
	public Component getComponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.virtualsession.ui.VirtualSessionComponent#init(com.sshtools.virtualsession.ui.VirtualSessionManagerUI)
	 */
	public void init(VirtualSessionManager display) {
		this.display = display;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.virtualsession.ui.VirtualSessionComponent#getTerminalDisplay()
	 */
	public VirtualSessionManager getTerminalDisplay() {
		return display;
	}
}
