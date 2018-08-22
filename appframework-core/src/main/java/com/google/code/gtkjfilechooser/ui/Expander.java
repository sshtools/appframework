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
	private JComponent component;
	private boolean expanded = false;
	private JLabel label;

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

	public boolean isExpanded() {
		return expanded;
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

	private void doStatusChanged() {
		component.setVisible(expanded);
		label.setIcon(expanded ? new ExpanderIcon(true, true) : new ExpanderIcon(false,
				true));
	}
}
