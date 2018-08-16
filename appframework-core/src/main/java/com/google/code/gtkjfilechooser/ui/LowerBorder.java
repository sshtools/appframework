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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

public class LowerBorder extends AbstractBorder {

	static final public Insets INSETS = new Insets(1,5,1,5);

	private static final long serialVersionUID = 1L;
	protected Color lineColor;

	protected int thickness;

	/**
	 * Creates a line border with the specified color and thickness.
	 * 
	 * @param color
	 *            the color of the border
	 * @param thickness
	 *            the thickness of the border
	 */
	public LowerBorder(Color color, int thickness) {
		this.lineColor = color;
		this.thickness = thickness;		
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return getBorderInsets();
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		return getBorderInsets();
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Color oldColor = g.getColor();
		g.setColor(lineColor);
		for (int i = 0; i < thickness; i++) {
			g.drawLine(x + i - width, 
					height - y + i, 
					width - i - i - 1, 
					height - i - i - 1);

			g.setColor(oldColor);
		}
	}

	protected Insets getBorderInsets() {
		return INSETS;
	}

}
