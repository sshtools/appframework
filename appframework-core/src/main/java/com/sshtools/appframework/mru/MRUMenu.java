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
package com.sshtools.appframework.mru;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.sshtools.ui.swing.EmptyIcon;

/**
 * An extension of a {@link JMenu} that is built using a model of Most Recently
 * Used items from a {@link MRUListModel}. When the model changes, the menu will
 * automatically update itself.
 */
public class MRUMenu extends JMenu implements ListDataListener, ActionListener {
	private MRUListModel model;
	private int max;

	public MRUMenu(Action action, MRUListModel model) {
		this(action, model, Integer.MAX_VALUE);
	}
	
	public MRUMenu(Action action, MRUListModel model, int max) {
		super(action);
		this.max = max;
		init(model);
	}

	public MRUMenu(String text, MRUListModel model) {
		this(text, model, Integer.MAX_VALUE);
	}

	public MRUMenu(String text, MRUListModel model, int max) {
		super(text);
		this.max = max;
		init(model);
		
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		evt = new ActionEvent(this, evt.getID(), evt.getActionCommand());
		ActionListener[] listeners = getActionListeners();
		// Don't use fireActionPerformed. GCJ's first changes the action command
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].actionPerformed(evt);
		}
	}

	public void cleanUp() {
		removeNotify();
		model.removeListDataListener(this);
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		rebuildMenu();
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		rebuildMenu();
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		rebuildMenu();
	}

	protected Icon getIconForFavourite(File favourite) {
		return null;
	}

	protected String getNameForFavourite(File favourite) {
		String name = favourite.getName();
		if (name.endsWith(".xml")) {
			name = name.substring(0, name.length() - 4);
		}
		return name;
	}

	protected String getToolTipForFavourite(File f) {
		return f.getAbsolutePath();
	}

	protected boolean include(File f) {
		return true;
	}

	private void init(MRUListModel model) {
		this.model = model;
		rebuildMenu();
		model.addListDataListener(this);
	}

	private void rebuildMenu() {
		Component[] c = getMenuComponents();
		for (int i = 0; (c != null) && (i < c.length); i++) {
			((JMenuItem) c[i]).removeActionListener(this);
			remove(c[i]);
		}
		for (int i = 0; i < model.getSize(); i++) {
			File f = (File) model.getElementAt(i);
			if (include(f)) {
				JMenuItem m = new JMenuItem(getNameForFavourite(f));
				Icon icon = getIconForFavourite(f);
				m.setIcon(icon == null ? new EmptyIcon(16, 16) : icon);
				m.setActionCommand(f.getAbsolutePath());
				m.setToolTipText(getToolTipForFavourite(f));
				m.addActionListener(this);
				add(m);
				if(getMenuComponentCount() >= max)
					break;
			}
		}
		setEnabled(model.getSize() > 0);
		validate();
	}
}