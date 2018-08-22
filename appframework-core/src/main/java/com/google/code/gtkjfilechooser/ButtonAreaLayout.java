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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * <code>ButtonAreaLayout</code> behaves in a similar manner to
 * <code>FlowLayout</code>. It lays out all components from left to right,
 * flushed right. The widths of all components will be set to the largest
 * preferred size width.
 */
public class ButtonAreaLayout implements LayoutManager {
	
	/**
	 * Gap between add/remove and open/cancel buttons.
	 */
	public static final int hGap = 6;
	
	private int topMargin = 17;

	@Override
	public void addLayoutComponent(String string, Component comp) {
	}

	@Override
	public void layoutContainer(Container container) {
		Component[] children = container.getComponents();

		if (children != null && children.length > 0) {
			int numChildren = children.length;
			Dimension[] sizes = new Dimension[numChildren];
			Insets insets = container.getInsets();
			int yLocation = insets.top + topMargin;
			int maxWidth = 0;

			for (int counter = 0; counter < numChildren; counter++) {
				sizes[counter] = children[counter].getPreferredSize();
				maxWidth = Math.max(maxWidth, sizes[counter].width);
			}
			int xLocation, xOffset;
			if (container.getComponentOrientation().isLeftToRight()) {
				xLocation = container.getSize().width - insets.left - maxWidth;
				xOffset = hGap + maxWidth;
			} else {
				xLocation = insets.left;
				xOffset = -(hGap + maxWidth);
			}
			for (int counter = numChildren - 1; counter >= 0; counter--) {
				children[counter].setBounds(xLocation, yLocation, maxWidth,
						sizes[counter].height);
				xLocation -= xOffset;
			}
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container c) {
		if (c != null) {
			Component[] children = c.getComponents();

			if (children != null && children.length > 0) {
				int numChildren = children.length;
				int height = 0;
				Insets cInsets = c.getInsets();
				int extraHeight = topMargin + cInsets.top + cInsets.bottom;
				int extraWidth = cInsets.left + cInsets.right;
				int maxWidth = 0;

				for (int counter = 0; counter < numChildren; counter++) {
					Dimension aSize = children[counter].getPreferredSize();
					height = Math.max(height, aSize.height);
					maxWidth = Math.max(maxWidth, aSize.width);
				}
				return new Dimension(extraWidth + numChildren * maxWidth
						+ (numChildren - 1) * hGap, extraHeight + height);
			}
		}
		return new Dimension(0, 0);
	}

	@Override
	public Dimension preferredLayoutSize(Container c) {
		return minimumLayoutSize(c);
	}

	@Override
	public void removeLayoutComponent(Component c) {
	}
}
