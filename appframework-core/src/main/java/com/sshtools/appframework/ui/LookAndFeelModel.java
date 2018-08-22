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