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
	 * Height
	 */
	private int h;

	/**
	 * Margin
	 */
	private int mx = 2;

	private int my = 2;
	/**
	 * Width
	 */
	private int w;

	public MissingResourceIcon() {
		this(16, 16);
	}

	public MissingResourceIcon(int x) {
		this(x, x);
	}

	public MissingResourceIcon(int w, int h) {
		this.w = w;
		this.h = h;
	}

	@Override
	public int getIconHeight() {
		return h;
	}

	@Override
	public int getIconWidth() {
		return w;
	}

	@Override
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

}
