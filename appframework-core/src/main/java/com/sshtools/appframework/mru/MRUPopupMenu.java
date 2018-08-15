package com.sshtools.appframework.mru;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * An extension of a {@link JPopupMenu} that is built using a model of Most
 * Recently Used items from a {@link MRUListModel}. When the model changes, the menu
 * will automatically update itself.
 */
@SuppressWarnings("serial")
public class MRUPopupMenu extends JPopupMenu implements ListDataListener,
		ActionListener {

	private ActionListener actionListener;
	private MRUListModel model;

	public MRUPopupMenu(MRUListModel model, ActionListener actionListener) {
		super();
		init(model);
		this.actionListener = actionListener;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		actionListener.actionPerformed(evt);
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

	protected Icon getIconForFavourite(File f) {
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
		Component[] c = getComponents();
		for (int i = 0; (c != null) && (i < c.length); i++) {
			((JMenuItem) c[i]).removeActionListener(this);
			remove(c[i]);
		}
		for (int i = 0; i < model.getSize(); i++) {
			File f = (File) model.getElementAt(i);
			if (include(f)) {
				String name = getNameForFavourite(f);
				JMenuItem m = new JMenuItem(name);
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