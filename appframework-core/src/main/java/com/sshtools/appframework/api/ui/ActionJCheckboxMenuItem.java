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
				color = UIManager.getColor("black");
				if(color != null)
					menuItem.setForeground(new Color(color.getRed(), color.getGreen(), color.getBlue()));
			}
		}
		return menuItem;
	}
}