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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * 
 * @author $author$
 */
public class IconWrapperPanel extends JPanel {
    private JLabel iconLabel;
    private JPanel westPanel;

    /**
     * Creates a new IconWrapperPanel object.
     * 
     * @param icon
     * @param component
     */
    public IconWrapperPanel(Icon icon, Component component) {
        super(new BorderLayout());
        // Create the west panel with the icon in it
        westPanel = new JPanel(new BorderLayout());
        westPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        westPanel.add(iconLabel = new JLabel(icon), BorderLayout.NORTH);
        // Build this panel
        add(westPanel, BorderLayout.WEST);
        add(component, BorderLayout.CENTER);
    }

    public void setIcon(Icon icon) {
        iconLabel.setIcon(icon);
    }

    public void setIconPosition(String position) {
        invalidate();
        remove(westPanel);
        add(westPanel, position);
        validate();
    }
}