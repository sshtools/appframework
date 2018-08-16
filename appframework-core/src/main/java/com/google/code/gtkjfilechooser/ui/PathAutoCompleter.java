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

import static javax.swing.JFileChooser.DIRECTORIES_ONLY;

import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

/**
 * Autocompleter decorator for path locations
 * 
 * @author c.cerbo
 * 
 */
public class PathAutoCompleter extends Autocompleter {

	private javax.swing.filechooser.FileFilter currentFilter;
	private String currentPath;
	private int fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;
	private boolean showHidden = false;

	public PathAutoCompleter(JTextField comp) {
		super(comp);
		setCurrentPath(System.getProperty("user.dir"));
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentFilter(javax.swing.filechooser.FileFilter currentFilter) {
		this.currentFilter = currentFilter;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	/**
	 * Sets the auto completion to allow the user to justselect files, just
	 * select directories, or select both files and directories. The default is
	 * <code>JFileChooser.FILES_ONLY</code>.
	 * 
	 * @param fileSelectionMode
	 *            the type of files to be displayed:
	 *            <ul>
	 *            <li>JFileChooser.FILES_ONLY</li>
	 *            <li>JFileChooser.DIRECTORIES_ONLY</li>
	 *            <li>JFileChooser.FILES_AND_DIRECTORIES</li>
	 *            </ul>
	 * 
	 * @exception IllegalArgumentException
	 *                if <code>mode</code> is an illegal file selection mode
	 */
	public void setFileSelectionMode(int fileSelectionMode) {
		if (fileSelectionMode < 0  || fileSelectionMode > 2) {
			throw new IllegalArgumentException("Value " + fileSelectionMode + " is invalid.");
		}
		this.fileSelectionMode = fileSelectionMode;
	}

	/**
	 * Show hidden files?
	 * 
	 * @param showHidden show hidden
	 */
	public void setShowHidden(boolean showHidden) {
		this.showHidden = showHidden;
	}

	@Override
	protected ListCellRenderer getCellRenderer() {
		return new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);

				String suggestion = (String) value;
				int lastIndexOf = suggestion.lastIndexOf(File.separatorChar);
				if (lastIndexOf < suggestion.length() - 1) {
					setText(suggestion.substring(lastIndexOf + 1));
				}
				return this;
			}
		};
	}

	@Override
	protected List<String> updateSuggestions(String value) {
		File file = isAbsolute(value) ? new File(value) : new File(getCurrentPath()
				+ File.separator + value);

		final String prefix = file.getName();
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (!showHidden && pathname.isHidden()) {
					return false;
				}

				if (fileSelectionMode == DIRECTORIES_ONLY && !pathname.isDirectory()) {
					return false;
				}

				// Take care of the choosableFilter set in the combo box
				if (currentFilter != null) {
					if(!currentFilter.accept(pathname)){
						return false;
					}
				}

				String name = pathname.getName();
				return name != null ? name.startsWith(prefix) : false;
			}
		};

		File[] list = null;
		if (file.isDirectory()) {
			list = file.listFiles(filter);
		} else if (file.getParentFile() != null) {
			list = file.getParentFile().listFiles(filter);
		}

		if (list == null || list.length == 0) {
			return null;
		}

		List<String> results = new ArrayList<String>();

		int index = value.lastIndexOf(File.separatorChar);
		String before = (index != -1) ? value.substring(0, index + 1) : "";
		for (File f : list) {
			String name = f.getName();
			if (f.isDirectory()) {
				// add slash if directory
				name = name + File.separator;
			}
			results.add(before + name);
		}

		Collections.sort(results);
		return results;
	}

	private boolean isAbsolute(String value) {
		return new File(value).isAbsolute();
	}

}
