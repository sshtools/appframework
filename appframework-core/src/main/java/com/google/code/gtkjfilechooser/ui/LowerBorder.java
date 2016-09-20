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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

public class LowerBorder extends AbstractBorder {

	private static final long serialVersionUID = 1L;

	protected int thickness;
	protected Color lineColor;

	static final public Insets INSETS = new Insets(1,5,1,5);

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

	protected Insets getBorderInsets() {
		return INSETS;
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

}
