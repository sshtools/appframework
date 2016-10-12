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

import javax.swing.UIManager;

public class AcceptAllFileFilter extends javax.swing.filechooser.FileFilter{

	@Override
	public boolean accept(File file) {
		return true;
	}

	@Override
	public String getDescription() {
		return UIManager.getString("FileChooser.acceptAllFileFilterText");
	}

	@Override
	public String toString() {
		return getDescription();
	}

}
