/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
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
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(AppAction.IS_SELECTED)) {
					setSelected(((Boolean) evt.getNewValue())
							.booleanValue());
				}
			}
		});
	}
}