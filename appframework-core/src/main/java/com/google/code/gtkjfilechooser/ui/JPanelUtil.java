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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JPanelUtil {

	/**
	 * Create a panel using the given {@link LayoutManager} and adding the
	 * {@link Component}s in the order they are passed.
	 * 
	 * @param layoutManager
	 * @param components
	 * @return The desired panel
	 */
	static public JPanel createPanel(LayoutManager layoutManager, Component... components) {
		JPanel panel = new JPanel(layoutManager);
		for (Component component : components) {
			panel.add(component);
		}

		return panel;
	}

	static public JPanel createPanelBoxLayout(int axis, Component... components) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, axis));
		for (Component component : components) {
			panel.add(component);
		}

		return panel;
	}

	static public JPanel createPanelBoxLayout(Component... components) {
		int axis = BoxLayout.X_AXIS;
		return createPanelBoxLayout(axis, components);
	}

	static public JPanel createPanel(Component... components) {
		return createPanel(new FlowLayout(), components);
	}

	/**
	 * Create a panel using {@link BorderLayout} and adding the
	 * {@link Component}s in the specified position.
	 * 
	 * @param panelElements
	 * @return The desired panel
	 */
	static public JPanel createPanel(int hgap, int vgap, PanelElement... panelElements) {
		JPanel panel = new JPanel(new BorderLayout(hgap, vgap));
		for (PanelElement elem : panelElements) {
			panel.add(elem.component, elem.position);
		}

		return panel;
	}
	static public JPanel createPanel(PanelElement... panelElements) {
		return createPanel(0, 0, panelElements);
	}


	static public class PanelElement {
		private Component component;
		private String position;

		/**
		 * Build a new PanelElement
		 * 
		 * @param component a Component.
		 * @param position A {@link BorderLayout} position, for example {@link BorderLayout#CENTER}.
		 */
		public PanelElement(Component component, String position) {
			super();
			this.component = component;
			this.position = position;
		}
	}

	static public void show(JComponent panel){
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.getContentPane().add(panel);	
		f.pack();

		centerOnScreen(f);		
		f.setVisible(true);
	}

	static public void centerOnScreen(Component comp){
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		int h = comp.getHeight();
		int w = comp.getWidth();

		comp.setLocation((screenWidth-w) / 2, (screenHeight-h) / 2);
	}
}
