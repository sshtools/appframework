package com.sshtools.appframework.mru;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;

import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.EmptyIcon;
import com.sshtools.ui.swing.MenuAction;

public abstract class MRUAction extends AppAction implements MenuAction {

	public MRUAction(MRUListModel model) {
		putValue(Action.NAME, Messages.getString("MRUAction.Name"));
		putValue(Action.SMALL_ICON, new EmptyIcon(16, 16));
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("MRUAction.ShortDesc"));
		putValue(Action.LONG_DESCRIPTION, Messages.getString("MRUAction.LongDesc"));
		putValue(Action.MNEMONIC_KEY, new Integer('r'));
//		putValue(Action.ACTION_COMMAND_KEY, "recent");
		putValue(MENU_NAME, "File");
		putValue(MENU_ITEM_GROUP, new Integer(5));
		putValue(ON_MENUBAR, new Boolean(true));
		putValue(MENU_ITEM_WEIGHT, new Integer(10));
		MRUMenu menu = createMenu(model);
//		menu.addActionListener(this);
		putValue(MenuAction.MENU, menu);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (listeners != null) {
			Object[] listenerList = listeners.getListenerList();
			ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(), evt.getActionCommand());
			for (int i = 0; i <= (listenerList.length - 2); i += 2) {
				((ActionListener) listenerList[i + 1]).actionPerformed(e);
			}
		}
	}

	protected MRUMenu createMenu(MRUListModel model) {
		return new MRUMenu(this, model);
	}
}