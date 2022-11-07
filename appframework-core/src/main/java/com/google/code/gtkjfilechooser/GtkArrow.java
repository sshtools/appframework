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
package com.google.code.gtkjfilechooser;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JLabel;


/**
 * @author Costantino Cerbo
 * 
 */
public class GtkArrow extends JLabel {

	static public Icon get(ArrowType type) {
		try {
			Class<?> cls = Class
					.forName("com.sun.java.swing.plaf.gtk.GTKIconFactory");
			String name = "getAscendingSortIcon";
			if (type == ArrowType.DOWN) {
				name = "getDescendingSortIcon";
			}
			Method method = cls.getMethod(name, (Class<?>[])null);
			method.setAccessible(true);
			return (Icon) method.invoke(null, (Object[])null);
		} catch (Throwable e) {
			return null;
		}
	}

	private ArrowType type;

	public GtkArrow(ArrowType type) {
		super(null, get(type), CENTER);
		this.type = type;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gr = (Graphics2D) g.create();

		switch (type) {
		case UP:
			// do nothing: the arrow is already upward
			break;
		case DOWN:
			// do nothing: the arrow is already downward
			break;
		case LEFT:
			gr.translate(0, getPreferredSize().getHeight() + 1);
			gr.transform(AffineTransform.getQuadrantRotateInstance(-1));
			break;
		case RIGHT:
			gr.translate(getPreferredSize().getWidth(), -1);
			gr.transform(AffineTransform.getQuadrantRotateInstance(1));
			break;
		}

		super.paintComponent(gr);
	}
}
