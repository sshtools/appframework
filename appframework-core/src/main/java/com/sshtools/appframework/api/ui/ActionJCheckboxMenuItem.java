package com.sshtools.appframework.api.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBoxMenuItem;

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
	}
}