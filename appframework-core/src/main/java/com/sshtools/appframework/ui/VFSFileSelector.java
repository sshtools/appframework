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
package com.sshtools.appframework.ui;

import java.awt.Component;

import org.apache.commons.vfs2.FileObject;

import com.googlecode.vfsjfilechooser2.VFSJFileChooser;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.DIALOG_TYPE;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.RETURN_TYPE;
import com.googlecode.vfsjfilechooser2.VFSJFileChooser.SELECTION_MODE;

public class VFSFileSelector implements XFileChooser<FileObject> {

	private VFSJFileChooser fileChooser;

	public VFSFileSelector(FileObject home) {
		fileChooser = new VFSJFileChooser();
	}

	@Override
	public FileObject getCurrentDirectory() {
		return fileChooser.getCurrentDirectoryObject();
	}

	@Override
	public FileObject getSelectedFile() {
		return fileChooser.getSelectedFileObject();
	}

	@Override
	public FileObject[] getSelectedFiles() {
		return fileChooser.getSelectedFileObjects();
	}

	@Override
	public void setCurrentDirectory(FileObject file) {
		fileChooser.setCurrentDirectoryObject(file);
	}

	@Override
	public void setDialogType(int openDialog) {
		switch (openDialog) {
		case OPEN_DIALOG:
			fileChooser.setDialogType(DIALOG_TYPE.OPEN);
			break;
		case SAVE_DIALOG:
			fileChooser.setDialogType(DIALOG_TYPE.SAVE);
			break;
		}
	}

	@Override
	public void setFileSelectionMode(int fileSelectionMode) {
		switch (fileSelectionMode) {
		case FILES_ONLY:
			fileChooser.setFileSelectionMode(SELECTION_MODE.FILES_ONLY);
			break;
		case FILES_AND_DIRECTORIES:
			fileChooser.setFileSelectionMode(SELECTION_MODE.FILES_AND_DIRECTORIES);
			break;
		case DIRECTORIES_ONLY:
			fileChooser.setFileSelectionMode(SELECTION_MODE.DIRECTORIES_ONLY);
			break;
		}
	}

	@Override
	public void setMultiSelectionEnabled(boolean multiSelection) {
		fileChooser.setMultiSelectionEnabled(multiSelection);
	}

	@Override
	public void setSelectedFile(FileObject file) {
		fileChooser.setSelectedFileObject(file);
	}

	@Override
	public int showDialog(Component parent, String title) {

		// show the file dialog
		RETURN_TYPE answer = fileChooser.showOpenDialog(null);

		// check if a file was selected
		if (answer == RETURN_TYPE.APPROVE) {
			return APPROVE_OPTION;
		}
		return CANCEL_OPTION;
	}

}
