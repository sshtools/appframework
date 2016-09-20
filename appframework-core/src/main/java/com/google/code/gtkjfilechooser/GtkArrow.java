/*
 * Copyright 2010 Costantino Cerbo.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact me at c.cerbo@gmail.com if you need additional information or
 * have any questions.
 */
package com.google.code.gtkjfilechooser;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.sun.java.swing.plaf.gtk.GTKConstants.ArrowType;

/**
 * @author Costantino Cerbo
 * 
 */
public class GtkArrow extends JLabel {

	private ArrowType type;

	public GtkArrow(ArrowType type) {
		super(null, get(type), CENTER);
		this.type = type;
	}

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
