/*
 *  SSHTools - Java SSH2 API
 *
 *  Copyright (C) 2002 Lee David Painter.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  You may also distribute it and/or modify it under the terms of the
 *  Apache style J2SSH Software License. A copy of which should have
 *  been provided with the distribution.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  License document supplied with your distribution for more details.
 *
 */

package com.sshtools.appframework.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JPanel;

import com.sshtools.ui.swing.ResourceIcon;

public class ImagePanel extends JPanel {

    private Icon icon;
    private boolean alignBottomRight = false;

    public ImagePanel(String imageName) {
        icon = new ResourceIcon(getClass(), imageName);
        setOpaque(false);
    }

    public ImagePanel(Icon icon) {
        this.icon = icon;
        setOpaque(false);
    }
    
    public Dimension getPreferredSize() {
        Insets insets = getInsets();
        return new Dimension(icon.getIconWidth() + insets.left + insets.right, icon.getIconHeight() + insets.top + insets.bottom);

    }
    
    public Dimension getMinimumSize() {
        return getPreferredSize();

    }

    public void paintComponent(Graphics g) {
        Insets insets = getInsets();
        if (!alignBottomRight) {
            // Paint the image at the top left hand side of the panel
            icon.paintIcon(this, g, insets.left, insets.top);
        } else {
            // Paint the image at the bottom right hand side of the panel
            icon.paintIcon(this, g, (this.getWidth() - icon.getIconWidth()), (this.getHeight() - icon.getIconHeight()));
        }
        super.paintComponent(g);

    }
}