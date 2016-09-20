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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.google.code.gtkjfilechooser.ActionDispatcher;
import com.google.code.gtkjfilechooser.BasicActionDispatcher;
import com.google.code.gtkjfilechooser.BookmarkManager;
import com.google.code.gtkjfilechooser.FreeDesktopUtil;
import com.google.code.gtkjfilechooser.Path;
import com.google.code.gtkjfilechooser.SpringLayoutUtil;


public class SaveDialogPanel extends JPanel implements PropertyChangeListener, ActionDispatcher {

	static public final String ACTION_SAVE = "Action Save";

	private JTextField nameTextField;
	private JLabel saveFolderLabel;
	private JComboBox foldersComboBox;
	private Expander expander;
	private String externalPath;

	private ActionDispatcher actionDispatcher = new BasicActionDispatcher();

	public SaveDialogPanel(JComponent fileExplorerPanel) {
		super(new BorderLayout());

		JPanel saveTopPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		saveTopPanel.setLayout(layout);

		JLabel nameLabel = new JLabel(_("_Name:"));
		nameTextField = new JTextField();
		nameTextField.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				ActionEvent evt = new ActionEvent(SaveDialogPanel.this, -1, ACTION_SAVE);
				fireActionEvent(evt);
			}
		});
		saveFolderLabel = new JLabel(_("Save in _folder:"));
		initFoldersComboBox();

		saveTopPanel.add(nameLabel);
		saveTopPanel.add(nameTextField);

		saveTopPanel.add(saveFolderLabel);
		saveTopPanel.add(foldersComboBox);

		// Lay out the panel.
		SpringLayoutUtil.makeCompactGrid(saveTopPanel, 2, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		Dimension size = saveTopPanel.getPreferredSize();
		size.width = 600;
		saveTopPanel.setPreferredSize(size);
		add(saveTopPanel, BorderLayout.PAGE_START);

		expander = new Expander(_("_Browse for other folders"), fileExplorerPanel);
		expander.addPropertyChangeListener(this);
		add(expander, BorderLayout.CENTER);
	}

	private void initFoldersComboBox() {
		foldersComboBox = new JComboBox();
		foldersComboBox.setMaximumRowCount(30);
		foldersComboBox.setRenderer(new FileComboBoxRenderer(foldersComboBox));

		List<Path> locations = getLocations();

		foldersComboBox.setModel(new DefaultComboBoxModel(locations.toArray()));
	}

	/**
	 * The default locations: Home, Desktop, File System and all the removable
	 * devices.
	 * 
	 * @return
	 */
	private List<Path> getLocations() {
		List<Path> locations = new ArrayList<Path>();
		locations.addAll(FreeDesktopUtil.getBasicLocations());
		locations.addAll(FreeDesktopUtil.getRemovableDevices());
		locations.addAll(new BookmarkManager().getAll());
		return locations;
	}

	public boolean isExpanded() {
		return expander.isExpanded();
	}

	public void setExpanded(boolean expanded) {
		expander.setExpanded(expanded);
	}

	/**
	 * External path typicall set in the file browser panel.
	 * 
	 * @param externalPath
	 */
	public void setExternalPath(String externalPath) {
		this.externalPath = externalPath;
	}

	public File getFilename() {
		String name = nameTextField.getText();
		if (name == null || name.isEmpty()) {
			return null;
		}

		String path = isExpanded() ? externalPath : ((Path) foldersComboBox
				.getSelectedItem()).getLocation();

		return new File(path + File.separator + name);
	}

	/**
	 * Set the content of the text field. This setter does't influence the
	 * method {@link #getFilename()}.
	 * 
	 * @param simplyname
	 */
	public void setFilenameText(String simplyname) {
		nameTextField.setText(simplyname);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		if (Expander.EXPANDED_STATUS_CHANGED.equals(property)) {
			saveFolderLabel.setEnabled(!expander.isExpanded());
			foldersComboBox.setEnabled(!expander.isExpanded());
		}
		firePropertyChange(property, evt.getOldValue(), evt.getNewValue());
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
