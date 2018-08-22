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
package com.sshtools.appframework.ui;

import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.sshtools.ui.swing.FontUtil;

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