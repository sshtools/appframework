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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.Icon;

public class MissingResourceIcon implements Icon {

	/**
	 * Width
	 */
	private int w;

	/**
	 * Height
	 */
	private int h;

	/**
	 * Margin
	 */
	private int mx = 2;
	private int my = 2;

	public MissingResourceIcon(int w, int h) {
		this.w = w;
		this.h = h;
	}

	public MissingResourceIcon(int x) {
		this(x, x);
	}

	public MissingResourceIcon() {
		this(16, 16);
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();

		// Use antialiasing.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, w, h);

		g2d.setColor(Color.DARK_GRAY);
		g2d.drawRect(mx, my, w - 2 * mx, h - 2 * my);

		int stroke = 2;
		int k = 2;
		g2d.setStroke(new BasicStroke(stroke));
		g2d.setColor(Color.RED);
		GeneralPath cross = new GeneralPath();
		cross.moveTo(k * my + stroke, k * mx + stroke);
		cross.lineTo(w - (k * mx + stroke), h - (k * my + stroke));
		cross.moveTo(w - (k * mx + stroke), k * mx + stroke);
		cross.lineTo(k * my + stroke, h - (k * my + stroke));
		g2d.draw(cross);

		g2d.dispose();
	}

	public int getIconWidth() {
		return w;
	}

	public int getIconHeight() {
		return h;
	}

}
