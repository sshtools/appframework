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
package com.google.code.gtkjfilechooser.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class Expander extends JComponent implements PropertyChangeListener {
	static final public String EXPANDED_STATUS_CHANGED = "expanded_status_changed";
	private JLabel label;
	private JComponent component;
	private boolean expanded = false;

	public Expander(String text, JComponent aComponent) {
		this.component = aComponent;
		addPropertyChangeListener(this);

		setLayout(new BorderLayout());

		label = new JLabel(text);
		label.setIcon(new ExpanderIcon(false, false));

		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				expanded = !expanded;
				firePropertyChange(EXPANDED_STATUS_CHANGED, !expanded, expanded);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				label.setOpaque(true);
				label.setBackground(new Color(241, 238, 233));
				label.setIcon(expanded ? new ExpanderIcon(true, true) : new ExpanderIcon(
						false, true));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				label.setOpaque(false);
				label.setBackground(UIManager.getColor("Label.background"));
				label.setIcon(expanded ? new ExpanderIcon(true, false)
				: new ExpanderIcon(false, false));
			}
		});

		add(label, BorderLayout.PAGE_START);

		component.setVisible(false);
		add(component, BorderLayout.CENTER);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();

		if (EXPANDED_STATUS_CHANGED.equals(property)) {
			doStatusChanged();
		}
	}

	public void setExpanded(boolean expanded) {
		boolean oldValue = this.expanded;
		boolean newValue = expanded;
		this.expanded = expanded;

		firePropertyChange(EXPANDED_STATUS_CHANGED, oldValue, newValue);
	}

	public boolean isExpanded() {
		return expanded;
	}

	private void doStatusChanged() {
		component.setVisible(expanded);
		label.setIcon(expanded ? new ExpanderIcon(true, true) : new ExpanderIcon(false,
				true));
	}
}
