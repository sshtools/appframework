/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 *     i30817 - Issue 60 (Layout improvements)
 *     Yuvi Masory - Issue 71
 ******************************************************************************/
/*
 * Copyright 2010 Costantino Cerbo.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact me at c.cerbo@gmail.com if you need additional information or
 * have any questions.
 */
package com.google.code.gtkjfilechooser.ui;

import static com.google.code.gtkjfilechooser.ActionPath.RECENTLY_USED_PANEL_ID;
import static com.google.code.gtkjfilechooser.ActionPath.SEARCH_PANEL_ID;
import static com.google.code.gtkjfilechooser.I18N.i18n;
import static com.google.code.gtkjfilechooser.I18N.getMnemonic;
import static com.google.code.gtkjfilechooser.NavigationKeyBinding.*;
import static com.google.code.gtkjfilechooser.ui.ContextMenu.ACTION_ADD_BOOKMARK;
import static com.google.code.gtkjfilechooser.ui.Expander.EXPANDED_STATUS_CHANGED;
import static com.google.code.gtkjfilechooser.ui.JPanelUtil.createPanel;
import static com.google.code.gtkjfilechooser.ui.JPanelUtil.createPanelBoxLayout;
import static com.google.code.gtkjfilechooser.ui.SaveDialogPanel.ACTION_SAVE;
import static javax.swing.JFileChooser.*;
import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

import com.google.code.gtkjfilechooser.ActionPath;
import com.google.code.gtkjfilechooser.ArrayUtil;
import com.google.code.gtkjfilechooser.ButtonAreaLayout;
import com.google.code.gtkjfilechooser.FileFilterWrapper;
import com.google.code.gtkjfilechooser.FocusUtil;
import com.google.code.gtkjfilechooser.FreeDesktopUtil;
import com.google.code.gtkjfilechooser.GtkFileChooserSettings;
import com.google.code.gtkjfilechooser.GtkFileView;
import com.google.code.gtkjfilechooser.GtkStockIcon;
import com.google.code.gtkjfilechooser.GtkVersion;
import com.google.code.gtkjfilechooser.Log;
import com.google.code.gtkjfilechooser.NavigationKeyBinding;
import com.google.code.gtkjfilechooser.Path;
import com.google.code.gtkjfilechooser.BookmarkManager.GtkBookmark;
import com.google.code.gtkjfilechooser.FreeDesktopUtil.WellKnownDir;
import com.google.code.gtkjfilechooser.GtkFileChooserSettings.Mode;
import com.google.code.gtkjfilechooser.GtkStockIcon.Size;
import com.google.code.gtkjfilechooser.filewatcher.FileEvent;
import com.google.code.gtkjfilechooser.filewatcher.FileListener;
import com.google.code.gtkjfilechooser.filewatcher.FileWatcher;
import com.google.code.gtkjfilechooser.ui.JPanelUtil.PanelElement;

public class GtkFileChooserUI extends BasicFileChooserUI implements Serializable, PropertyChangeListener, ActionListener {
	/**
	 * Action to select a file or select/browse a directory (according to the
	 * FileSelectionMode).
	 */
	private abstract class SelectPathAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			File path = getSelectedPath();
			if (path == null) {
				return;
			}
			if (path.isDirectory()) {
				fireChangeDirectoryEvent(path);
				if (getFileChooser().getFileSelectionMode() == JFileChooser.DIRECTORIES_ONLY) {
					getFileChooser().setSelectedFile(path);
					approveSelection();
				}
			} else {
				getFileChooser().setSelectedFile(path);
				approveSelection();
			}
		}

		protected abstract File getSelectedPath();
	}
	/**
	 * Backup files are hidden by default. Set this property to show/hide Backup
	 * files.
	 */
	static public final String PROP_FILE_CHOOSER_SHOW_BACKUP = "FileChooser.showBackup";
	static private final String ACTION_CREATE_FOLDER = "createFolder";
	static private final String ACTION_SELECTED_BOOKMARK = "selected bookmark";
	static private final String ANCESTOR_PROPERTY = "ancestor";
	private static final int BOTTOM_ROW_GAP = 6;
	static private final String COMPONENT_ORIENTATION_PROPERTY = "componentOrientation";
	static private final String CURRENT_PANEL_CHANGED = "CurrentPanelChanged";
	static private final File FILE_GTK_BOOKMARK = new File(System.getProperty("user.home") + File.separator + ".gtk-bookmarks");
	static private final File FILE_MEDIA = new File("/media");
	/**
	 * Names of the "cards" in the rightPanel.
	 */
	static private final String FILEBROWSER_PANEL = "fileBrowserPane";
	static private final int FILEBROWSER_PANEL_ID = 1000;
	private static int PREF_HEIGHT = 326;
	private static int PREF_WIDTH = 700;
	private static int LIST_PREF_HEIGHT = 135;
	private static int LIST_PREF_WIDTH = 405;
	private static int MIN_WIDTH = 700;
	private static int MIN_EXPANDED_HEIGHT = 500;
	private static int MIN_HEIGHT = 200;
	private static Dimension LIST_PREF_SIZE = new Dimension(LIST_PREF_WIDTH, LIST_PREF_HEIGHT);
	private static Dimension MIN_SIZE = new Dimension(MIN_WIDTH, MIN_EXPANDED_HEIGHT);
	private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);
	// Preferred and Minimum sizes for the dialog box
	static private final String RECENTLY_USED_PANEL = "recentlyUsedPane";
	static private final String SEARCH_PANEL = "searchFilesPane";
	private static final long serialVersionUID = 10L;
	/**
	 * Names of the "cards" in the topPanel.
	 */
	static private final String TOP_PATHBAR_PANEL = "Path bar panel on the top";
	static private final String TOP_SEARCH_PANEL = "Search panel on the top";
	private static final int UPPER_BUTTON_GAP = 12;
	/**
	 * ComponentUI Interface Implementation methods
	 * 
	 * @param c component
	 * @return UI
	 */
	public static ComponentUI createUI(JComponent c) {
		GtkFileChooserUI ui = new GtkFileChooserUI((JFileChooser) c);
		return ui;
	}
	private JButton addBookmarkButton;
	private JButton approveButton;
	private JPanel buttonPanel;
	private JButton cancelButton;
	/**
	 * Panel mit CardLayout used to show one of the following three panels: the
	 * File-Browser panel, the Recently-Used panel and the Search panel.
	 */
	private JPanel cardPanel = new JPanel(new CardLayout());
	private JFileChooser chooser;
	private ComponentAdapter chooserComponentListener = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			if (saveDialogPanel != null && !saveDialogPanel.isExpanded()) {
				// Do not persist the size when we are in save
				// mode and the folders aren't expanded.
				return;
			}
			Rectangle bound = e.getComponent().getBounds();
			if (getFileChooser().getDialogType() == JFileChooser.SAVE_DIALOG) {
				// FIXME Why do we need 20px more for the Save dialog?
				bound.height += 20;
			}
			GtkFileChooserSettings.get().setBound(bound);
		}
	};
	private JButton createFolderButton;
	private int currentPanelId = FILEBROWSER_PANEL_ID;
	/**
	 * The height of the dialog in save mode, when the folder view is expanded.
	 */
	private transient int expandedHeight = -1;
	/**
	 * The panel with for the file/directory navigation.
	 */
	private FileBrowserPane fileBrowserPane;
	private int fileNameLabelMnemonic = 0;
	private String fileNameLabelText;
	/**
	 * Panel with the location text field.
	 */
	private JPanel filenamePanel;
	private JTextField fileNameTextField;
	private String filesOfTypeLabelText;
	/**
	 * Combox with file filters.
	 */
	private JComboBox filterComboBox;
	/**
	 * The panel on the left with locations and bookmarks
	 */
	private GtkLocationsPane locationsPane;
	/**
	 * Panel for the open dialog. In the save dialog it's put on the bottom.
	 */
	private JPanel openDialogPanel;
	/**
	 * Decorator for auto completion for #fileNameTextField
	 */
	private PathAutoCompleter pathAutoCompletion;
	private ActionListener pathBarActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			fireChangeDirectoryEvent(pathBarButtons.getCurrentDirectory());
		}
	};
	private GtkPathBar pathBarButtons;
	/**
	 * Table to show the recent used files
	 */
	private FilesListPane recentlyUsedPane;
	private JButton removeBookmarkButton;
	/**
	 * Panel for the save dialog. It contains the {@code openDialogPanel} in an
	 * expandable container.
	 */
	private SaveDialogPanel saveDialogPanel;
	/**
	 * Table to show the results of a search
	 */
	private FilesListPane searchFilesPane;
	/**
	 * Panel on the top with the text field to do the search.
	 */
	private SearchPanel searchPanel;

	/**
	 * Button to enable/disable the location text field.
	 */
	private JToggleButton showPositionButton;

	/**
	 * The panel on the top with the button to show/hide the location textfield,
	 * the combo buttons for the path and the textfield for the location.
	 */
	private JPanel topPanel;

	public GtkFileChooserUI(JFileChooser chooser) {
		super(chooser);
		this.chooser = chooser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		Log.debug("GtkFileChooserUI: Action: ", e.getActionCommand());
		if (APPROVE_SELECTION.equals(cmd)) {
			getFileChooser().setSelectedFile(fileBrowserPane.getSelectedFile());
			getFileChooser().setSelectedFiles(fileBrowserPane.getSelectedFiles());
			approveSelection();
		} else if (ACTION_SAVE.equals(cmd)) {
			approveButton.doClick();
		} else if (ACTION_ADD_BOOKMARK.equals(cmd)) {
			addToBookmarks();
		} else if (ACTION_SELECTED_BOOKMARK.equals(cmd)) {
			File location = new File(locationsPane.getCurrentPath().getLocation());
			fireChangeDirectoryEvent(location);
		} else if (LOCATION_POPUP.equals(cmd)) {
			if (getFileChooser().getDialogType() != SAVE_DIALOG) {
				showPositionButton.doClick();
			}
		} else if (UP_FOLDER.equals(cmd)) {
			pathBarButtons.upFolder();
		} else if (DOWN_FOLDER.equals(cmd)) {
			pathBarButtons.downFolder();
		} else if (HOME_FOLDER.equals(cmd)) {
			fireChangeDirectoryEvent(new File(System.getProperty("user.home")));
		} else if (DESKTOP_FOLDER.equals(cmd)) {
			fireChangeDirectoryEvent(FreeDesktopUtil.getWellKnownDirPath(WellKnownDir.DESKTOP));
		} else if (QUICK_BOOKMARK.equals(cmd)) {
			int id = e.getID();
			locationsPane.selectBookmark(id);
			File location = new File(locationsPane.getCurrentPath().getLocation());
			fireChangeDirectoryEvent(location);
		} else if (ACTION_CREATE_FOLDER.equals(cmd)) {
			fileBrowserPane.createFolder();
		}
	}

	@Override
	public String getApproveButtonText(JFileChooser fc) {
		String buttonText = fc.getApproveButtonText();
		if (buttonText != null) {
			return buttonText;
		} else if (fc.getDialogType() == JFileChooser.OPEN_DIALOG) {
			return openButtonText;
		} else if (fc.getDialogType() == JFileChooser.SAVE_DIALOG) {
			return saveButtonText;
		} else {
			return null;
		}
	}

	@Override
	public String getDirectoryName() {
		return getFileChooser().getCurrentDirectory().getName();
	}

	@Override
	public JFileChooser getFileChooser() {
		return chooser;
	}

	@Override
	public String getFileName() {
		if (getFileChooser().getDialogType() == SAVE_DIALOG) {
			File filename = saveDialogPanel.getFilename();
			return filename != null ? filename.getAbsolutePath() : null;
		}
		return fileNameTextField.getText();
	}

	/**
	 * Returns the maximum size of the <code>JFileChooser</code>.
	 * 
	 * @param c a <code>JFileChooser</code>
	 * @return a <code>Dimension</code> specifying the maximum width and height
	 *         of the file chooser
	 */
	@Override
	public Dimension getMaximumSize(JComponent c) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Returns the minimum size of the <code>JFileChooser</code>.
	 * 
	 * @param c a <code>JFileChooser</code>
	 * @return a <code>Dimension</code> specifying the minimum width and height
	 *         of the file chooser
	 */
	@Override
	public Dimension getMinimumSize(JComponent c) {
		return MIN_SIZE;
	}

	/**
	 * Returns the preferred size of the specified <code>JFileChooser</code>.
	 * The preferred size is at least as large, in both height and width, as the
	 * preferred size recommended by the file chooser's layout manager.
	 * 
	 * @param c a <code>JFileChooser</code>
	 * @return a <code>Dimension</code> specifying the preferred width and
	 *         height of the file chooser
	 */
	@Override
	public Dimension getPreferredSize(JComponent c) {
		Rectangle bound = GtkFileChooserSettings.get().getBound();
		if (bound != null && bound.width > 0 && bound.height > 0) {
			return new Dimension(bound.width, bound.height);
		}
		int prefWidth = PREF_SIZE.width;
		Dimension d = c.getLayout().preferredLayoutSize(c);
		if (d != null) {
			return new Dimension(d.width < prefWidth ? prefWidth : d.width,
					d.height < PREF_SIZE.height ? PREF_SIZE.height : d.height);
		} else {
			return new Dimension(prefWidth, PREF_SIZE.height);
		}
	}

	@Override
	public void installComponents(final JFileChooser fc) {
		fileBrowserPane = new FileBrowserPane(getFileChooser().getCurrentDirectory(), getFileChooser().getFileView());
		fc.addPropertyChangeListener(this);
		fileBrowserPane.addPropertyChangeListener(this);
		fileBrowserPane.addActionListener(this);
		// When PAGE_UP is pressed, go to the file browser table
		fileBrowserPane.table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "goToFileNameTextField");
		fileBrowserPane.table.getActionMap().put("goToFileNameTextField", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileNameTextField.requestFocus();
			}
		});
		// ********************************* //
		// **** Construct the top panel **** //
		// ********************************* //
		showPositionButton = new JToggleButton(GtkStockIcon.get("gtk-edit", Size.GTK_ICON_SIZE_BUTTON));
		showPositionButton.setSelected(GtkFileChooserSettings.get().getLocationMode() == Mode.FILENAME_ENTRY);
		showPositionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Mode mode = showPositionButton.isSelected() ? Mode.FILENAME_ENTRY : Mode.PATH_BAR;
				GtkFileChooserSettings.get().setLocationMode(mode);
			}
		});
		showPositionButton.setToolTipText(i18n("Type a file name"));
		// CurrentDir Combo Buttons
		pathBarButtons = new GtkPathBar(getFileChooser().getCurrentDirectory());
		pathBarButtons.addActionListener(pathBarActionListener);
		/**
		 * Pathbar
		 */
		JPanel pathbar = new JPanel();
		pathbar.setLayout(new BoxLayout(pathbar, BoxLayout.LINE_AXIS));
		pathbar.add(showPositionButton);
		pathbar.add(Box.createHorizontalStrut(UPPER_BUTTON_GAP));
		pathbar.add(pathBarButtons);
		// Create folder button
		createFolderButton = new JButton(i18n("Create Fo_lder"));
		createFolderButton.setVisible(false);
		createFolderButton.setMnemonic(getMnemonic("Create Fo_lder"));
		createFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ActionEvent evt = new ActionEvent(GtkFileChooserUI.this, ACTION_CREATE_FOLDER.hashCode(), ACTION_CREATE_FOLDER);
				GtkFileChooserUI.this.actionPerformed(evt);
			}
		});
		pathbar.add(createFolderButton);
		/**
		 * Filename textfield
		 */
		createFilenamePanel(fc);
		showPositionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JToggleButton btn = (JToggleButton) e.getSource();
				filenamePanel.setVisible(btn.isSelected());
			}
		});
		filenamePanel.setVisible(showPositionButton.isSelected());
		// First card put in the topPanel
		JPanel topPanelDefault = new JPanel(new BorderLayout());
		topPanelDefault.setLayout(new BoxLayout(topPanelDefault, BoxLayout.PAGE_AXIS));
		topPanelDefault.add(pathbar);
		topPanelDefault.add(filenamePanel);
		topPanel = new JPanel(new CardLayout());
		topPanel.add(topPanelDefault, TOP_PATHBAR_PANEL);
		// Add the top panel to the open dialog panel
		openDialogPanel.add(topPanel, BorderLayout.NORTH);
		/***********************************************
		 * Accessory Panel (for example image preview) *
		 ***********************************************/
		openDialogPanel.add(getAccessoryPanel(), BorderLayout.AFTER_LINE_ENDS);
		JComponent accessory = fc.getAccessory();
		if (accessory != null) {
			getAccessoryPanel().add(accessory);
		}
		/********************************
		 * Central Panel (File Browser) *
		 ********************************/
		addFileBrowserPane();
		if (fc.getControlButtonsAreShown()) {
			fc.add(getButtonPanel(), BorderLayout.PAGE_END);
		}
	}

	@Override
	public void installUI(JComponent c) {
		// Init FileView
		if (getFileChooser().getFileView() == null) {
			getFileChooser().setFileView(new GtkFileView());
		}
		openDialogPanel = new JPanel();
		openDialogPanel.setLayout(new BorderLayout(0, 11));
		// Add to the file chooser
		chooser.setLayout(new BorderLayout());
		chooser.setBorder(new EmptyBorder(12, 11, 11, 11));
		doDialogTypeChanged(chooser.getDialogType());
		chooser.setFileHidingEnabled(!GtkFileChooserSettings.get().getShowHidden());
		if (chooser.getCurrentDirectory() == null) {
			chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		}
		// Persist component bounds and sizes
		chooser.removeComponentListener(chooserComponentListener);
		chooser.addComponentListener(chooserComponentListener);
		// Add key binding
		installKeyBinding();
		// File watcher to "live" updated the file chooser when files are
		// changed..
		FileWatcher.theFileWatcher().register(chooser.getCurrentDirectory());
		// .. or new devices are mounted..
		FileWatcher.theFileWatcher().register(FILE_MEDIA);
		// .. or the bookmarks are updated.
		FileWatcher.theFileWatcher().register(FILE_GTK_BOOKMARK);
		FileWatcher.theFileWatcher().addFileListener(new FileListener() {
			@Override
			public void fileChanged(final FileEvent event) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						File file = event.getFile();
						if (FILE_MEDIA.equals(file) || FILE_GTK_BOOKMARK.equals(file)) {
							locationsPane.refreshLocations();
						} else {
							// the remaining case is the current directory
							if (fileBrowserPane != null)
								fileBrowserPane.refresh();
						}
					}
				});
			}
		});
		super.installUI(c);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		Object value = e.getNewValue();
		if (value != null) {
			// Prevent false property changes (issue 54)
			if (value.getClass().isArray() && ArrayUtil.areArrayEqual(value, e.getOldValue())) {
				return;
			} else if (value.equals(e.getOldValue())) {
				return;
			}
		}
		String property = e.getPropertyName();
		Object source = e.getSource();
		Log.debug("Property: ", property, " = ", value, " ; source :", source.getClass());
		if (DIRECTORY_CHANGED_PROPERTY.equals(property)) {
			doDirectoryChanged((File) e.getOldValue(), (File) value, source);
		} else if (SELECTED_FILE_CHANGED_PROPERTY.equals(property)) {
			doSelectedFileChanged((File) value);
		} else if (SELECTED_FILES_CHANGED_PROPERTY.equals(property)) {
			doSelectedFilesChanged((File[]) value);
		} else if (CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY.equals(property)) {
			doChoosableFileFilterChanged((FileFilter[]) value);
		} else if (CURRENT_PANEL_CHANGED.equals(property)) {
			doCurrentPanelChanged((Integer) value);
		} else if (FILE_FILTER_CHANGED_PROPERTY.equals(property)) {
			doFilterChanged((javax.swing.filechooser.FileFilter) value);
		} else if (FILE_SELECTION_MODE_CHANGED_PROPERTY.equals(property)) {
			doFileSelectionModeChanged((Integer) value);
		} else if (FILE_HIDING_CHANGED_PROPERTY.equals(property)) {
			doFileHidingChanged((Boolean) value);
		} else if (MULTI_SELECTION_ENABLED_CHANGED_PROPERTY.equals(property)) {
			doMultiSelectionEnabledChanged((Boolean) value);
		} else if (ACCESSORY_CHANGED_PROPERTY.equals(property)) {
			doAccessoryChanged(e);
		} else if (APPROVE_BUTTON_TEXT_CHANGED_PROPERTY.equals(property)) {
			doApproveButtonTextChanged(e);
		} else if (APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY.equals(property)) {
			doApproveButtonTextChanged(e);
		} else if (DIALOG_TYPE_CHANGED_PROPERTY.equals(property)) {
			doDialogTypeChanged((Integer) value);
		} else if ("JFileChooserDialogIsClosingProperty".equals(property)) {
			onClosing();
		} else if (CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY.equals(property)) {
			getButtonPanel().setVisible((Boolean) value);
		} else if (COMPONENT_ORIENTATION_PROPERTY.equals(property)) {
			doComponentOrientationChanged(e);
		} else if (ANCESTOR_PROPERTY.equals(property)) {
			doAncestorChanged(e);
		} else if (EXPANDED_STATUS_CHANGED.equals(property)) {
			boolean expanded = (Boolean) value;
			GtkFileChooserSettings.get().setExpandFolders(expanded);
			packSaveDialog(expanded);
		} else if (FILE_VIEW_CHANGED_PROPERTY.equals(property)) {
			dofileViewChanged((FileView) value);
		}
	}

	@Override
	public void rescanCurrentDirectory(JFileChooser fc) {
		fileBrowserPane.rescanCurrentDirectory();
	}

	@Override
	public void setDirectoryName(String dirname) {
		getFileChooser().setCurrentDirectory(new File(dirname));
	}

	@Override
	public void setFileName(String filename) {
		if (fileNameTextField != null) {
			fileNameTextField.setText(filename);
		}
	}

	@Override
	public void uninstallComponents(JFileChooser fc) {
		super.uninstallComponents(fc);
		addBookmarkButton = null;
		approveButton = null;
		buttonPanel = null;
		cancelButton = null;
		openDialogPanel = null;
		saveDialogPanel = null;
		cardPanel = null;
		showPositionButton = null;
		pathBarButtons = null;
		pathBarActionListener = null;
		createFolderButton = null;
		fileBrowserPane = null;
		fileNameLabelText = null;
		filenamePanel = null;
		fileNameTextField = null;
		filterComboBox = null;
		locationsPane = null;
		pathAutoCompletion = null;
		recentlyUsedPane = null;
		removeBookmarkButton = null;
		searchFilesPane = null;
		searchPanel = null;
		topPanel = null;
	}

	@Override
	public void uninstallUI(JComponent c) {
		uninstallListeners(chooser);
		uninstallComponents(chooser);
		uninstallDefaults(chooser);
		if (getAccessoryPanel() != null) {
			getAccessoryPanel().removeAll();
		}
		getFileChooser().removeAll();
	}

	public void valueChanged(ListSelectionEvent e) {
		JFileChooser fc = getFileChooser();
		File f = fc.getSelectedFile();
		if (!e.getValueIsAdjusting() && f != null && !getFileChooser().isTraversable(f)) {
			setFileName(fileNameString(f));
		}
	}

	@Override
	protected JButton getApproveButton(JFileChooser fc) {
		return approveButton;
	}

	/**
	 * The Button panel to cancel, open/save or custom names.
	 * 
	 * @return button panel
	 */
	protected JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			// Buttons
			buttonPanel.setLayout(new ButtonAreaLayout());
			cancelButton = new JButton(cancelButtonText);
			cancelButton.setToolTipText(cancelButtonToolTipText);
			cancelButton.addActionListener(getCancelSelectionAction());
			cancelButton.setMnemonic(getMnemonic("Stock label|_Cancel"));
			buttonPanel.add(cancelButton);
			approveButton = new JButton();
			approveButton.setAction(getOpenClickedAction());
			approveButton.setToolTipText(getApproveButtonToolTipText(getFileChooser()));
			buttonPanel.add(approveButton);
			// Since GTK 2.18.0 these buttons, as many others, have no icon
			// more.
			if (!(GtkVersion.check(2, 18, 0))) {
				if (getFileChooser().getDialogType() == JFileChooser.OPEN_DIALOG) {
					approveButton.setIcon(GtkStockIcon.get("gtk-open", Size.GTK_ICON_SIZE_BUTTON));
				} else {
					approveButton.setIcon(GtkStockIcon.get("gtk-save", Size.GTK_ICON_SIZE_BUTTON));
				}
				cancelButton.setIcon(GtkStockIcon.get("gtk-cancel", Size.GTK_ICON_SIZE_BUTTON));
			}
			// Adjust buttons width (on Ubuntu it was different)
			Dimension psize0 = approveButton.getPreferredSize();
			Dimension psize1 = cancelButton.getPreferredSize();
			int width = psize0.width > psize1.width ? psize0.width : psize1.width;
			width = width < 80 ? 80 : width;
			psize0.width = width;
			cancelButton.setPreferredSize(psize0);
			approveButton.setPreferredSize(psize0);
		}
		return buttonPanel;
	}

	@Override
	protected void installStrings(JFileChooser fc) {
		super.installStrings(fc);
		Locale l = fc.getLocale();
		fileNameLabelText = i18n("_Location:");
		fileNameLabelMnemonic = getMnemonic("_Location:");
		filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", l);
		// Use gnome l10n resources
		openButtonText = i18n("Stock label|_Open");
		saveButtonText = i18n("Stock label|_Save");
		cancelButtonText = i18n("Stock label|_Cancel");
	}

	@Override
	protected void uninstallListeners(JFileChooser fc) {
		fc.removePropertyChangeListener(this);
		fc.removeActionListener(this);
		SwingUtilities.replaceUIInputMap(fc, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
		SwingUtilities.replaceUIActionMap(fc, null);
	}

	FilesListPane getRecentlyUsedPane() {
		return recentlyUsedPane;
	}

	private void addFileBrowserPane() {
		// Left Panel (Bookmarks)
		addBookmarkButton = new JButton(i18n("_Add"));
		addBookmarkButton.setMnemonic(getMnemonic("_Add"));
		addBookmarkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addToBookmarks();
			}
		});
		addBookmarkButton.setEnabled(false);
		removeBookmarkButton = new JButton(i18n("_Remove"));
		removeBookmarkButton.setMnemonic(getMnemonic("_Remove"));
		// it will be enabled, when we select a bookmark.
		removeBookmarkButton.setEnabled(false);
		removeBookmarkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				locationsPane.removeSelectedBookmark();
			}
		});
		// Since GTK 2.18.0 these buttons, as many others, have no icon more.
		if (!(GtkVersion.check(2, 18, 0))) {
			addBookmarkButton.setIcon(GtkStockIcon.get("gtk-add", Size.GTK_ICON_SIZE_BUTTON));
			removeBookmarkButton.setIcon(GtkStockIcon.get("gtk-remove", Size.GTK_ICON_SIZE_BUTTON));
		}
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(Box.createVerticalStrut(BOTTOM_ROW_GAP));
		addBookmarkButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		removeBookmarkButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		JPanel innerPanel = new JPanel();
		innerPanel.add(addBookmarkButton);
		innerPanel.add(Box.createHorizontalStrut(ButtonAreaLayout.hGap));
		innerPanel.add(removeBookmarkButton);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		buttonPanel.add(innerPanel);
		locationsPane = new GtkLocationsPane();
		locationsPane.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path entry = ((GtkLocationsPane) e.getSource()).getCurrentPath();
				if (entry instanceof ActionPath) {
					ActionPath action = (ActionPath) entry;
					getFileChooser().firePropertyChange(CURRENT_PANEL_CHANGED, currentPanelId, action.getId());
					return;
				}
				getFileChooser().firePropertyChange(CURRENT_PANEL_CHANGED, currentPanelId, FILEBROWSER_PANEL_ID);
				if (entry != null && entry.getLocation() != null) {
					ActionEvent evt = new ActionEvent(locationsPane, -1, ACTION_SELECTED_BOOKMARK);
					// fire an action event on the current ActionListener
					// (GtkFileChooser)
					GtkFileChooserUI.this.actionPerformed(evt);
				}
			}
		});
		JPanel leftPane = createPanel(new PanelElement(locationsPane, BorderLayout.CENTER),
				new PanelElement(buttonPanel, BorderLayout.PAGE_END));
		installListenersForBookmarksButtons();
		// Right Panel (file browser)
		fileBrowserPane.setPreferredSize(LIST_PREF_SIZE);
		cardPanel.add(fileBrowserPane, FILEBROWSER_PANEL);
		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(cardPanel, BorderLayout.CENTER);
		if (filterComboBox == null) {
			createFilterComboBox();
		}
		rightPane.add(createPanelBoxLayout(BoxLayout.Y_AXIS, Box.createVerticalStrut(BOTTOM_ROW_GAP),
				createPanelBoxLayout(Box.createHorizontalGlue(), filterComboBox)), BorderLayout.PAGE_END);
		// add to the file chooser
		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		splitPanel.setContinuousLayout(true);
		openDialogPanel.add(splitPanel, BorderLayout.CENTER);
	}

	private void addToBookmarks() {
		File[] paths = fileBrowserPane.getSelectedFiles();
		if (paths == null) {
			locationsPane.addBookmark(fileBrowserPane.getSelectedFile());
		} else {
			for (File path : paths) {
				locationsPane.addBookmark(path);
			}
		}
	}

	private void approveSelection() {
		if (askOverride()) {
			getFileChooser().approveSelection();
		}
	}

	/**
	 * Ask if to override, when necessary.
	 * 
	 * @return {@code true} when we can save or override, else {@code false}.
	 */
	private boolean askOverride() {
		// Override behavior
		if (SAVE_DIALOG == getFileChooser().getDialogType()) {
			File selectedFile = getFileChooser().getSelectedFile();
			if (selectedFile == null) {
				return false;
			}
			if (selectedFile.exists()) {
				String head = i18n("A file named \"%s\" already exists.  Do you want to replace it?", selectedFile.getName());
				String foot = i18n("The file already exists in \"%s\".  Replacing it will overwrite its contents.",
						selectedFile.getParentFile().getName());
				String msg = "<html><p width='400px'>" + "<span style='font-weight: bold; font-size: 18pt;'>" + head
						+ "</span></p><br /><p>" + foot + "</p></html>";
				int n = JOptionPane.showConfirmDialog(getFileChooser(), msg, "", JOptionPane.OK_CANCEL_OPTION);
				return n == JOptionPane.OK_OPTION;
			}
		}
		return true;
	}

	private void createFilenamePanel(JFileChooser fc) {
		// FileName label and textfield
		filenamePanel = new JPanel();
		filenamePanel.setLayout(new BoxLayout(filenamePanel, BoxLayout.LINE_AXIS));
		JLabel fileNameLabel = new JLabel(fileNameLabelText);
		fileNameLabel.setDisplayedMnemonic(fileNameLabelMnemonic);
		filenamePanel.add(fileNameLabel);
		filenamePanel.add(Box.createRigidArea(new Dimension(15, 0)));
		fileNameTextField = new JTextField() {
			private static final long serialVersionUID = GtkFileChooserUI.serialVersionUID;

			@Override
			public Dimension getMaximumSize() {
				return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
			}
		};
		filenamePanel.add(fileNameTextField);
		fileNameLabel.setLabelFor(fileNameTextField);
		fileNameTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (!getFileChooser().isMultiSelectionEnabled()) {
					fileBrowserPane.clearSelection();
				}
			}
		});
		fileNameTextField.addActionListener(new SelectPathAction() {
			@Override
			protected File getSelectedPath() {
				String text = fileNameTextField.getText();
				File path = new File(text);
				if (!path.isAbsolute()) {
					path = new File(getFileChooser().getCurrentDirectory().getAbsolutePath() + File.separator + text);
				}
				return path;
			}
		});
		// When PAGE_DOWN is pressed, go to the file browser table
		fileNameTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "goToFileBrowser");
		fileNameTextField.getActionMap().put("goToFileBrowser", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileBrowserPane.table.requestFocus();
			}
		});
		// Add decorator for auto completion
		pathAutoCompletion = new PathAutoCompleter(fileNameTextField);
		pathAutoCompletion.setShowHidden(GtkFileChooserSettings.get().getShowHidden());
		pathAutoCompletion.setCurrentPath(fileBrowserPane.getCurrentDir().getAbsolutePath());
		if (fc.isMultiSelectionEnabled()) {
			setFileName(fileNameString(fc.getSelectedFiles()));
		} else {
			setFileName(fileNameString(fc.getSelectedFile()));
		}
	}

	/* The following methods are used by the PropertyChange Listener */
	private void createFilterComboBox() {
		filterComboBox = new JComboBox();
		filterComboBox.putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY, filesOfTypeLabelText);
		Dimension size = new Dimension(150, (int) removeBookmarkButton.getPreferredSize().getHeight());
		filterComboBox.setPreferredSize(size);
		filterComboBox.setMaximumSize(size);
		filterComboBox.setMinimumSize(size);
		filterComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileFilter filter = (FileFilter) filterComboBox.getSelectedItem();
				getFileChooser().setFileFilter(filter);
			}
		});
	}

	private void createRecentlyUsedPane() {
		/**
		 * Create an empty table
		 */
		recentlyUsedPane = new FilesListPane(getFileChooser().getFileView());
		int selectionMode = getFileChooser().isMultiSelectionEnabled() ? MULTIPLE_INTERVAL_SELECTION : SINGLE_SELECTION;
		recentlyUsedPane.setSelectionMode(selectionMode);
		recentlyUsedPane.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getFileChooser().isMultiSelectionEnabled()) {
					getFileChooser().setSelectedFiles(recentlyUsedPane.getSelectedFiles());
				} else {
					getFileChooser().setSelectedFile(recentlyUsedPane.getSelectedFile());
				}
				if (FilesListPane.DOUBLE_CLICK_ID == e.getID()) {
					// On double click on a recent file, close the file chooser.
					getFileChooser().approveSelection();
				}
			}
		});
		cardPanel.add(recentlyUsedPane, RECENTLY_USED_PANEL);
		// add listener on ENTER pressed for select/browse
		recentlyUsedPane.addActionListener(new SelectPathAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (FilesListPane.ENTER_PRESSED_ID == e.getID()) {
					super.actionPerformed(e);
				}
			}

			@Override
			protected File getSelectedPath() {
				return recentlyUsedPane.getSelectedFile();
			}
		});
		/**
		 * Add the content
		 */
		new RecentlyUsedFileWorker(this).execute();
	}

	private void createSaveDialogPanel() {
		saveDialogPanel = new SaveDialogPanel(openDialogPanel);
		saveDialogPanel.addPropertyChangeListener(this);
		saveDialogPanel.addActionListener(this);
		saveDialogPanel.setExternalPath(getFileChooser().getCurrentDirectory().getAbsolutePath());
		saveDialogPanel.setExpanded(GtkFileChooserSettings.get().getExpandFolders());
	}

	/**
	 * Create the view for the search, with a text field on the top and a file
	 * list table on the center-right.
	 */
	private void createSearchPane() {
		searchFilesPane = new FilesListPane(getFileChooser().getFileView());
		int selectionMode = getFileChooser().isMultiSelectionEnabled() ? MULTIPLE_INTERVAL_SELECTION : SINGLE_SELECTION;
		searchFilesPane.setSelectionMode(selectionMode);
		searchFilesPane.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getFileChooser().isMultiSelectionEnabled()) {
					getFileChooser().setSelectedFiles(searchFilesPane.getSelectedFiles());
				} else {
					getFileChooser().setSelectedFile(searchFilesPane.getSelectedFile());
				}
				if (FilesListPane.DOUBLE_CLICK_ID == e.getID()) {
					// On double click close the file chooser.
					getFileChooser().approveSelection();
				}
			}
		});
		// add listener on ENTER pressed for select/browse
		searchFilesPane.addActionListener(new SelectPathAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (FilesListPane.ENTER_PRESSED_ID == e.getID()) {
					super.actionPerformed(e);
				}
			}

			@Override
			protected File getSelectedPath() {
				return searchFilesPane.getSelectedFile();
			}
		});
		searchPanel = new SearchPanel(searchFilesPane);
		topPanel.add(searchPanel, TOP_SEARCH_PANEL);
		cardPanel.add(searchFilesPane, SEARCH_PANEL);
	}

	private void doAccessoryChanged(PropertyChangeEvent e) {
		if (getAccessoryPanel() != null) {
			if (e.getOldValue() != null) {
				getAccessoryPanel().remove((JComponent) e.getOldValue());
			}
			JComponent accessory = (JComponent) e.getNewValue();
			if (accessory != null) {
				getAccessoryPanel().add(accessory, BorderLayout.CENTER);
			}
		}
	}

	private void doAncestorChanged(PropertyChangeEvent e) {
		if (e.getOldValue() == null && e.getNewValue() != null && e.getSource() instanceof JFileChooser) {
			// Ancestor was added, the file chooser is visible.
			// Set the focus order (on TAB pressed) for the component
			FocusUtil.setFocusOrder(pathBarButtons, fileNameTextField, locationsPane.bookmarksTable, fileBrowserPane.table,
					filterComboBox, cancelButton, approveButton);
			// set initial focus
			fileNameTextField.selectAll();
			fileNameTextField.requestFocus();
			if (saveDialogPanel != null) {
				packSaveDialog(GtkFileChooserSettings.get().getExpandFolders());
			}
			FileWatcher.theFileWatcher().start();
		}
	}

	private void doApproveButtonTextChanged(PropertyChangeEvent e) {
		JFileChooser chooser = getFileChooser();
		approveButton.setText(getApproveButtonText(chooser));
		approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
	}

	private void doChoosableFileFilterChanged(FileFilter[] filters) {
		filterComboBox.removeAllItems();
		for (FileFilter filter : filters) {
			filterComboBox.addItem(wrapFileFilter(filter));
		}
		if (filterComboBox.getItemCount() == 0) {
			// Add Default AcceptAll file filter
			filterComboBox.addItem(wrapFileFilter(getFileChooser().getAcceptAllFileFilter()));
		}
	}

	private void doComponentOrientationChanged(PropertyChangeEvent e) {
		ComponentOrientation o = (ComponentOrientation) e.getNewValue();
		JFileChooser cc = (JFileChooser) e.getSource();
		if (o != (ComponentOrientation) e.getOldValue()) {
			cc.applyComponentOrientation(o);
		}
	}

	private void doCurrentPanelChanged(int id) {
		currentPanelId = id;
		// Top panel
		CardLayout top = (CardLayout) topPanel.getLayout();
		// Right panel
		CardLayout right = (CardLayout) cardPanel.getLayout();
		switch (id) {
		case FILEBROWSER_PANEL_ID:
			Log.debug("   >>> Panel: ", FILEBROWSER_PANEL);
			topPanel.setVisible(true);
			filenamePanel.setVisible(showPositionButton.isSelected());
			top.show(topPanel, TOP_PATHBAR_PANEL);
			right.show(cardPanel, FILEBROWSER_PANEL);
			break;
		case RECENTLY_USED_PANEL_ID:
			Log.debug("   >>> Panel: ", RECENTLY_USED_PANEL);
			// show recent used files panel
			if (recentlyUsedPane == null) {
				createRecentlyUsedPane();
			}
			topPanel.setVisible(false);
			right.show(cardPanel, RECENTLY_USED_PANEL);
			break;
		case SEARCH_PANEL_ID:
			Log.debug("   >>> Panel: ", SEARCH_PANEL);
			if (searchFilesPane == null) {
				createSearchPane();
			}
			filenamePanel.setVisible(false);
			topPanel.setVisible(true);
			top.show(topPanel, TOP_SEARCH_PANEL);
			right.show(cardPanel, SEARCH_PANEL);
			searchPanel.setFileFilter(new FileFilterWrapper(getFileChooser().getFileFilter()));
			searchPanel.requestFocusInWindow();
			break;
		}
	}

	private void doDialogTypeChanged(int dialogType) {
		JFileChooser chooser = getFileChooser();
		if (SAVE_DIALOG == chooser.getDialogType()) {
			// Remove the Open Dialog
			if (openDialogPanel != null) {
				chooser.remove(openDialogPanel);
			}
			if (saveDialogPanel == null) {
				createSaveDialogPanel();
			}
			chooser.add(saveDialogPanel, BorderLayout.CENTER);
			// Hide Location button and text field
			if (showPositionButton != null) {
				showPositionButton.setVisible(false);
				filenamePanel.setVisible(false);
			}
			// Show the "Create Folder" button
			if (createFolderButton != null) {
				createFolderButton.setVisible(true);
			}
			doMultiSelectionEnabledChanged(false);
		} else {
			// Remove the Save Dialog
			if (saveDialogPanel != null) {
				chooser.remove(saveDialogPanel);
			}
			// Add the Open dialog
			chooser.add(openDialogPanel, BorderLayout.CENTER);
			// Show Location button and text field
			if (showPositionButton != null) {
				showPositionButton.setVisible(true);
				filenamePanel.setVisible(true);
			}
			// Hide the "Create Folder" button
			if (createFolderButton != null) {
				createFolderButton.setVisible(false);
			}
			saveDialogPanel = null;
		}
		// Button to approve the selection (Open, Save or custom text)
		if (approveButton != null) {
			approveButton.setText(getApproveButtonText(chooser));
			approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
			// Set the corresponding action for the "Open" or "Save" button
			approveButton.setAction(SAVE_DIALOG == chooser.getDialogType() ? getSaveClickedAction() : getOpenClickedAction());
		}
	}

	private void doDirectoryChanged(File olddir, File newdir, Object source) {
		JFileChooser fc = getFileChooser();
		FileSystemView fsv = fc.getFileSystemView();
		if (newdir != null) {
			if (newdir.equals(fileBrowserPane.getCurrentDir())) {
				// to avoid repeated invocations on the same directory.
				return;
			}
			// remove and re-add the ActionListener to avoid to fire the same
			// event again.
			pathBarButtons.removeActionListener(pathBarActionListener);
			pathBarButtons.setCurrentDirectory(newdir);
			pathBarButtons.addActionListener(pathBarActionListener);
			updateFileNameField();
			// If the event was fired by the same FileBrowserPane, do not set
			// the dir again.
			if (!fileBrowserPane.equals(source)) {
				// Remove and re-add listeners to not fire the same event
				// repeatedly
				PropertyChangeListener[] listeners = fileBrowserPane.getPropertyChangeListeners();
				for (PropertyChangeListener listener : listeners) {
					fileBrowserPane.removePropertyChangeListener(listener);
				}
				fileBrowserPane.setCurrentDir(newdir);
				for (PropertyChangeListener listener : listeners) {
					fileBrowserPane.addPropertyChangeListener(listener);
				}
			}
			if (fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled()) {
				if (fsv.isFileSystem(newdir)) {
					setFileName(newdir.getPath());
				} else {
					setFileName(null);
				}
			}
			if (saveDialogPanel != null) {
				saveDialogPanel.setExternalPath(newdir.getAbsolutePath());
			}
			// If the event was fired by the same JFileChooser, do not set the
			// dir again.
			if (!fc.equals(source)) {
				// Remove and re-add listeners to not fire the same event
				// repeatedly
				PropertyChangeListener[] listeners = fc.getPropertyChangeListeners();
				for (PropertyChangeListener listener : listeners) {
					fc.removePropertyChangeListener(listener);
				}
				fc.setCurrentDirectory(newdir);
				for (PropertyChangeListener listener : listeners) {
					fc.addPropertyChangeListener(listener);
				}
			}
			// Filename text field with autocompletion
			pathAutoCompletion.setCurrentPath(newdir.getAbsolutePath());
			// Update FileWatcher
			FileWatcher.theFileWatcher().unregister(olddir);
			FileWatcher.theFileWatcher().register(newdir);
		}
	}

	private void doFileHidingChanged(Boolean hide) {
		Boolean showHidden = !hide;
		GtkFileChooserSettings.get().setShowHidden(showHidden);
		fileBrowserPane.setShowHidden(showHidden);
	}

	private void doFileSelectionModeChanged(Integer fileSelectionMode) {
		JFileChooser fc = getFileChooser();
		File currentDirectory = fc.getCurrentDirectory();
		if (currentDirectory != null && fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled()
				&& fc.getFileSystemView().isFileSystem(currentDirectory)) {
			setFileName(currentDirectory.getPath());
		} else {
			setFileName(null);
		}
		pathAutoCompletion.setFileSelectionMode(fileSelectionMode);
		fileBrowserPane.setFileSelectionMode(fileSelectionMode);
	}

	private void dofileViewChanged(FileView fileView) {
		fileBrowserPane.setFileView(fileView);
		recentlyUsedPane.setFileView(fileView);
		searchFilesPane.setFileView(fileView);
	}

	private void doFilterChanged(javax.swing.filechooser.FileFilter filter) {
		fileBrowserPane.setCurrentFilter(filter);
		pathAutoCompletion.setCurrentFilter(filter);
		if (filter != null && !filterExists(filter)) {
			getFileChooser().addChoosableFileFilter(filter);
		}
		selectFilterInCombo();
		// Update the recent used file panel
		if (recentlyUsedPane != null) {
			new RecentlyUsedFileWorker(this).execute();
		}
		// Set the new filter in the Search panel
		if (searchPanel != null) {
			searchPanel.setFileFilter(new FileFilterWrapper(filter));
		}
	}

	private void doMultiSelectionEnabledChanged(Boolean multiSelectionEnabled) {
		if (getFileChooser().getDialogType() == SAVE_DIALOG && multiSelectionEnabled) {
			// Multi selection is not allowed in the Save Modus.
			return;
		}
		int selectionMode = multiSelectionEnabled ? MULTIPLE_INTERVAL_SELECTION : SINGLE_SELECTION;
		if (getRecentlyUsedPane() != null) {
			getRecentlyUsedPane().setSelectionMode(selectionMode);
		}
		if (searchFilesPane != null) {
			searchFilesPane.setSelectionMode(selectionMode);
		}
		fileBrowserPane.setIsMultiSelectionEnabled(multiSelectionEnabled);
	}

	private void doSelectedFileChanged(File file) {
		JFileChooser fc = getFileChooser();
		if (file != null && ((fc.isFileSelectionEnabled() && !file.isDirectory())
				|| (file.isDirectory() && fc.isDirectorySelectionEnabled()))) {
			setFileName(fileNameString(file));
		}
		if (file != null && !file.equals(fc.getSelectedFile())) {
			fc.setSelectedFile(file);
		}
		// Enable/disable the "Add to Bookmark" button and update tooltip
		if (file != null && file.isDirectory()) {
			addBookmarkButton.setEnabled(true);
			addBookmarkButton.setToolTipText(i18n("Add the folder '%s' to the bookmarks", file.getName()));
		} else {
			addBookmarkButton.setEnabled(false);
			addBookmarkButton.setToolTipText(null);
		}
		if (saveDialogPanel != null && file != null && !file.isDirectory()) {
			saveDialogPanel.setFilenameText(file.getName());
		}
	}

	private void doSelectedFilesChanged(File[] files) {
		JFileChooser fc = getFileChooser();
		if (files != null) {
			List<File> fileList = new ArrayList<File>();
			for (File file : files) {
				// If directory, don't add to the selection when
				// isDirectorySelectionEnabled is false
				if (file.isDirectory()) {
					if (fc.isDirectorySelectionEnabled()) {
						fileList.add(file);
					}
				} else {
					fileList.add(file);
				}
			}
			setFileName(fileNameString(fileList.toArray(new File[fileList.size()])));
		}
		// Update the property in the JFileChooser if not yet happened
		if (files != null && !files.equals(fc.getSelectedFiles())) {
			fc.setSelectedFiles(files);
		}
		// Enable/disable the "Add to Bookamark" button
		if (files != null) {
			boolean enable = true;
			for (File file : files) {
				if (!file.isDirectory()) {
					enable = false;
					break;
				}
			}
			addBookmarkButton.setEnabled(enable);
			addBookmarkButton.setToolTipText(i18n("Add the selected folders to the bookmarks"));
		}
	}

	private String fileNameString(File file) {
		if (file == null) {
			return null;
		} else {
			JFileChooser fc = getFileChooser();
			if ((fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled()) || (fc.isDirectorySelectionEnabled()
					&& fc.isFileSelectionEnabled() && fc.getFileSystemView().isFileSystemRoot(file))) {
				return file.getPath();
			} else {
				return file.getName();
			}
		}
	}

	private String fileNameString(File[] files) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; files != null && i < files.length; i++) {
			if (i > 0) {
				buf.append(" ");
			}
			if (files.length > 1) {
				buf.append("\"");
			}
			buf.append(fileNameString(files[i]));
			if (files.length > 1) {
				buf.append("\"");
			}
		}
		return buf.toString();
	}

	// Fix Issue 61
	private boolean filterExists(FileFilter filter) {
		FileFilter[] filters = getFileChooser().getChoosableFileFilters();
		for (FileFilter f : filters) {
			if (f.getDescription().equals(filter.getDescription())) {
				return true;
			}
		}
		return false;
	}

	private void fireChangeDirectoryEvent(File newDirectory) {
		propertyChange(new PropertyChangeEvent(GtkFileChooserUI.this, DIRECTORY_CHANGED_PROPERTY,
				getFileChooser().getCurrentDirectory(), newDirectory));
	}

	/**
	 * Retrieve the ancestor dialog.
	 */
	private JDialog getAncestorDialog() {
		// Note: If done with reflection, it's 6 time slower.
		JFileChooser fc = getFileChooser();
		Container parent = fc.getParent();
		JDialog dialog = null;
		while (parent != null) {
			parent = parent.getParent();
			if (parent instanceof JDialog) {
				dialog = (JDialog) parent;
				break;
			}
		}
		return dialog;
	}

	/**
	 * Action when the button "Open" is pressed to approve the selection.
	 */
	private Action getOpenClickedAction() {
		Action action = getApproveSelectionAction();
		action.putValue(Action.NAME, getApproveButtonText(getFileChooser()));
		action.putValue(Action.MNEMONIC_KEY, getMnemonic("Stock label|_Open"));
		return action;
	}

	/**
	 * Action when the button "Save" is pressed to approve the selection. If the
	 * file to save already exists, it asks before override.
	 */
	private Action getSaveClickedAction() {
		// In the Save mode, we can use a SelectPathAction because only a single
		// file can be saved (multiselection disabled).
		Action action = new SelectPathAction() {
			@Override
			protected File getSelectedPath() {
				if (saveDialogPanel != null) {
					return saveDialogPanel.getFilename();
				}
				return null;
			}
		};
		action.putValue(Action.NAME, getApproveButtonText(getFileChooser()));
		action.putValue(Action.MNEMONIC_KEY, getMnemonic("Stock label|_Save"));
		return action;
	}

	private void installKeyBinding() {
		NavigationKeyBinding keyBinding = new NavigationKeyBinding(getFileChooser());
		keyBinding.addActionListener(this);
	}

	/**
	 * Listeners for enable/disable the buttons "Add" and "Remove" below the
	 * LocationsPane.
	 */
	private void installListenersForBookmarksButtons() {
		// Behavior for the "Remove" button
		locationsPane.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path path = locationsPane.getCurrentPath();
				// Enable only if a bookmark is selected.
				if (path instanceof GtkBookmark) {
					removeBookmarkButton.setEnabled(true);
					removeBookmarkButton.setToolTipText(i18n("Remove the bookmark '%s'", path.getName()));
				} else {
					removeBookmarkButton.setEnabled(false);
					removeBookmarkButton.setToolTipText(null);
				}
			}
		});
	}

	/**
	 * Method invoked when the FileChooser is closed.
	 */
	private void onClosing() {
		if (searchPanel != null) {
			searchPanel.stopSearch();
		}
		FileWatcher.theFileWatcher().stop();
	}

	private void packSaveDialog(boolean expand) {
		JDialog dialog = getAncestorDialog();
		if (dialog == null) {
			return;
		}
		Rectangle bound = GtkFileChooserSettings.get().getBound();
		Dimension size = dialog.getSize();
		if (size.width == 0 && size.height == 0) {
			// the size wasn't yet initialized, use the .ini file size
			if (bound != null) {
				size = new Dimension(bound.width, bound.height);
			} else {
				// ... or the the preferred size
				size = dialog.getPreferredSize();
			}
		}
		if (expand) {
			if (expandedHeight == -1) {
				expandedHeight = bound != null ? bound.height : MIN_EXPANDED_HEIGHT;
			}
			if (expandedHeight < MIN_EXPANDED_HEIGHT) {
				expandedHeight = MIN_EXPANDED_HEIGHT;
			}
			size.height = expandedHeight;
		} else {
			if (expandedHeight != -1) {
				expandedHeight = size.height;
			}
			// 200px is size when the folder view isn'expanded.
			size.height = MIN_HEIGHT;
		}
		if (dialog != null) {
			dialog.setPreferredSize(size);
			dialog.setSize(size);
		}
	}

	private void selectFilterInCombo() {
		FileFilter filterInChooser = getFileChooser().getFileFilter();
		FileFilter filterInCombo = (FileFilter) filterComboBox.getSelectedItem();
		if (filterInChooser == null || filterInCombo == null) {
			return;
		}
		if (!filterInCombo.getDescription().equals(filterInChooser.getDescription())) {
			// Select on the combo the just now changed
			// file-filter value if different.
			for (int i = 0; i < filterComboBox.getItemCount(); i++) {
				FileFilter item = (FileFilter) filterComboBox.getItemAt(i);
				if (item.getDescription().equals(filterInChooser.getDescription())) {
					filterComboBox.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	/**
	 * Update the decorator current path and empty the text field for the path.
	 */
	private void updateFileNameField() {
		pathAutoCompletion.setCurrentPath(getFileChooser().getCurrentDirectory().getAbsolutePath());
		if (chooser.getFileSelectionMode() != JFileChooser.FILES_ONLY)
			fileNameTextField.setText("");
	}

	/**
	 * Wrap a FileFiler setting toString() like getDescription()
	 */
	private FileFilter wrapFileFilter(final FileFilter filter) {
		return new FileFilter() {
			@Override
			public boolean accept(File f) {
				return filter.accept(f);
			}

			@Override
			public String getDescription() {
				return filter.getDescription();
			}

			@Override
			public String toString() {
				return getDescription();
			}
		};
	}
}
