/* HEADER */

package com.sshtools.appframework.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.sshtools.appframework.api.ui.SshToolsApplicationPanel;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.EmptyIcon;

/**
     * Concrete implementation of an {@link AppAction} that will toggles whether the
 * menubar is visible or not.
 *
 * @author $Author: brett $
 */

public class ToggleMenuBarAction
    extends AppAction {

  // Private instance variables

  private SshToolsApplicationPanel panel;

  /**
   * Creates a new ToggleToolBarAction.
   *
   * @param application
   *            application
   */

  public ToggleMenuBarAction(SshToolsApplicationPanel panel) {
    this.panel = panel;
    putValue(NAME, Messages.getString("ToggleMenuBarAction.Name"));
    putValue(SHORT_DESCRIPTION, Messages.getString("ToggleMenuBarAction.ShortDesc"));
    putValue(SMALL_ICON, new EmptyIcon(16, 16));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M,
        KeyEvent.ALT_MASK | KeyEvent.CTRL_MASK));
    putValue(LONG_DESCRIPTION, Messages.getString("ToggleMenuBarAction.LongDesc"));
    putValue(MNEMONIC_KEY, new Integer('m'));
    putValue(ON_MENUBAR, new Boolean(true));
    putValue(MENU_NAME, "View");
    putValue(MENU_ITEM_GROUP, new Integer(90));
    putValue(MENU_ITEM_WEIGHT, new Integer(5));
    putValue(IS_TOGGLE_BUTTON, Boolean.TRUE);
    putValue(IS_SELECTED, Boolean.valueOf(panel.isMenuBarVisible()));
    putValue(AppAction.ON_CONTEXT_MENU, Boolean.TRUE);
    putValue(AppAction.CONTEXT_MENU_GROUP, new Integer(95));
    putValue(AppAction.CONTEXT_MENU_WEIGHT, new Integer(0));

  }

  /*
   * (non-Javadoc)
   *
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */

  public void actionPerformed(ActionEvent evt) {
    boolean sel = ! (Boolean.TRUE.equals(getValue(AppAction.IS_SELECTED)));
    panel.setMenuBarVisible(sel);
    putValue(AppAction.IS_SELECTED, Boolean.valueOf(sel));

  }

}