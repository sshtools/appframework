package com.sshtools.appframework.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang.SystemUtils;

import com.google.code.gtkjfilechooser.ui.GtkFileChooserUI;

public abstract class XFileSelector {

	static {
		if ("GTK look and feel".equals(UIManager.getLookAndFeel().getName())) {
			UIManager.put("FileChooserUI", com.google.code.gtkjfilechooser.ui.GtkFileChooserUI.class.getName());
		}
	}

	static class JFileChooserSelector extends XFileSelector {
		private JFileChooser chooser;

		JFileChooserSelector(String dir) {
			chooser = new JFileChooser(dir) {
				{
					if (SystemUtils.IS_OS_LINUX)
						setUI(new GtkFileChooserUI(this));
				}
			};
		}

		@Override
		public void setMultiSelectionEnabled(boolean multiSelection) {
			chooser.setMultiSelectionEnabled(multiSelection);
		}

		@Override
		public void setFileSelectionMode(int fileSelectionMode) {
			chooser.setFileSelectionMode(fileSelectionMode);
		}

		@Override
		public void setSelectedFile(File file) {
			chooser.setSelectedFile(file);
		}

		@Override
		public void setCurrentDirectory(File file) {
			chooser.setCurrentDirectory(file);
		}

		@Override
		public int showDialog(Component parent, String title) {
			return chooser.showDialog(parent, title);
		}

		@Override
		public File getSelectedFile() {
			return chooser.getSelectedFile();
		}

		@Override
		public File getCurrentDirectory() {
			return chooser.getCurrentDirectory();
		}

		@Override
		public File[] getSelectedFiles() {
			return chooser.getSelectedFiles();
		}

		@Override
		public void setDialogType(int type) {
			chooser.setDialogType(type);

		}
	}

	static class AWTFileSelector extends XFileSelector {

		private FileDialog dialog;
		private boolean multiSelection;
		private int fileSelectionMode;
		private int dialogType;
		private File selectedFile;
		private File selectedDirectory;
		private XFileSelector fallback;

		AWTFileSelector(String dir) {
			selectedDirectory = dir == null ? null : new File(dir);
		}

		@Override
		public void setMultiSelectionEnabled(boolean multiSelection) {
			if (fallback != null) {
				fallback.setMultiSelectionEnabled(multiSelection);
			} else {
				this.multiSelection = multiSelection;
				doSetMultipleMode(multiSelection);
			}
		}

		private void doSetMultipleMode(boolean multiSelection) {
			if (dialog != null) {
				try {
					dialog.getClass().getMethod("setMultipleMode", boolean.class).invoke(dialog, multiSelection);
				} catch (Exception e) {
				}
			}
		}

		@Override
		public void setFileSelectionMode(int fileSelectionMode) {
			if ((fileSelectionMode == DIRECTORIES_ONLY || fileSelectionMode == FILES_AND_DIRECTORIES)
					&& fallback == null) {
				fallback = new JFileChooserSelector(selectedDirectory.getAbsolutePath());
				if (selectedFile != null) {
					fallback.setSelectedFile(selectedFile);
				}
				fallback.setDialogType(dialogType);
				fallback.setMultiSelectionEnabled(multiSelection);
			} else if (fileSelectionMode != DIRECTORIES_ONLY && fallback != null) {
				if (fallback.getSelectedFile() != null) {
					setSelectedFile(fallback.getSelectedFile());
				}
				setDialogType(((JFileChooserSelector) fallback).chooser.getDialogType());
				setMultiSelectionEnabled(((JFileChooserSelector) fallback).chooser.isMultiSelectionEnabled());
				fallback = null;
			}
			if (fallback != null) {
				fallback.setFileSelectionMode(fileSelectionMode);
			} else {
				this.fileSelectionMode = fileSelectionMode;
				createFilter();
			}
		}

		void createFilter() {
			if (dialog != null) {
				switch (fileSelectionMode) {
				case DIRECTORIES_ONLY:
					dialog.setFilenameFilter(new FilenameFilter() {
						public boolean accept(File arg0, String arg1) {
							return arg0.isDirectory();
						}
					});
					break;
				case FILES_ONLY:
					dialog.setFilenameFilter(new FilenameFilter() {
						public boolean accept(File arg0, String arg1) {
							return arg0.isFile();
						}
					});
					break;
				default:
					dialog.setFilenameFilter(new FilenameFilter() {
						public boolean accept(File arg0, String arg1) {
							return true;
						}
					});
					break;
				}
			}
		}

		@Override
		public void setSelectedFile(File file) {
			if (fallback != null) {
				fallback.setSelectedFile(file);
			} else {
				this.selectedFile = file;
				doSetSelectedFile();
			}

		}

		private void doSetSelectedFile() {
			if (dialog != null) {
				String path = selectedFile == null ? null : selectedFile.getPath();
				dialog.setFile(path);
			}
		}

		@Override
		public void setCurrentDirectory(File file) {
			if (fallback != null) {
				fallback.setCurrentDirectory(file);
			} else {
				this.selectedDirectory = file;
				doSetCurrentDirectory();
			}
		}

		private void doSetCurrentDirectory() {
			if (dialog != null) {
				dialog.setDirectory(selectedDirectory == null ? null : selectedDirectory.getAbsolutePath());
			}
		}

		@Override
		public int showDialog(Component parent, String title) {
			if (fallback != null) {
				return fallback.showDialog(parent, title);
			} else {
				Window window = SwingUtilities.getWindowAncestor(parent);
				if (window instanceof Frame) {
					dialog = new FileDialog((Frame) window);
				} else if (window instanceof Dialog) {
					dialog = new FileDialog((Dialog) window);
				} else {
					dialog = new FileDialog((Dialog) null);
				}
				createFilter();
				doSetCurrentDirectory();
				doSetSelectedFile();
				doSetMultipleMode(multiSelection);
				doSetDialogType();
				dialog.setVisible(true);
				if (dialog.getFile() == null) {
					selectedFile = null;
					return CANCEL_OPTION;
				}
				return APPROVE_OPTION;
			}
		}

		@Override
		public File getSelectedFile() {
			if (fallback != null) {
				return fallback.getSelectedFile();
			} else {
				String fileName = dialog.getFile();
				if (fileName == null) {
					return selectedFile;
				}
				String dirName = dialog.getDirectory();
				if (dirName == null) {
					return new File(fileName);
				} else {
					return new File(new File(dirName), fileName);
				}
			}
		}

		@Override
		public File getCurrentDirectory() {
			if (fallback != null) {
				return fallback.getCurrentDirectory();
			} else {
				return dialog == null || dialog.getDirectory() == null ? selectedDirectory
						: new File(dialog.getDirectory());
			}
		}

		@Override
		public File[] getSelectedFiles() {
			if (fallback != null) {
				return fallback.getSelectedFiles();
			} else {
				if (dialog != null) {
					try {
						return (File[]) dialog.getClass().getMethod("getFiles").invoke(dialog);
					} catch (Exception e) {

					}
				}
				File f = getSelectedFile();
				if (f != null) {
					return new File[] { f };
				}
				return null;
			}
		}

		@Override
		public void setDialogType(int type) {
			if (fallback != null) {
				fallback.setDialogType(type);
			} else {
				this.dialogType = type;
				doSetDialogType();
			}

		}

		private void doSetDialogType() {
			if (dialog != null) {
				dialog.setMode(dialogType == OPEN_DIALOG ? FileDialog.LOAD : FileDialog.SAVE);
			}
		}

	}

	// These are same as JFileChooser for ease of implementation
	public static final int CANCEL_OPTION = 1;
	public static final int APPROVE_OPTION = 0;
	public static final int ERROR_OPTION = -1;
	public static final int FILES_ONLY = 0;
	public static final int DIRECTORIES_ONLY = 1;
	public static final int FILES_AND_DIRECTORIES = 2;
	public static final int OPEN_DIALOG = 0;
	public static final int SAVE_DIALOG = 1;

	public void open() {
	}

	public static XFileSelector create(String dir) {
		return new JFileChooserSelector(dir);
	}

	public abstract void setMultiSelectionEnabled(boolean multiSelection);

	public abstract void setFileSelectionMode(int fileSelectionMode);

	public abstract void setSelectedFile(File file);

	public abstract void setCurrentDirectory(File file);

	public abstract int showDialog(Component parent, String title);

	public abstract File getSelectedFile();

	public abstract File getCurrentDirectory();

	public abstract File[] getSelectedFiles();

	public abstract void setDialogType(int openDialog);
}
