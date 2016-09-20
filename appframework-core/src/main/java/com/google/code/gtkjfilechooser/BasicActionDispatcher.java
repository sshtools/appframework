/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 ******************************************************************************/
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
