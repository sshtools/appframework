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

	private ActionDispatcher actionDispatcher = new BasicActionDispatcher();
	private Expander expander;
	private String externalPath;
	private JComboBox foldersComboBox;
	private JTextField nameTextField;

	private JLabel saveFolderLabel;

	public SaveDialogPanel(JComponent fileExplorerPanel) {
		super(new BorderLayout());

		JPanel saveTopPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		saveTopPanel.setLayout(layout);

		JLabel nameLabel = new JLabel(i18n("_Name:"));
		nameTextField = new JTextField();
		nameTextField.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				ActionEvent evt = new ActionEvent(SaveDialogPanel.this, -1, ACTION_SAVE);
				fireActionEvent(evt);
			}
		});
		saveFolderLabel = new JLabel(i18n("Save in _folder:"));
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

		expander = new Expander(i18n("_Browse for other folders"), fileExplorerPanel);
		expander.addPropertyChangeListener(this);
		add(expander, BorderLayout.CENTER);
	}

	@Override
	public void addActionListener(ActionListener l) {
		actionDispatcher.addActionListener(l);

	}

	@Override
	public void fireActionEvent(ActionEvent e) {
		actionDispatcher.fireActionEvent(e);

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

	public boolean isExpanded() {
		return expander.isExpanded();
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
	public void removeActionListener(ActionListener l) {
		actionDispatcher.removeActionListener(l);

	}

	@Override
	public void removeAllActionListeners() {
		actionDispatcher.removeAllActionListeners();		
	}

	public void setExpanded(boolean expanded) {
		expander.setExpanded(expanded);
	}

	/**
	 * External path typicall set in the file browser panel.
	 * 
	 * @param externalPath external path
	 */
	public void setExternalPath(String externalPath) {
		this.externalPath = externalPath;
	}

	/**
	 * Set the content of the text field. This setter does't influence the
	 * method {@link #getFilename()}.
	 * 
	 * @param simplyname name
	 */
	public void setFilenameText(String simplyname) {
		nameTextField.setText(simplyname);
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

	private void initFoldersComboBox() {
		foldersComboBox = new JComboBox();
		foldersComboBox.setMaximumRowCount(30);
		foldersComboBox.setRenderer(new FileComboBoxRenderer(foldersComboBox));

		List<Path> locations = getLocations();

		foldersComboBox.setModel(new DefaultComboBoxModel(locations.toArray()));
	}
}
