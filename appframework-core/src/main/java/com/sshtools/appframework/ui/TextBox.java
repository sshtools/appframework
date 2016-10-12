/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.ui;

import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.sshtools.ui.swing.FontUtil;

/**
 * Description of the Class
 * 
 * @author magicthize
 * @created 26 May 2002
 */
public class TextBox extends JTextArea {
	private String text;

	public TextBox() {
		this("");
	}

	public TextBox(String text) {
		this(text, 0, 0);
	}

	public TextBox(String text, int rows, int columns) {
		super(rows, columns);
		setBackground(UIManager.getColor("Label.background"));
		setForeground(UIManager.getColor("Label.foreground"));
		setBorder(UIManager.getBorder("Label.border"));
		setFont(FontUtil.getUIManagerTextFieldFontOrDefault("TextField.font"));
		setOpaque(false);
		setWrapStyleWord(true);
		setLineWrap(true);
		setEditable(false);
		setText(text);
	}
}