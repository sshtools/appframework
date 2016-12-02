package com.sshtools.appframework.ui;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public interface XFileChooser<F> {

	// These are same as JFileChooser for ease of implementation
	public static final int CANCEL_OPTION = 1;
	public static final int APPROVE_OPTION = 0;
	public static final int ERROR_OPTION = -1;
	public static final int FILES_ONLY = 0;
	public static final int DIRECTORIES_ONLY = 1;
	public static final int FILES_AND_DIRECTORIES = 2;
	public static final int OPEN_DIALOG = 0;
	public static final int SAVE_DIALOG = 1;

	void setMultiSelectionEnabled(boolean multiSelection);

	void setFileSelectionMode(int fileSelectionMode);

	void setSelectedFile(F file);

	void setCurrentDirectory(F file);

	int showDialog(Component parent, String title);

	F getSelectedFile();

	F getCurrentDirectory();

	F[] getSelectedFiles();

	void setDialogType(int openDialog);

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

}
