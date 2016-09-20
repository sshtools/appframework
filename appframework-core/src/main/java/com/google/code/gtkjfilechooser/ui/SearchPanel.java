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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.code.gtkjfilechooser.FileSearch;
import com.google.code.gtkjfilechooser.GtkFileChooserSettings;
import com.google.code.gtkjfilechooser.GtkStockIcon;
import com.google.code.gtkjfilechooser.FileSearch.FileSearchHandler;
import com.google.code.gtkjfilechooser.GtkStockIcon.Size;


/**
 * Panel to enter the term for a search.
 * @author c.cerbo
 *
 */
public class SearchPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private FilesListPane filesPane;

	private FileSearch fileSearch;

	private JLabel searchLabel;

	private JTextField searchTextField;

	private JButton stopButton;

	private FileFilter fileFilter;

	public SearchPanel(FilesListPane pane) {		
		this.filesPane = pane;

		//		setLayout(new BorderLayout());
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		/**
		 * Search label
		 */
		searchLabel = new JLabel(_("_Search:"));
		add(searchLabel);
		add(Box.createRigidArea(new Dimension(10,0)));

		/**
		 * Search TextField
		 */
		searchTextField = new JTextField();
		int height = searchTextField.getPreferredSize().height;
		searchTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, height));
		searchTextField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				filesPane.getModel().clear();
				stopSearch();
				fileSearch = new FileSearch(System.getProperty("user.home"), searchTextField.getText(), new ThisFileSearchHandler());
				fileSearch.setSearchHidden(GtkFileChooserSettings.get().getShowHidden());
				fileSearch.setFileFilter(fileFilter);
				fileSearch.start();					
			}
		});
		add(searchTextField);

		/**
		 * Stop Button
		 */
		stopButton = new JButton(GtkStockIcon.get("gtk-stop", Size.GTK_ICON_SIZE_MENU));
		stopButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				stopSearch();
			}			
		});
		add(stopButton);
	}


	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	@Override
	public boolean requestFocusInWindow(){
		return searchTextField.requestFocusInWindow();
	}

	@Override
	public void setCursor(Cursor cursor) {
		super.setCursor(cursor);
		filesPane.setCursor(cursor);
	}

	public void stopSearch() {
		if (fileSearch != null) {
			fileSearch.stop();
			fileSearch = null;
		}
	}

	private class ThisFileSearchHandler implements FileSearchHandler {

		@Override
		public void found(File file) {
			filesPane.addFile(file);			
		}

		@Override
		public void finished(Status status) {
			setCursor(Cursor.getDefaultCursor());				
		}	

	}
}
