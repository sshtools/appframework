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
import java.util.HashMap;
import java.util.Map;

public interface XFileChooser<F> {

	public static class Chooser {
		private static Map<Class<?>, Class<? extends XFileChooser<?>>> chooserImpls = new HashMap<>();

		public static <T> void addChoserImpl(Class<T> fileClass, Class<? extends XFileChooser<T>> chooser) {
			chooserImpls.put(fileClass, chooser);
		}

		@SuppressWarnings("unchecked")
		public static <T> XFileChooser<T> create(Class<T> clazz, T home) {
			try {
				Class<? extends XFileChooser<?>> implClazz = chooserImpls.get(clazz);
				if (implClazz == null)
					throw new Exception("No impl for class " + clazz);
				return (XFileChooser<T>) implClazz.getConstructor(clazz).newInstance(home);
			} catch (Exception e) {
				throw new IllegalArgumentException("Failed to create chooser.", e);
			}
		}
	}
	public static final int APPROVE_OPTION = 0;
	// These are same as JFileChooser for ease of implementation
	public static final int CANCEL_OPTION = 1;
	public static final int DIRECTORIES_ONLY = 1;
	public static final int ERROR_OPTION = -1;
	public static final int FILES_AND_DIRECTORIES = 2;
	public static final int FILES_ONLY = 0;
	public static final int OPEN_DIALOG = 0;

	public static final int SAVE_DIALOG = 1;

	F getCurrentDirectory();

	F getSelectedFile();

	F[] getSelectedFiles();

	void setCurrentDirectory(F file);

	void setDialogType(int openDialog);

	void setFileSelectionMode(int fileSelectionMode);

	void setMultiSelectionEnabled(boolean multiSelection);

	void setSelectedFile(F file);

	int showDialog(Component parent, String title);

}
