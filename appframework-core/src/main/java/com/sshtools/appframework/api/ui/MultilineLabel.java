/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.api.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sshtools.ui.swing.UIUtil;

/**
 *
 *
 * @author $author$
 */

public class MultilineLabel

    extends JPanel {

  //  Private instance variables

  private GridBagConstraints constraints;

  private String text;

  /**
   * Creates a new MultilineLabel object.
   */

  public MultilineLabel() {
    this("");

  }

  /**
   * Creates a new MultilineLabel object.
   *
   * @param text
   */

  public MultilineLabel(String text) {
    super(new GridBagLayout());
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.fill = GridBagConstraints.NONE;
    setText(text);

  }

  /**
   *
   *
   * @param f
   */

  public void setFont(Font f) {
    super.setFont(f);
    for (int i = 0; i < getComponentCount(); i++) {
      getComponent(i).setFont(f);
    }

  }

  /**
   *
   *
   * @param text
   */

  public void setText(String text) {
    this.text = text;
    removeAll();
    StringTokenizer tok = new StringTokenizer(text, "\n");
    constraints.weighty = 0.0;
    constraints.weightx = 1.0;
    while (tok.hasMoreTokens()) {
      String t = tok.nextToken();
      if (!tok.hasMoreTokens()) {
        constraints.weighty = 1.0;
      }
      UIUtil.jGridBagAdd(this, new JLabel(t), constraints,
                         GridBagConstraints.REMAINDER);
    }
    revalidate();
    repaint();

  }

  /**
   *
   *
   * @return
   */

  public String getText() {
    return text;

  }

}