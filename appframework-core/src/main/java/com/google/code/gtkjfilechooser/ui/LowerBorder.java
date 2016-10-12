/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
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
