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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JPanel;

import com.sshtools.ui.swing.ResourceIcon;

public class ImagePanel extends JPanel {

    private boolean alignBottomRight = false;
    private Icon icon;

    public ImagePanel(Icon icon) {
        this.icon = icon;
        setOpaque(false);
    }

    public ImagePanel(String imageName) {
        icon = new ResourceIcon(getClass(), imageName);
        setOpaque(false);
    }
    
    @Override
	public Dimension getMinimumSize() {
        return getPreferredSize();

    }
    
    @Override
	public Dimension getPreferredSize() {
        Insets insets = getInsets();
        return new Dimension(icon.getIconWidth() + insets.left + insets.right, icon.getIconHeight() + insets.top + insets.bottom);

    }

    @Override
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