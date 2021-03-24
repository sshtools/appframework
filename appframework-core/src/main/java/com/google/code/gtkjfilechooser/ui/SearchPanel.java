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
import com.google.code.gtkjfilechooser.FileSearch.FileSearchHandler;
import com.google.code.gtkjfilechooser.GtkFileChooserSettings;
import com.google.code.gtkjfilechooser.GtkStockIcon;
import com.google.code.gtkjfilechooser.GtkStockIcon.Size;


/**
 * Panel to enter the term for a search.
 * @author c.cerbo
 *
 */
public class SearchPanel extends JPanel {

	private class ThisFileSearchHandler implements FileSearchHandler {

		@Override
		public void finished(Status status) {
			setCursor(Cursor.getDefaultCursor());				
		}

		@Override
		public void found(File file) {
			filesPane.addFile(file);			
		}	

	}

	private static final long serialVersionUID = 1L;

	private FileFilter fileFilter;

	private FileSearch fileSearch;

	private FilesListPane filesPane;

	private JLabel searchLabel;

	private JTextField searchTextField;

	private JButton stopButton;


	public SearchPanel(FilesListPane pane) {		
		this.filesPane = pane;

		//		setLayout(new BorderLayout());
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		/**
		 * Search label
		 */
		searchLabel = new JLabel(i18n("_Search:"));
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

	@Override
	public boolean requestFocusInWindow(){
		return searchTextField.requestFocusInWindow();
	}

	@Override
	public void setCursor(Cursor cursor) {
		super.setCursor(cursor);
		filesPane.setCursor(cursor);
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public void stopSearch() {
		if (fileSearch != null) {
			fileSearch.stop();
			fileSearch = null;
		}
	}
}
