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

import static javax.swing.JFileChooser.SELECTED_FILE_CHANGED_PROPERTY;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel to provide {@link JFileChooser} a preview for images. Simply set it as
 * accessory component:
 * 
 * <pre>
 * JFileChooser chooser = new JFileChooser();
 * chooser.setAccessory(new ImagePreviewer(chooser));
 * </pre>
 * 
 * @author Costantino Cerbo
 * 
 */
public class ImagePreviewer extends JPanel implements PropertyChangeListener {
	private static final int SCALED_WIDTH = 180;
	private static final int OFFSET = 20;
	ImageIcon thumbnail = null;
	private JLabel filenameLabel;
	private JLabel previewLabel;

	public ImagePreviewer(JFileChooser fc) {
		filenameLabel = new JLabel("", JLabel.CENTER);
		previewLabel = new JLabel("", JLabel.CENTER);

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(SCALED_WIDTH + OFFSET, -1));
		fc.addPropertyChangeListener(this);
		setVisible(false);

		add(filenameLabel, BorderLayout.PAGE_START);
		add(previewLabel, BorderLayout.CENTER);
	}

	public void loadImage(File f) {
		if (f == null) {
			thumbnail = null;
		} else {
			ImageIcon tmpIcon = new ImageIcon(f.getPath());
			if (tmpIcon.getIconWidth() > SCALED_WIDTH) {
				Image scaled = tmpIcon.getImage().getScaledInstance(SCALED_WIDTH, -1,
						Image.SCALE_FAST);
				thumbnail = new ImageIcon(scaled);
			} else {
				thumbnail = tmpIcon;
			}

			setVisible(thumbnail.getIconWidth() != -1);
			filenameLabel.setText(f.getName());
			previewLabel.setIcon(thumbnail);
		}
	}

	public void propertyChange(PropertyChangeEvent e) {
		String property = e.getPropertyName();
		if (SELECTED_FILE_CHANGED_PROPERTY.equals(property)) {
			File file = (File) e.getNewValue();
			loadImage(file);
			repaint();
		}
	}
}
