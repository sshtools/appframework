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
package com.sshtools.appframework.api.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sshtools.ui.swing.UIUtil;

public class MultilineLabel extends JPanel {
	// Private instance variables
	private GridBagConstraints constraints;
	private String text;

	/**
	 * Creates a new MultilineLabel object.
	 */
	public MultilineLabel() {
		this("");
	}

	/**
	 * Creates a new MultilineLabel object.
	 *
	 * @param text text
	 */
	public MultilineLabel(String text) {
		super(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill = GridBagConstraints.NONE;
		setText(text);
	}

	public String getText() {
		return text;
	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		for (int i = 0; i < getComponentCount(); i++) {
			getComponent(i).setFont(f);
		}
	}

	public void setText(String text) {
		this.text = text;
		removeAll();
		StringTokenizer tok = new StringTokenizer(text, "\n");
		constraints.weighty = 0.0;
		constraints.weightx = 1.0;
		while (tok.hasMoreTokens()) {
			String t = tok.nextToken();
			if (!tok.hasMoreTokens()) {
				constraints.weighty = 1.0;
			}
			UIUtil.jGridBagAdd(this, new JLabel(t), constraints, GridBagConstraints.REMAINDER);
		}
		revalidate();
		repaint();
	}
}