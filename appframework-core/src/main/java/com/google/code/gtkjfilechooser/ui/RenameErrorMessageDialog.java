/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 ******************************************************************************/
package com.google.code.gtkjfilechooser.ui;

import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class RenameErrorMessageDialog {
	private JFileChooser chooser;
	private final String renameErrorFileExistsText;
	private final String renameErrorText;
	private final String renameErrorTitleText;

	public RenameErrorMessageDialog(JFileChooser chooser) {
		this.chooser = chooser;
		Locale locale = chooser.getLocale();
		renameErrorTitleText = UIManager.getString("FileChooser.renameErrorTitleText", locale);
		renameErrorText = UIManager.getString("FileChooser.renameErrorText", locale);
		renameErrorFileExistsText = UIManager.getString("FileChooser.renameErrorFileExistsText", locale);
	}

	/**
	 * Show generic rename error.
	 * 
	 * @param oldFileName old file name
	 */
	public void showRenameError(String oldFileName) {
		JOptionPane.showMessageDialog(chooser, MessageFormat.format(renameErrorText, oldFileName), renameErrorTitleText,
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Show error file already exists.
	 * 
	 * @param oldFileName old file name
	 */
	public void showRenameErrorFileExists(String oldFileName) {
		JOptionPane.showMessageDialog(chooser, MessageFormat.format(renameErrorFileExistsText, oldFileName), renameErrorTitleText,
				JOptionPane.ERROR_MESSAGE);
	}
}
