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

/**
 * An extension of a {@link JMenu} that is built using a model of Most Recently
 * Used items from a {@link MRUModel}. When the model changes, the menu will
 * automatically update itself.
 */

public class MRUMenu extends JMenu implements ListDataListener, ActionListener {

	private MRUListModel model;

	protected MRUMenu(Action action, MRUListModel model) {
		super(action);
		init(model);

	}

	protected MRUMenu(String text, MRUListModel model) {
		super(text);
		init(model);
	}

	public void cleanUp() {
		removeNotify();
		model.removeListDataListener(this);
	}

	private void init(MRUListModel model) {
		this.model = model;
		rebuildMenu();
		model.addListDataListener(this);

	}

	public void intervalAdded(ListDataEvent e) {
		rebuildMenu();

	}

	public void intervalRemoved(ListDataEvent e) {
		rebuildMenu();
	}

	public void contentsChanged(ListDataEvent e) {
		rebuildMenu();
	}

	public void actionPerformed(ActionEvent evt) {
		evt = new ActionEvent(this, evt.getID(), evt.getActionCommand());
		ActionListener[] listeners = getActionListeners();

		// Don't use fireActionPerformed. GCJ's first changes the action command
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].actionPerformed(evt);
		}
	}

	protected String getNameForFavourite(File favourite) {
		String name = favourite.getName();
		if (name.endsWith(".xml")) {
			name = name.substring(0, name.length() - 4);
		}
		return name;
	}

	protected Icon getIconForFavourite(File favourite) {
		return null;
	}

	protected String getToolTipForFavourite(File f) {
		return f.getAbsolutePath();
	}

	protected boolean include(File f) {
		return true;
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
				m.setIcon(getIconForFavourite(f));
				m.setActionCommand(f.getAbsolutePath());
				m.setToolTipText(getToolTipForFavourite(f));
				m.addActionListener(this);
				add(m);
			}
		}
		setEnabled(model.getSize() > 0);
		validate();
	}

}