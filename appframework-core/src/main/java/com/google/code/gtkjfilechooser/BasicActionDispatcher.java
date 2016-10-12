/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.google.code.gtkjfilechooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BasicActionDispatcher implements ActionDispatcher {
	protected List<ActionListener> actionListeners;
	
	public BasicActionDispatcher() {
		actionListeners = new ArrayList<ActionListener>();
	}
	
	@Override
	public void addActionListener(ActionListener l) {
		actionListeners.add(l);		
	}

	@Override
	public void removeActionListener(ActionListener l) {
		actionListeners.remove(l);
	}
	
	@Override
	public void removeAllActionListeners() {
		actionListeners.clear();
	}

	@Override
	public void fireActionEvent(ActionEvent e) {
		for (ActionListener l : actionListeners) {
			l.actionPerformed(e);
		}		
	}

}
