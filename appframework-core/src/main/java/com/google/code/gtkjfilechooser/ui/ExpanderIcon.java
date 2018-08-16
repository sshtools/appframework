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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.Icon;

public class ExpanderIcon implements Icon {

	public enum Orientation {
		DOWN, RIGHT
	}

	public boolean filled = false;

	private boolean down = false;
	/**
	 * Height
	 */
	private int h = 18;

	/**
	 * Margin
	 */
	private int mx = 5;;

	private int my = 2;
	/**
	 * Width
	 */
	private int w = 18;

	public ExpanderIcon(boolean down, boolean filled) {
		this.down = down;
		this.filled = filled;
	}

	@Override
	public int getIconHeight() {
		return down ? w : h;
	}

	@Override
	public int getIconWidth() {
		return down ? h : w;
	}

	@Override
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

}
