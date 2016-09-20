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
import static com.google.code.gtkjfilechooser.I18N.getMnemonic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.google.code.gtkjfilechooser.ActionDispatcher;
import com.google.code.gtkjfilechooser.BasicActionDispatcher;
import com.google.code.gtkjfilechooser.GtkFileChooserSettings;
import com.google.code.gtkjfilechooser.GtkStockIcon;
import com.google.code.gtkjfilechooser.Log;
import com.google.code.gtkjfilechooser.GtkStockIcon.Size;


public class ContextMenu extends JPopupMenu implements PropertyChangeListener, ActionDispatcher {
	static final public String SHOW_SIZE_COLUMN_CHANGED_PROPERTY = "ShowSizeColumnChanged";

	static final public String ACTION_ADD_BOOKMARK = "AddBookmark";

	static final public String ACTION_REFRESH = "refresh";

	static final public String ACTION_NEW_FOLDER = "New Folder";

	private ActionDispatcher actionDispatcher = new BasicActionDispatcher();

	private JMenuItem addToBookmarkMenuItem;

	public ContextMenu() {
		addPropertyChangeListener(this);

		addToBookmarkMenuItem = new JMenuItem();
		addToBookmarkMenuItem.setText(_("_Add to Bookmarks"));
		addToBookmarkMenuItem.setMnemonic(getMnemonic("_Add to Bookmarks"));
		addToBookmarkMenuItem.setIcon(GtkStockIcon.get("gtk-add", Size.GTK_ICON_SIZE_MENU));
		addToBookmarkMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fireActionEvent(new ActionEvent(ContextMenu.this, ACTION_ADD_BOOKMARK.hashCode(), ACTION_ADD_BOOKMARK));				
			}			
		});

		add(addToBookmarkMenuItem);

		addSeparator();

		// Add "show hidden files" CheckBoxMenuItem
		JCheckBoxMenuItem showHiddenCheckBoxItem = new JCheckBoxMenuItem();
		showHiddenCheckBoxItem.setText(_("Show _Hidden Files"));
		showHiddenCheckBoxItem.setMnemonic(getMnemonic("Show _Hidden Files"));
		showHiddenCheckBoxItem.setSelected(GtkFileChooserSettings.get().getShowHidden());
		showHiddenCheckBoxItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
				boolean showHidden = source.isSelected();
				firePropertyChange(JFileChooser.FILE_HIDING_CHANGED_PROPERTY, showHidden, !showHidden);
				//getFileChooser().setFileHidingEnabled(!showHidden);
				// property 'showHidden' persisten in
				// GtkFileChooserUI#listenToFileChooserPropertyChanges

				// Update also the decorator for the filenameTextField
				//getFileChooserUIAccessor().showHiddenAutocompletion(showHidden);
			}
		});
		add(showHiddenCheckBoxItem);

		// Add "show file size column" CheckBoxMenuItem
		JCheckBoxMenuItem showFileSizeCheckBoxItem = new JCheckBoxMenuItem();
		showFileSizeCheckBoxItem.setText(_("Show _Size Column"));
		showFileSizeCheckBoxItem.setMnemonic(getMnemonic("Show _Size Column"));
		showFileSizeCheckBoxItem.setSelected(GtkFileChooserSettings.get()
				.getShowSizeColumn());
		showFileSizeCheckBoxItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
				boolean showSizeColumn = source.isSelected();
				firePropertyChange(SHOW_SIZE_COLUMN_CHANGED_PROPERTY, !showSizeColumn, showSizeColumn);
			}
		});
		add(showFileSizeCheckBoxItem);
	}


	public void setAddToBookmarkMenuItemEnabled(boolean enabled) {
		// enable if path != null && path.isDirectory()
		addToBookmarkMenuItem.setEnabled(enabled);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		Object value = evt.getNewValue();

		Log.debug(property, " = ", value);		
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

}
