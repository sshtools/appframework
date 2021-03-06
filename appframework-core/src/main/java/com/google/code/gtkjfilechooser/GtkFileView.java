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
