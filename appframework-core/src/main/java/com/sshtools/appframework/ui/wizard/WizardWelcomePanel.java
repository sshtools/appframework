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
/* HEADER */
package com.sshtools.appframework.ui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.sshtools.appframework.ui.TextBox;

public class WizardWelcomePanel extends JPanel {
	String description, title;
	Icon icon;
	private JLabel welcomeLabel;

	/**
	 * Creates a new WizardWelcomePanel object.
	 * 
	 * @param title title
	 * @param description description
	 * @param icon icon
	 */
	public WizardWelcomePanel(String title, String description, Icon icon) {
		this.icon = icon;
		this.description = description;
		this.title = title;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Creates a new WizardWelcomePanel object.
	 * 
	 * @param title title
	 * @param description description
	 * @param icon icon
	 * @param welcomeLabel wecome label
	 */
	public WizardWelcomePanel(String title, String description, Icon icon, JLabel welcomeLabel) {
		this.icon = icon;
		this.description = description;
		this.title = title;
		this.welcomeLabel = welcomeLabel;
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		this.setLayout(new BorderLayout());
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);
		top.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 8));
		TextBox welcomeTitle = new TextBox(title);
		// Add the welcome label if we have one
		if (welcomeLabel == null) {
			welcomeTitle.setFont(new java.awt.Font("Dialog", 1, 16));
			welcomeTitle.setLocale(java.util.Locale.getDefault());
			top.add(welcomeTitle, BorderLayout.CENTER);
		} else {
			top.add(welcomeLabel, BorderLayout.CENTER);
		}
		JPanel middle = new JPanel(new BorderLayout());
		middle.setOpaque(false);
		middle.setBorder(BorderFactory.createEmptyBorder(24, 16, 0, 8));
		TextBox welcomeDescription = new TextBox(description);
		middle.add(welcomeDescription, BorderLayout.CENTER);
		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 8));
		JLabel clickNext = new JLabel();
		clickNext.setVerifyInputWhenFocusTarget(true);
		clickNext.setText(Messages.getString("WizardWelcomePanel.Continue"));
		bottom.add(clickNext, BorderLayout.SOUTH);
		bottom.setOpaque(false);
		// Side bar
		JPanel left = new JPanel(new BorderLayout()) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(120, super.getPreferredSize().height);
			}
		};
		left.setBackground(UIManager.getColor("Table.background"));
		left.setForeground(UIManager.getColor("Table.foreground"));
		left.setOpaque(true);
		left.setDebugGraphicsOptions(0);
		JLabel welcomeIcon = new JLabel();
		welcomeIcon.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		welcomeIcon.setHorizontalAlignment(SwingConstants.CENTER);
		welcomeIcon.setVerticalAlignment(SwingConstants.TOP);
		welcomeIcon.setIcon(icon);
		left.add(welcomeIcon, BorderLayout.NORTH);
		// Right
		JPanel right = new JPanel(new BorderLayout());
		right.setOpaque(false);
		right.add(top, BorderLayout.NORTH);
		right.add(middle, BorderLayout.CENTER);
		right.add(bottom, BorderLayout.SOUTH);
		// This panel
		this.setBackground(UIManager.getColor("Scrollpane.background"));
		this.setForeground(UIManager.getColor("Scrollpane.foreground"));
		this.add(left, BorderLayout.WEST);
		this.add(right, BorderLayout.CENTER);
	}
}