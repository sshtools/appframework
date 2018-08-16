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
     * @param icon icon
     * @param component component
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