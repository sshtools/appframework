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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.Icon;

public class ExpanderIcon implements Icon {

	/**
	 * Width
	 */
	private int w = 18;

	/**
	 * Height
	 */
	private int h = 18;

	/**
	 * Margin
	 */
	private int mx = 5;
	private int my = 2;

	public enum Orientation {
		RIGHT, DOWN
	};

	private boolean down = false;
	public boolean filled = false;

	public ExpanderIcon(boolean down, boolean filled) {
		this.down = down;
		this.filled = filled;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();

		// Use antialiasing.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		GeneralPath triangle = new GeneralPath();
		if (down) {
			triangle.moveTo(my, mx);
			triangle.lineTo(w - my, mx);
			triangle.lineTo(w/2, h-mx);
			triangle.closePath();
		} else {
			triangle.moveTo(mx, my);
			triangle.lineTo(w - mx, h / 2);
			triangle.lineTo(mx, h - my);
			triangle.closePath();	
		}

		if (filled) {
			g2d.fill(triangle);
		} else {
			g2d.draw(triangle);
		}

		g2d.dispose();
	}

	public int getIconWidth() {
		return down ? h : w;
	}

	public int getIconHeight() {
		return down ? w : h;
	}

}
