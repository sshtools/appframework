/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/**
 * 
 */
package com.sshtools.appframework.ui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;

public class LookAndFeelModel extends DefaultComboBoxModel {

	public LookAndFeelModel() {
		addElement(new UIManager.LookAndFeelInfo("Automatic", "automatic"));
		for (UIManager.LookAndFeelInfo info : SshToolsApplication
			.getAllLookAndFeelInfo()) {
			addElement(info);
		}
	}

	public UIManager.LookAndFeelInfo getElementForName(String lafClassName) {
		for (int i = 0; i < getSize(); i++) {
			UIManager.LookAndFeelInfo laf = (UIManager.LookAndFeelInfo) getElementAt(i);
			if (laf.getClassName().equals(lafClassName)) {
				return laf;
			}
		}
		return getElementForName("automatic");
	}
}