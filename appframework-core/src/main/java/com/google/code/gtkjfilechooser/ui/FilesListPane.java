/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 *     Yuvi Masory - Issue 68
 ******************************************************************************/
package com.google.code.gtkjfilechooser.ui;

import static com.google.code.gtkjfilechooser.I18N.i18n;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;

import com.google.code.gtkjfilechooser.ActionDispatcher;
import com.google.code.gtkjfilechooser.BasicActionDispatcher;
import com.google.code.gtkjfilechooser.DateUtil;
import com.google.code.gtkjfilechooser.FreeDesktopUtil;
import com.google.code.gtkjfilechooser.GtkFileChooserSettings;

public class FilesListPane extends JComponent implements ActionDispatcher {
	public static final Color PEARL_GRAY = new Color(238, 238, 238);
	public static final Color PEARL_GRAY_LIGHT = new Color(221, 221, 221);
	private static final String FILE_NAME_COLUMN_ID = "Name";
	private static final int FILE_NAME_COLUMN_INDEX = 0;
	private static final String FILE_SIZE_COLUMN_ID = "Size";
	private static final int FILE_SIZE_COLUMN_WIDTH = 100;
	private static final String FILE_DATE_COLUMN_ID = "Modified";
	private static final int FILE_DATE_COLUMN_WIDTH = 125;
	public static final String SELECTED = "selected";
	public static final int SELECTED_ID = 1;
	public static final String DOUBLE_CLICK = "double_click";
	public static final int DOUBLE_CLICK_ID = 2;
	public static final String ENTER_PRESSED = "enter pressed";
	public static final int ENTER_PRESSED_ID = 3;
	private static final long serialVersionUID = 1L;
	protected JTable table;
	private ActionDispatcher actionDispatcher = new BasicActionDispatcher();
	private boolean filesSelectable = true;
	/**
	 * {@link FileView} to to retrieve the icon that represents a file and its
	 * name
	 */
	private FileView fileView;

	public FilesListPane(FileView fileView) {
		this(new ArrayList<File>(), fileView);
	}

	public FilesListPane(List<File> fileEntries, FileView fileView) {
		this.fileView = fileView;
		setLayout(new BorderLayout());
		table = new JTable() {
			@Override
			public void changeSelection(int row, int column, boolean toggle, boolean extend) {
				File file = (File) getValueAt(row, 0);
				if (FilesListPane.this.isRowEnabled(file)) {
					// If the row isn't enabled, don't allow the selection.
					super.changeSelection(row, column, toggle, extend);
				}
			}
		};
		table.setColumnModel(new FilesListTableColumnModel());
		table.setAutoCreateColumnsFromModel(false);
		table.setBackground(UIManager.getColor("ScrollPane.background"));
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setBackground(UIManager.getColor("window"));
		table.setIntercellSpacing(new Dimension(0, 0));
		Boolean showSizeColumn = GtkFileChooserSettings.get().getShowSizeColumn();
		setModel(fileEntries, showSizeColumn);
		table.setDefaultRenderer(Object.class, new FilesListRenderer());
		table.setRowSelectionAllowed(true);
		table.setShowGrid(false);
		table.getTableHeader().setResizingAllowed(true);
		// Gnome rows are taller
		table.setRowHeight(23);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ActionEvent event = null;
				if (e.getClickCount() == 2) {
					event = new ActionEvent(FilesListPane.this, DOUBLE_CLICK_ID, DOUBLE_CLICK);
				} else {
					event = new ActionEvent(FilesListPane.this, SELECTED_ID, SELECTED);
				}
				fireActionEvent(event);
			}
		});
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int ch = e.getKeyChar();
				if (ch == KeyEvent.VK_ENTER) {
					fireActionEvent(new ActionEvent(FilesListPane.this, ENTER_PRESSED_ID, ENTER_PRESSED));
				}
			}
		});
		// Add interactive file search support
		new FileFindAction().install(table);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void setFileView(FileView fileView) {
		if (fileView == null) {
			throw new IllegalArgumentException("FileView cannot be null");
		}
		this.fileView = fileView;
	}

	public void setShowSizeColumn(boolean showSizeColumn) {
		GtkFileChooserSettings.get().setShowSizeColumn(showSizeColumn);
		setModel(new ArrayList<File>(), showSizeColumn);
		table.createDefaultColumnsFromModel();
	}

	public void uninstallUI() {
		table = null;
		removeAllActionListeners();
	}

	/**
	 * Sets the table's selection mode to allow only single selections, a single
	 * contiguous interval, or multiple intervals.
	 * 
	 * @see JList#setSelectionMode
	 */
	public void setSelectionMode(int selectionMode) {
		table.setSelectionMode(selectionMode);
	}

	/**
	 * Append a new {@link File} to this table.Notification of the row being
	 * added will be generated.
	 * 
	 * @param entry the {@link File} to be inserted.
	 */
	public void addFile(File entry) {
		FilesListTableModel dataModel = (FilesListTableModel) table.getModel();
		dataModel.addFile(entry);
	}

	/**
	 * Set if the the files are enabled/selectable; for example when
	 * FileSelectionMode = DIRECTORIES_ONLY.
	 * 
	 * @param filesSelectable
	 */
	public void setFilesSelectable(boolean filesSelectable) {
		this.filesSelectable = filesSelectable;
	}

	public void setModel(List<File> fileEntries, Boolean showSizeColumn) {
		FilesListTableModel dataModel = new FilesListTableModel(fileEntries, showSizeColumn);
		table.setModel(dataModel);
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		FilesListTableRowSorter sorter = new FilesListTableRowSorter();
		sorter.setSortKeys(sortKeys);
		table.setRowSorter(sorter);
		createColumnsFromModel();
	}

	private void createColumnsFromModel() {
		FilesListTableModel m = (FilesListTableModel) table.getModel();
		if (m != null) {
			// Remove any current columns
			TableColumnModel cm = table.getColumnModel();
			while (cm.getColumnCount() > 0) {
				cm.removeColumn(cm.getColumn(0));
			}
			// Create new columns from the data model info
			for (int i = 0; i < m.getColumnCount(); i++) {
				TableColumn newColumn = new TableColumn(i);
				newColumn.setIdentifier(m.getColumnId(i));
				table.addColumn(newColumn);
			}
		}
	}

	public FilesListTableModel getModel() {
		return (FilesListTableModel) table.getModel();
	}

	public boolean getShowSizeColumn() {
		return getModel().getShowSizeColumn();
	}

	private boolean isRowEnabled(File file) {
		if (file == null) {
			return false;
		}
		// Directory are always enabled
		if (file.isDirectory()) {
			return true;
		}
		// When FileSelectionMode = DIRECTORIES_ONLY, disable files
		// Use !file.isDirectory() instead of file.isFile() because
		// the last one doesn't return true for links
		return !file.isDirectory() && filesSelectable;
	}

	public File getSelectedFile() {
		int row = table.getSelectedRow();
		if (row == -1) {
			return null;
		}
		return (File) table.getModel().getValueAt(table.convertRowIndexToModel(row), 0);
	}

	public File[] getSelectedFiles() {
		int[] rows = table.getSelectedRows();
		if (rows.length == 0) {
			return null;
		}
		File[] selectesFiles = new File[rows.length];
		for (int i = 0; i < rows.length; i++) {
			int rowIndex = rows[i];
			selectesFiles[i] = (File) table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), 0);
		}
		return selectesFiles;
	}

	public void clearSelection() {
		table.getSelectionModel().clearSelection();
	}

	@Override
	public void addActionListener(ActionListener l) {
		actionDispatcher.addActionListener(l);
	}

	@Override
	public void fireActionEvent(ActionEvent e) {
		actionDispatcher.fireActionEvent(e);
	}

	@Override
	public void removeActionListener(ActionListener l) {
		actionDispatcher.removeActionListener(l);
	}

	@Override
	public void removeAllActionListeners() {
		actionDispatcher.removeAllActionListeners();
	}

	/**
	 * Inner classes
	 */
	protected class FilesListTableModel extends AbstractTableModel implements Serializable, TableModelListener {
		private static final long serialVersionUID = 1L;
		private List<Object[]> data;
		private String[] columnNames;
		private String[] columnIds;
		private boolean showSizeColumn;
		private int editableCellRowIndex = -1;

		public FilesListTableModel(List<File> fileEntries, boolean showSizeColumn) {
			this.data = new ArrayList<Object[]>();
			this.showSizeColumn = showSizeColumn;
			addTableModelListener(this);
			if (getShowSizeColumn()) {
				this.columnIds = new String[] { FILE_NAME_COLUMN_ID, FILE_SIZE_COLUMN_ID, FILE_DATE_COLUMN_ID };
			} else {
				this.columnIds = new String[] { FILE_NAME_COLUMN_ID, FILE_DATE_COLUMN_ID };
			}
			this.columnNames = new String[columnIds.length];
			for (int i = 0; i < columnIds.length; i++) {
				String columnId = columnIds[i];
				columnNames[i] = i18n(columnId);
			}
			for (File file : fileEntries) {
				addFileEntryInternal(file);
			}
		}

		private Boolean getShowSizeColumn() {
			return showSizeColumn;
		}

		public void clear() {
			this.data = new ArrayList<Object[]>();
			fireTableDataChanged();
		}

		private void addFileEntryInternal(File file) {
			Object[] row = new Object[getColumnCount()];
			row[0] = file;
			if (getShowSizeColumn()) {
				if (file != null) {
					if (file.isDirectory()) {
						// if a dir returns the number of contained files
						// as negative number with one added.
						// Negative entries will be not rendered.
						row[1] = file.list() != null ? -file.list().length - 1L : -1L;
					} else {
						row[1] = file.length();
					}
				}
				row[2] = new Date(file.lastModified());
			} else {
				row[1] = new Date(file.lastModified());
			}
			data.add(row);
		}

		/**
		 * Append a new {@link File} to this table.Notification of the row being
		 * added will be generated.
		 * 
		 * @param entry the {@link File} to be inserted.
		 */
		public void addFile(File entry) {
			addFileEntryInternal(entry);
			int row = getRowCount() - 1;
			fireTableRowsInserted(row, row);
		}

		/**
		 * Add an empty row as first row in the table. This row is editable and
		 * it's used when a new folder is created.
		 */
		void addEmtpyRow() {
			Object[] row = new Object[getColumnCount()];
			data.add(0, row);
			fireTableRowsInserted(0, 0);
			// Scroll to the first empty row just added
			table.scrollRectToVisible(table.getCellRect(0, 0, true));
		}

		/**
		 * Remove the first row in the table, if it's an empty row.
		 */
		void removeEmtpyRow() {
			if (data != null && !data.isEmpty() && data.get(0)[0] == null) {
				data.remove(0);
				fireTableRowsDeleted(0, 0);
			}
		}

		// *** TABLE MODEL METHODS ***
		/**
		 * Set the coordinates of an editable cell. Use the value -1 to disable
		 * edit at all.
		 */
		public void setEditableRow(int row) {
			editableCellRowIndex = row != -1 ? table.convertRowIndexToModel(row) : -1;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return row == editableCellRowIndex && column == 0;
		}

		public int getColumnCount() {
			return getShowSizeColumn() ? 3 : 2;
		}

		public int getRowCount() {
			return data.size();
		}

		public Object getValueAt(int row, int col) {
			checkColumnIndex(col);
			return data.get(row)[col];
		}

		@Override
		public String getColumnName(int col) {
			checkColumnIndex(col);
			return columnNames[col];
		}

		/**
		 * Return the unmodifiable not localized column identifier.
		 * 
		 * @param col The model column index.
		 * @return The column identifier.
		 */
		public String getColumnId(int col) {
			checkColumnIndex(col);
			return columnIds[col];
		}

		@Override
		public Class<?> getColumnClass(int col) {
			checkColumnIndex(col);
			if (!data.isEmpty()) {
				if (data.get(0)[col] != null) {
					return data.get(0)[col].getClass();
				} else if (data.size() > 1 && data.get(1) != null && data.get(1)[col] != null) {
					// it happens when the first row in the empty row (for
					// create folder).
					return data.get(1)[col].getClass();
				}
			}
			return Object.class;
		}

		private void checkColumnIndex(int col) {
			if (col >= getColumnCount()) {
				throw new IllegalArgumentException(col + " greater the the column count");
			}
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (table.getRowSorter() != null) {
				table.getRowSorter().allRowsChanged();
			}
		}
	}

	/**
	 * Cell renderer
	 */
	protected class FilesListRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			// reset the icon for all columns
			setIcon(null);
			if (value == null) {
				// It can be null only for the editable cell when
				// we create a new folder in the save mode.
				setText("");
			} else if (value instanceof File) {
				// filename column
				File file = (File) value;
				setText(fileView.getName(file));
				setIcon(fileView.getIcon(file));
			} else if (value instanceof Long) {
				// size column
				Long bytes = (Long) value;
				setText(bytes >= 0 ? FreeDesktopUtil.humanreadble(bytes, 0) : "");
			} else if (value instanceof Date) {
				// last modified column
				Date date = (Date) value;
				setText(DateUtil.toPrettyFormat(date));
			}
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				Color rowcolor = table.getBackground();
				if (row % 2 == 0) {
					float[] hsb = Color.RGBtoHSB(rowcolor.getRed(), rowcolor.getGreen(), rowcolor.getBlue(), null);
					if (hsb[2] > 0.5) {
						rowcolor = rowcolor.brighter();
					} else
						rowcolor = rowcolor.darker().darker();
				}
				setBackground(rowcolor);
			}
			try {
				if (table.getColumnCount() > 0 && table.getModel().getValueAt(row, 0) instanceof File) {
					File file = (File) table.getModel().getValueAt(row, 0);
					setToolTipText(fileView.getDescription(file));
					// enable/disable according to the FileSelectionMode
					setEnabled(FilesListPane.this.isRowEnabled(file));
				}
			} catch (NullPointerException npe) {
				// TODO why ..
				npe.printStackTrace();
			}
			return this;
		}
	}

	private class FilesListTableColumnModel extends DefaultTableColumnModel {
		private static final long serialVersionUID = 1L;

		@Override
		public TableColumn getColumn(int columnIndex) {
			TableColumn col = super.getColumn(columnIndex);
			String columnId = col.getIdentifier().toString();
			if (FILE_SIZE_COLUMN_ID.equals(columnId)) {
				col.setPreferredWidth(FILE_SIZE_COLUMN_WIDTH);
			} else if (FILE_DATE_COLUMN_ID.equals(columnId)) {
				col.setPreferredWidth(FILE_DATE_COLUMN_WIDTH);
			} else {
				// The filename column fills the remaining space.
				int offset = FILE_DATE_COLUMN_WIDTH;
				if (table.getModel().getColumnCount() == 3) {
					offset += FILE_SIZE_COLUMN_WIDTH;
				}
				col.setPreferredWidth(getTotalColumnWidth() - offset);
			}
			return col;
		}
	}

	protected class FilesListTableRowSorter extends TableRowSorter<FilesListTableModel> {
		static final private String PREFIX_FIRST = "01_";
		static final private String PREFIX_LAST = "09_";
		/**
		 * Used only for the empty cell
		 */
		static final private String PREFIX_BEFORE_FIRST = "00_";
		static final private String PREFIX_AFTER_LAST = "99_";

		public FilesListTableRowSorter() {
			super((FilesListTableModel) table.getModel());
		}

		@Override
		protected boolean useToString(int column) {
			return true;
		}

		@Override
		public TableStringConverter getStringConverter() {
			return new TableStringConverter() {
				@Override
				public String toString(TableModel model, int row, int column) {
					return FilesListTableRowSorter.this.toString(model, row, column);
				}
			};
		}

		/**
		 * This method is responsible for the correct sort order. We create a
		 * string that guarantees the desired order.
		 */
		private String toString(TableModel model, int row, int column) {
			File file = (File) model.getValueAt(row, FILE_NAME_COLUMN_INDEX);
			String sortString = null;
			if (file != null) {
				if (file.isDirectory()) {
					// Directories go always first
					sortString = isAscending() ? PREFIX_FIRST : PREFIX_LAST;
				} else {
					sortString = isAscending() ? PREFIX_LAST : PREFIX_FIRST;
				}
			}
			Object value = model.getValueAt(row, column);
			if (value == null) {
				// The empty cell for the folder creation must
				// be always on the top.
				sortString = isAscending() ? PREFIX_BEFORE_FIRST : PREFIX_AFTER_LAST;
			} else if (value instanceof File) {
				sortString += file.getName().toLowerCase();
			} else if (value instanceof Long) {
				Long size = (Long) value;
				// For normal files the it's the byte size, for dirs
				// the number of contained files (this value is passed
				// negative with 1 added).
				size = size > 0 ? size : -1 * size - 1;
				// A Long may have max 19 digits. The 0 prefix guarantees the
				// right order also between entries with a different length.
				sortString = String.format("%s%019d", sortString, size);
			} else if (value instanceof Date) {
				Date modified = (Date) value;
				sortString = String.format("%s%019d", sortString, modified.getTime());
			}
			return sortString.toLowerCase();
		}

		private boolean isAscending() {
			if (getSortKeys().size() > 0) {
				return getSortKeys().get(0).getSortOrder() == SortOrder.ASCENDING;
			}
			return true;
		}
	}
}
