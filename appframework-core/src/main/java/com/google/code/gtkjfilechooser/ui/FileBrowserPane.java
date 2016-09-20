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

import static com.google.code.gtkjfilechooser.I18N._;
import static com.google.code.gtkjfilechooser.ui.ContextMenu.ACTION_ADD_BOOKMARK;
import static com.google.code.gtkjfilechooser.ui.ContextMenu.SHOW_SIZE_COLUMN_CHANGED_PROPERTY;
import static javax.swing.JFileChooser.*;
import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileView;
import javax.swing.table.TableCellEditor;

import com.google.code.gtkjfilechooser.AcceptAllFileFilter;
import com.google.code.gtkjfilechooser.FileFilterWrapper;
import com.google.code.gtkjfilechooser.GtkFileChooserSettings;


/**
 * File browser
 * 
 * @author Costantino Cerbo
 * 
 */

public class FileBrowserPane extends FilesListPane {

	private File currentDir;

	private boolean showHidden = GtkFileChooserSettings.get().getShowHidden();
	private boolean showBackup = UIManager.getBoolean(GtkFileChooserUI.PROP_FILE_CHOOSER_SHOW_BACKUP);

	private int fileSelectionMode = FILES_ONLY;

	private javax.swing.filechooser.FileFilter currentFilter = new AcceptAllFileFilter();

	private ContextMenu contextMenu;

	public FileBrowserPane(File startDir, FileView fileView) {
		super(fileView);
		setCurrentDir(startDir);

		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();

				if (FilesListPane.DOUBLE_CLICK.equals(cmd)) {
					maybeApproveSelection();
				}
			}
		});

		// Move to FilesListPane
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (table.getSelectedRowCount() > 1) {
					firePropertyChange(SELECTED_FILES_CHANGED_PROPERTY, null,
							getSelectedFiles());
				} else {
					firePropertyChange(SELECTED_FILE_CHANGED_PROPERTY, null,
							getSelectedFile());
				}
			}
		});

		bindKeyAction();

		doMultiSelectionEnabledChanged(false);

		addMouseListener();
	}

	private void addMouseListener() {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				int index = table.rowAtPoint(evt.getPoint());

				if (SwingUtilities.isRightMouseButton(evt)) {
					// on right click reset the selections
					table.getSelectionModel().setSelectionInterval(index, index);

					if (contextMenu == null) {
						createContextMenu();
					}
					boolean enabled = getSelectedFile() != null
					&& getSelectedFile().isDirectory();
					contextMenu.setAddToBookmarkMenuItemEnabled(enabled);
					contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});
	}

	private void bindKeyAction() {
		// On enter pressed, approve the selection or go into the selected dir.
		bind(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), new AbstractAction(
		"maybeApproveSelection") {
			@Override
			public void actionPerformed(ActionEvent e) {
				maybeApproveSelection();
			}
		});
	}

	private void bind(KeyStroke key, Action action) {
		String name = (String) action.getValue(Action.NAME);
		if (name == null) {
			throw new IllegalArgumentException("The action must have a name.");
		}

		table.getInputMap().put(key, name);
		table.getActionMap().put(name, action);
	}

	private void listDirectory(File dir, javax.swing.filechooser.FileFilter swingFilter) {
		if (!dir.exists()) {
			throw new IllegalArgumentException(dir + " doesn't exist.");
		}

		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(dir + " isn't a directory.");
		}

		FileFilter filter = new FileFilterWrapper(swingFilter);

		getModel().clear();
		File[] files = dir.listFiles(filter);
		if (files != null) {
			for (File file : files) {
				if (file.isHidden()) {
					if (showHidden) {
						if (isBackup(file)) {
							if (showBackup) {
								getModel().addFile(file);
							}
						} else {
							getModel().addFile(file);	
						}
					}
				} else if (isBackup(file)) {
					if (showBackup) {
						getModel().addFile(file);
					}
				} else {
					getModel().addFile(file);
				}
			}
		}
	}
	
	private boolean isBackup(File file){
		return file.getName().endsWith("~");
	}

	public File getCurrentDir() {
		return currentDir;
	}

	/**
	 * Set the current dir and update the view.
	 * 
	 * @param currentDir
	 */
	public void setCurrentDir(File currentDir) {
		Object oldValue = this.currentDir;
		Object newValue = currentDir;

		this.currentDir = currentDir;

		listDirectory(currentDir, currentFilter);
		firePropertyChange(DIRECTORY_CHANGED_PROPERTY, oldValue, newValue);

		// Scroll to the first row
		table.scrollRectToVisible(table.getCellRect(0, 0, true));
	}
	
	public void refresh() {
		listDirectory(currentDir, currentFilter);
		// Scroll to the first row
		table.scrollRectToVisible(table.getCellRect(0, 0, true));
	}

	public void setShowHidden(boolean showHidden) {
		this.showHidden = showHidden;
	}

	public void setIsMultiSelectionEnabled(boolean enabled) {
		doMultiSelectionEnabledChanged(enabled);
	}

	public void setCurrentFilter(javax.swing.filechooser.FileFilter swingFileFilter) {
		this.currentFilter = swingFileFilter;

		doFileFilerChanged(swingFileFilter);
	}

	public void setFileSelectionMode(int fileSelectionMode) {
		this.fileSelectionMode = fileSelectionMode;
		doFileSelectionModeChanged(fileSelectionMode);
	}

	private void doFileSelectionModeChanged(Integer value) {
		setFilesSelectable(DIRECTORIES_ONLY != value);

		// Repaint the table to immediately enable/disable the rows
		table.repaint();
	}

	private void doFileFilerChanged(javax.swing.filechooser.FileFilter filter) {
		listDirectory(getCurrentDir(), filter);
	}

	private void doMultiSelectionEnabledChanged(Boolean multi) {
		table.setSelectionMode(multi ? MULTIPLE_INTERVAL_SELECTION : SINGLE_SELECTION);
	}

	/**
	 * Approve a selection (with double click or enter) or navigate into another
	 * directory (when the File Selection Mode isn't DIRECTORIES_ONLY).
	 */
	private void maybeApproveSelection() {
		File selectedFile = getSelectedFile();
		if (selectedFile == null) {
			return;
		}

		if (selectedFile.isDirectory()) {
			setCurrentDir(selectedFile);
		} else {
			fireActionEvent(new ActionEvent(FileBrowserPane.this, APPROVE_SELECTION
					.hashCode(), APPROVE_SELECTION));
		}
	}

	/**
	 * List again the entries in the current directory.
	 */
	public void rescanCurrentDirectory() {
		listDirectory(getCurrentDir(), currentFilter);
	}

	private void createContextMenu() {
		// lazy init the popup
		contextMenu = new ContextMenu();
		ContextMenuListener contextMenuListener = new ContextMenuListener();
		contextMenu.addPropertyChangeListener(contextMenuListener);
		contextMenu.addActionListener(contextMenuListener);
	}

	/**
	 * Inner class
	 */
	private class ContextMenuListener implements PropertyChangeListener, ActionListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String property = evt.getPropertyName();
			Object value = evt.getNewValue();

			if (SHOW_SIZE_COLUMN_CHANGED_PROPERTY.equals(property)) {
				boolean showSizeColumn = (Boolean) value;
				setShowSizeColumn(showSizeColumn);
				rescanCurrentDirectory();
			} else if (FILE_HIDING_CHANGED_PROPERTY.equals(property)) {
				boolean hide = (Boolean) value;
				GtkFileChooserSettings.get().setShowHidden(!hide);
				setShowHidden(!hide);
				rescanCurrentDirectory();
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (ACTION_ADD_BOOKMARK.equals(cmd)) {
				fireActionEvent(e);
			}
		}

	}

	public void createFolder() {
		FilesListTableModel model = (FilesListTableModel) table.getModel();

		model.addEmtpyRow();

		// Make the just added first empty cell editable
		model.setEditableRow(0);
		boolean success = table.editCellAt(0, 0);
		model.setEditableRow(-1);

		JTextField editorComponent = (JTextField) table.getEditorComponent();
		if (editorComponent != null) {
			editorComponent.setText(_("Type name of new folder"));
			editorComponent.selectAll();
			editorComponent.requestFocusInWindow();

			// Cancel editing when ESC pressed.
			editorComponent.registerKeyboardAction(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (table.getCellEditor() != null) {
						table.getCellEditor().cancelCellEditing();
					}
				}
			}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);

			// Cancel editing when focus lost.
			editorComponent.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if (table.getCellEditor() != null) {
						table.getCellEditor().cancelCellEditing();
					}
				}
			});			
		}

		if (success) {
			table.changeSelection(0, 0, false, false);
		}

		final TableCellEditor cellEditor = table.getCellEditor();
		if (cellEditor != null) {			
			CellEditorListener cellEditorListener = new CellEditorListener() {

				@Override
				public void editingStopped(ChangeEvent e) {
					// remove before adding to avoid to trigger more times the same event.
					cellEditor.removeCellEditorListener(this);

					// Note that the getCellEditorValue() returns a instance of File 
					// even for empty row if there other files in a dir. Otherwise an
					// instance of String is returned. The reason for this particular
					// behavior is in the method FilesListTableModel#getColumnClass(int).
					Object cellValue = cellEditor.getCellEditorValue();
					String newFolderName = null;
					if (cellValue instanceof File) {
						newFolderName = ((File) cellValue).getName();						
					} else if (cellValue instanceof String) {
						newFolderName = (String) cellValue;
					}

					if (_("Type name of new folder").equals(newFolderName)) {
						// Avoid to create folder with the tip text.
						removeEmtpyRow();
						return;
					}

					File newFolder = new File (getCurrentDir().getAbsolutePath() + File.separator + newFolderName);
					if (newFolder.exists()) {
						JOptionPane.showMessageDialog(FileBrowserPane.this,
								"<html><p width='250px'>" + 
								_("The folder could not be created, as a file with the same name already exists.  Try using a different name for the folder, or rename the file first.") + 
								"</p></html>",
								"",
								JOptionPane.ERROR_MESSAGE);
						removeEmtpyRow();
					} else {
						newFolder.mkdir();
						setCurrentDir(newFolder);						
					}
				}

				@Override
				public void editingCanceled(ChangeEvent e) {
					// remove before adding to avoid to trigger more times the same event.
					cellEditor.removeCellEditorListener(this);

					removeEmtpyRow();
				}

				private void removeEmtpyRow() {
					((FilesListTableModel) table.getModel()).removeEmtpyRow();

				}
			};

			cellEditor.addCellEditorListener(cellEditorListener);
		}

	}
}
