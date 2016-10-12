/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.google.code.gtkjfilechooser.ui;

import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class RenameErrorMessageDialog {

	private final String renameErrorTitleText;
	private final String renameErrorText;
	private final String renameErrorFileExistsText;

	private JFileChooser chooser;

	public RenameErrorMessageDialog(JFileChooser chooser) {
		this.chooser = chooser;

		Locale locale = chooser.getLocale();
		renameErrorTitleText = UIManager.getString("FileChooser.renameErrorTitleText",
				locale);
		renameErrorText = UIManager.getString("FileChooser.renameErrorText", locale);
		renameErrorFileExistsText = UIManager.getString(
				"FileChooser.renameErrorFileExistsText", locale);
	}

	/**
	 * Show error file already exists.
	 * @param oldFileName
	 */
	public void showRenameErrorFileExists(String oldFileName) {
		JOptionPane.showMessageDialog(chooser, MessageFormat.format(
				renameErrorFileExistsText, oldFileName), renameErrorTitleText,
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Show generic rename error.
	 * @param oldFileName
	 */
	public void showRenameError(String oldFileName) {
		JOptionPane.showMessageDialog(chooser, MessageFormat.format(renameErrorText,
				oldFileName), renameErrorTitleText, JOptionPane.ERROR_MESSAGE);
	}

}
