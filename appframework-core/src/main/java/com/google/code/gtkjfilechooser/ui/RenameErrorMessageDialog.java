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
