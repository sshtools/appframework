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
package com.google.code.gtkjfilechooser;

import java.io.File;
import java.io.FileFilter;

import com.google.code.gtkjfilechooser.FileSearch.FileSearchHandler.Status;


/**
 * Find files and directories than contain the searched term (case insensitive).
 * 
 * @author c.cerbo
 * 
 */
public class FileSearch {
	private String targetdir;
	private String searchterm;
	private FileFilter fileFilter;
	private FileSearchHandler handler;

	private boolean searchHidden = false;
	private boolean stop = false;
	private boolean interrupted = false;

	public FileSearch(String targetdir, String searchterm, FileSearchHandler handler) {
		this.targetdir = targetdir;
		this.searchterm = searchterm.toLowerCase();
		this.handler = handler;
	}

	/**
	 * Set {@code true} if you want to search hidden files or in hidden folders,
	 * too.
	 * 
	 * @param searchHidden
	 */
	public void setSearchHidden(boolean searchHidden) {
		this.searchHidden = searchHidden;
	}
	
	/**
	 * Set a file filter for the search. If {@code null}, all files are accepted.
	 * @param fileFilter
	 */
	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;		
	}

	/**
	 * Stop the current search.
	 */
	public void stop() {
		this.stop = true;

	}

	/**
	 * Start the search in background. The in-progress results are returned to
	 * the {@link FileSearchHandler}.
	 */
	public void start() {
		if (stop) {
			throw new IllegalStateException(
					"This search was interruped or has completed. "
							+ "For a new search you must create a new instance.");
		}

		Thread scanFilesThread = new Thread(new Runnable() {
			@Override
			public void run() {
				scanFiles(new File(targetdir));

				if (!interrupted) {
					// the search was already interrupted, we don't need to
					// resend the signal.
					// The search has completed
					handler.finished(Status.COMPLETED);
				}
			}
		});

		scanFilesThread.start();
	}

	private void scanFiles(File file) {
		if (stop) {
			if (!interrupted) {
				// the search was already interrupted, we don't need to resend
				// the signal.
				interrupted = true;
				handler.finished(Status.INTERRUPTED);
			}

			return;
		}

		if (file.getName().toLowerCase().contains(searchterm)
				&& (searchHidden || !isHidden(file))
				&& (fileFilter != null && fileFilter.accept(file))) {
			handler.found(file);
		}

		if (file.isDirectory() && (searchHidden || !isHidden(file))) {
			File[] children = file.listFiles();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					scanFiles(children[i]);
				}
			}
		}
	}

	/**
	 * This method resolves a bug in the JDK: the current path (.) is wrongly
	 * interpreted as an hidden file.
	 */
	private boolean isHidden(File file) {
		if (".".equals(file.getName())) {
			return false;
		}

		return file.isHidden();
	}

	/**
	 * Inner class FileSearch
	 * 
	 */
	public interface FileSearchHandler {
		public enum Status {COMPLETED, INTERRUPTED};

		/**
		 * Method invoked when a file is found.
		 * 
		 * @param file
		 */
		public void found(File file);

		/**
		 * Method invoked when the search has completed ({@link Status#COMPLETED}) or was
		 * interrupted ({@link Status#INTERRUPTED}).
		 */
		public void finished(Status status);
	}


}
