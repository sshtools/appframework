/**
 * 
 */
package com.sshtools.appframework.ui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;

public class LookAndFeelModel extends DefaultComboBoxModel<UIManager.LookAndFeelInfo> {
	private static final long serialVersionUID = 1L;

	public LookAndFeelModel() {
		addElement(new UIManager.LookAndFeelInfo("Automatic", "automatic"));
		for (UIManager.LookAndFeelInfo info : SshToolsApplication.getAllLookAndFeelInfo()) {
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