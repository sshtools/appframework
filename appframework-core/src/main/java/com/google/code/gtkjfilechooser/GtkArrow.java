/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
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
