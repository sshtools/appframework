/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.google.code.gtkjfilechooser;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import com.google.code.gtkjfilechooser.GtkStockIcon.Size;


public class GtkFileView extends FileView {

	@Override
	public String getDescription(File f) {
		return f.getAbsolutePath();
	}

	@Override
	public Icon getIcon(File f) {
		return GtkStockIcon.get(f, Size.GTK_ICON_SIZE_MENU);
	}

	@Override
	public String getName(File f) {
		return f.getName();
	}

	@Override
	public String getTypeDescription(File f) {
		return null;
	}

	@Override
	public Boolean isTraversable(File f) {
		return f.isDirectory();
	}

}
