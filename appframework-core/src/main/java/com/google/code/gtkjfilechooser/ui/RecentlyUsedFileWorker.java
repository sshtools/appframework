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

	private final GtkFileChooserUI fileChooserUI;

	/**
	 * Manager for the recent used files.
	 */
	private RecentlyUsedManager recentManager;

	public RecentlyUsedFileWorker(GtkFileChooserUI fileChooserUI) {
		this.fileChooserUI = fileChooserUI;
		addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Log.debug(evt);

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

}


