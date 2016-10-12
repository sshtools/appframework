/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
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
 * toolbar is visible or not.
 *
 * @author $Author: brett $
 */

public class ToggleToolBarAction
    extends AppAction {

  // Private instance variables

  private SshToolsApplicationPanel panel;

  /**
   * Creates a new ToggleToolBarAction.
   *
   * @param application
   *            application
   */

  public ToggleToolBarAction(SshToolsApplicationPanel panel) {
    this.panel = panel;
    putValue(NAME, Messages.getString("ToggleToolBarAction.Name"));
    putValue(SHORT_DESCRIPTION, Messages.getString("ToggleToolBarAction.ShortDesc"));
    putValue(SMALL_ICON, new EmptyIcon(16, 16));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B,
        KeyEvent.ALT_MASK | KeyEvent.CTRL_MASK));
    putValue(LONG_DESCRIPTION, Messages.getString("ToggleToolBarAction.LongDesc"));
    putValue(MNEMONIC_KEY, new Integer('t'));
    putValue(ON_MENUBAR, new Boolean(true));
    putValue(MENU_NAME, "View");
    putValue(MENU_ITEM_GROUP, new Integer(90));
    putValue(MENU_ITEM_WEIGHT, new Integer(0));
    putValue(IS_TOGGLE_BUTTON, Boolean.TRUE);
    putValue(IS_SELECTED, Boolean.valueOf(panel.isToolBarVisible()));
    putValue(AppAction.ON_CONTEXT_MENU, Boolean.TRUE);
    putValue(AppAction.CONTEXT_MENU_GROUP, new Integer(95));
    putValue(AppAction.CONTEXT_MENU_WEIGHT, new Integer(10));

  }

  /*
   * (non-Javadoc)
   *
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */

  public void actionPerformed(ActionEvent evt) {
    boolean sel = ! (Boolean.TRUE.equals(getValue(AppAction.IS_SELECTED)));
    panel.setToolBarVisible(sel);
    putValue(AppAction.IS_SELECTED, Boolean.valueOf(sel));

  }

}