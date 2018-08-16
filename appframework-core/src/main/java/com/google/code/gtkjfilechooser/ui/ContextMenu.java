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

import static com.google.code.gtkjfilechooser.I18N.i18n;
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
	static final public String ACTION_ADD_BOOKMARK = "AddBookmark";

	static final public String ACTION_NEW_FOLDER = "New Folder";

	static final public String ACTION_REFRESH = "refresh";

	static final public String SHOW_SIZE_COLUMN_CHANGED_PROPERTY = "ShowSizeColumnChanged";

	private ActionDispatcher actionDispatcher = new BasicActionDispatcher();

	private JMenuItem addToBookmarkMenuItem;

	public ContextMenu() {
		addPropertyChangeListener(this);

		addToBookmarkMenuItem = new JMenuItem();
		addToBookmarkMenuItem.setText(i18n("_Add to Bookmarks"));
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
		showHiddenCheckBoxItem.setText(i18n("Show _Hidden Files"));
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
		showFileSizeCheckBoxItem.setText(i18n("Show _Size Column"));
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


	@Override
	public void addActionListener(ActionListener l) {
		actionDispatcher.addActionListener(l);

	}


	@Override
	public void fireActionEvent(ActionEvent e) {
		actionDispatcher.fireActionEvent(e);

	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		Object value = evt.getNewValue();

		Log.debug(property, " = ", value);		
	}

	@Override
	public void removeActionListener(ActionListener l) {
		actionDispatcher.removeActionListener(l);

	}

	@Override
	public void removeAllActionListeners() {
		actionDispatcher.removeAllActionListeners();		
	}

	public void setAddToBookmarkMenuItemEnabled(boolean enabled) {
		// enable if path != null && path.isDirectory()
		addToBookmarkMenuItem.setEnabled(enabled);
	}

}
