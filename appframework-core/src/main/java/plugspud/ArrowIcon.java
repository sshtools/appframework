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
/*
 */
package plugspud;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class ArrowIcon implements Icon, SwingConstants {
	private Color darkShadow;

	private int direction;

	private Color highlight;

	private Color shadow;

	public ArrowIcon(int direction) {
		this(direction, UIManager.getColor("controlDkShadow"), UIManager
				.getColor("controlText"), UIManager
				.getColor("controlLtHighlight"));
	}

	public ArrowIcon(int direction, Color shadow, Color darkShadow,
			Color highlight) {
		this.shadow = shadow;
		this.darkShadow = darkShadow;
		this.highlight = highlight;
		setDirection(direction);
	}

	public int getDirection() {
		return direction;
	}

	@Override
	public int getIconHeight() {
		return 16;
	}

	@Override
	public int getIconWidth() {
		return 16;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int h = getIconHeight();
		int w = getIconWidth();
		int size = Math.min((h - 4) / 3, (w - 4) / 3);
		size = Math.max(size, 2);
		paintTriangle(g, x + ((w - size) / 2), y + ((h - size) / 2), size,
				direction, true);
	}

	public void paintTriangle(Graphics g, int x, int y, int size,
			int direction, boolean isEnabled) {
		Color oldColor = g.getColor();
		int mid, i, j;
		j = 0;
		size = Math.max(size, 2);
		mid = (size / 2) - 1;
		g.translate(x, y);
		if (isEnabled)
			g.setColor(darkShadow);
		else
			g.setColor(shadow);
		switch (direction) {
		case NORTH:
			for (i = 0; i < size; i++) {
				g.drawLine(mid - i, i, mid + i, i);
			}
			if (!isEnabled) {
				g.setColor(highlight);
				g.drawLine(mid - i + 2, i, mid + i, i);
			}
			break;
		case SOUTH:
			if (!isEnabled) {
				g.translate(1, 1);
				g.setColor(highlight);
				for (i = size - 1; i >= 0; i--) {
					g.drawLine(mid - i, j, mid + i, j);
					j++;
				}
				g.translate(-1, -1);
				g.setColor(shadow);
			}
			j = 0;
			for (i = size - 1; i >= 0; i--) {
				g.drawLine(mid - i, j, mid + i, j);
				j++;
			}
			break;
		case WEST:
			for (i = 0; i < size; i++) {
				g.drawLine(i, mid - i, i, mid + i);
			}
			if (!isEnabled) {
				g.setColor(highlight);
				g.drawLine(i, mid - i + 2, i, mid + i);
			}
			break;
		case EAST:
			if (!isEnabled) {
				g.translate(1, 1);
				g.setColor(highlight);
				for (i = size - 1; i >= 0; i--) {
					g.drawLine(j, mid - i, j, mid + i);
					j++;
				}
				g.translate(-1, -1);
				g.setColor(shadow);
			}
			j = 0;
			for (i = size - 1; i >= 0; i--) {
				g.drawLine(j, mid - i, j, mid + i);
				j++;
			}
			break;
		}
		g.translate(-x, -y);
		g.setColor(oldColor);
	}

	public void setDirection(int dir) {
		direction = dir;
	}
}