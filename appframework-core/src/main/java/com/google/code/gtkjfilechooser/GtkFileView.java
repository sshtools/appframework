/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 ******************************************************************************/
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
