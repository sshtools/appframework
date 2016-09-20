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

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import com.google.code.gtkjfilechooser.Log;
import com.google.code.gtkjfilechooser.xbel.RecentlyUsedManager;


public class RecentlyUsedFileWorker extends SwingWorker<Void, Void> implements PropertyChangeListener {

	private static final int NUMBER_OF_RECENT_FILES = 30;

	/**
	 * Manager for the recent used files.
	 */
	private RecentlyUsedManager recentManager;

	private final GtkFileChooserUI fileChooserUI;

	public RecentlyUsedFileWorker(GtkFileChooserUI fileChooserUI) {
		this.fileChooserUI = fileChooserUI;
		addPropertyChangeListener(this);
	}

	@Override
	protected Void doInBackground() throws Exception {
		fileChooserUI.getFileChooser().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		if (recentManager == null) {
			// RecentlyUsedManager objects are expensive: create them
			// only when needed.
			recentManager = new RecentlyUsedManager(NUMBER_OF_RECENT_FILES);
		}
		List<File> fileEntries = recentManager.getRecentFiles();
		// add files in a loop instead of using
		// recentlyUsedPane#setModel:
		// the user see the progress and hasn't the impression that the
		// GUI is frozen.
		fileChooserUI.getRecentlyUsedPane().getModel().clear();
		for (File file : fileEntries) {
			if(fileChooserUI.getFileChooser().getFileFilter().accept(file)){
				fileChooserUI.getRecentlyUsedPane().addFile(file);
			}					
		}

		return null;
	}


	@Override
	protected void done() {
		fileChooserUI.getFileChooser().setCursor(Cursor.getDefaultCursor());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Log.debug(evt);

	}

}


