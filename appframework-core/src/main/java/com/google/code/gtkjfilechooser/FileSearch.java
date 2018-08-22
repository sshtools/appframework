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
	/**
	 * Inner class FileSearch
	 * 
	 */
	public interface FileSearchHandler {
		public enum Status {
			COMPLETED, INTERRUPTED
		};

		/**
		 * Method invoked when the search has completed
		 * ({@link Status#COMPLETED}) or was interrupted
		 * ({@link Status#INTERRUPTED}).
		 * 
		 * @param status status
		 */
		public void finished(Status status);

		/**
		 * Method invoked when a file is found.
		 * 
		 * @param file file
		 */
		public void found(File file);
	}
	private FileFilter fileFilter;
	private FileSearchHandler handler;
	private boolean interrupted = false;
	private boolean searchHidden = false;
	private String searchterm;
	private boolean stop = false;

	private String targetdir;

	public FileSearch(String targetdir, String searchterm, FileSearchHandler handler) {
		this.targetdir = targetdir;
		this.searchterm = searchterm.toLowerCase();
		this.handler = handler;
	}

	/**
	 * Set a file filter for the search. If {@code null}, all files are
	 * accepted.
	 * 
	 * @param fileFilter file filter
	 */
	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	/**
	 * Set {@code true} if you want to search hidden files or in hidden folders,
	 * too.
	 * 
	 * @param searchHidden search hidden
	 */
	public void setSearchHidden(boolean searchHidden) {
		this.searchHidden = searchHidden;
	}

	/**
	 * Start the search in background. The in-progress results are returned to
	 * the {@link FileSearchHandler}.
	 */
	public void start() {
		if (stop) {
			throw new IllegalStateException(
					"This search was interruped or has completed. " + "For a new search you must create a new instance.");
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

	/**
	 * Stop the current search.
	 */
	public void stop() {
		this.stop = true;
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
		if (file.getName().toLowerCase().contains(searchterm) && (searchHidden || !isHidden(file))
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
}
