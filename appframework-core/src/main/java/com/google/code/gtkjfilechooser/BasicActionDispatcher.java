/**
 * Maverick Application Framework - Application framework
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
	public void fireActionEvent(ActionEvent e) {
		for (ActionListener l : actionListeners) {
			l.actionPerformed(e);
		}		
	}
	
	@Override
	public void removeActionListener(ActionListener l) {
		actionListeners.remove(l);
	}

	@Override
	public void removeAllActionListeners() {
		actionListeners.clear();
	}

}
