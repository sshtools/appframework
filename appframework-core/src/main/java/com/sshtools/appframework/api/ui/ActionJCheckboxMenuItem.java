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
package com.sshtools.appframework.api.ui;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import com.sshtools.ui.swing.AppAction;

@SuppressWarnings("serial")
public class ActionJCheckboxMenuItem extends JCheckBoxMenuItem {
	public ActionJCheckboxMenuItem(AppAction a) {
		super(a);
		setSelected(Boolean.TRUE.equals(a.getValue(AppAction.IS_SELECTED)));
		a.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(AppAction.IS_SELECTED)) {
					setSelected(((Boolean) evt.getNewValue())
							.booleanValue());
				}
			}
		});
		
		/* BUG: This is work around for the fact that checkbox menu items text 
		 * goes completely transparent on GTK look and feel (and other Synth based ones?)
		 */
		fixLAF(this);
	}

	public static JCheckBoxMenuItem fixLAF(JCheckBoxMenuItem menuItem) {
		Color color = UIManager.getColor("MenuItem.foreground");
		if(color instanceof ColorUIResource) {
			ColorUIResource cui = (ColorUIResource)color;
			if(cui.getTransparency() == 1) {
				color = UIManager.getColor("Label.foreground");
				if(color != null)
					menuItem.setForeground(new Color(color.getRed(), color.getGreen(), color.getBlue()));
			}
		}
		return menuItem;
	}
}