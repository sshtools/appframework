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
package com.sshtools.virtualsession.status.awt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.util.Enumeration;
import java.util.Vector;

import com.sshtools.ui.awt.Separator;
import com.sshtools.ui.awt.UIUtil;
import com.sshtools.virtualsession.VirtualSessionManager;
import com.sshtools.virtualsession.status.StatusBar;
import com.sshtools.virtualsession.status.StatusElement;
import com.sshtools.virtualsession.ui.commonawt.CommonAWTVirtualSessionComponent;

/**
 * Implementation of a <code>StatusBar</code> that uses a AWT components.
 * 
 * @author $Author: brett $
 */
public class AWTStatusBar extends Panel implements StatusBar,
		CommonAWTVirtualSessionComponent {

	// Private instance variables

	private GridBagLayout layout;
	private GridBagConstraints gbc;
	private Vector elements;
	private boolean separators;

	//
	final static Dimension ZERO_SIZE = new Dimension(0, 0);
	private VirtualSessionManager display;

	/**
	 * Construct a new AWTStatusBar
	 */
	public AWTStatusBar() {
		super();
		setLayout(layout = new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		elements = new Vector();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.terminal.StatusBar#addElement(com.sshtools.terminal.StatusElement)
	 */
	public void addElement(StatusElement element)
			throws IllegalArgumentException {
		if (!(element instanceof Component)) {
			throw new IllegalArgumentException("Element " + element
					+ " must be a " + Component.class.getName());
		}
		elements.addElement(element);
		rebuildBar();
	}

	protected void rebuildBar() {

		// Rebuild the panel
		invalidate();
		removeAll();
		int t = (elements.size() - 1) + elements.size();
		int z = 0;
		for (Enumeration i = elements.elements(); i.hasMoreElements();) {
			StatusElement el = (StatusElement) i.nextElement();
			int pos = (z == t - 1) ? GridBagConstraints.REMAINDER
					: ((z == t - 2) ? GridBagConstraints.RELATIVE : 1);
			gbc.weightx = el.getWeight();
			UIUtil.gridBagAdd(this, (Component) el, gbc, pos);
			z++;
			if (separators && (z + 1) < t) {
				gbc.weightx = 0;
				pos = ((z == t - 2) ? GridBagConstraints.RELATIVE : 1);
				UIUtil.gridBagAdd(this, new Separator(Separator.VERTICAL), gbc,
						pos);
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
	 * @see com.sshtools.terminal.StatusBar#removeAllElements()
	 */
	public void removeAllElements() {
		elements.removeAllElements();
		rebuildBar();
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

	/**
	 * Set whether seperator bars will be placed between each element
	 * 
	 * @param seperators
	 */
	public void setSeparators(boolean seperators) {
		this.separators = seperators;
		rebuildBar();
	}
}
