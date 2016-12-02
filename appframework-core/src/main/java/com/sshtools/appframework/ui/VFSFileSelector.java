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
	public void setMultiSelectionEnabled(boolean multiSelection) {
		fileChooser.setMultiSelectionEnabled(multiSelection);
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
	public void setSelectedFile(FileObject file) {
		fileChooser.setSelectedFileObject(file);
	}

	@Override
	public void setCurrentDirectory(FileObject file) {
		fileChooser.setCurrentDirectoryObject(file);
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

	@Override
	public FileObject getSelectedFile() {
		return fileChooser.getSelectedFileObject();
	}

	@Override
	public FileObject getCurrentDirectory() {
		return fileChooser.getCurrentDirectoryObject();
	}

	@Override
	public FileObject[] getSelectedFiles() {
		return fileChooser.getSelectedFileObjects();
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

}
