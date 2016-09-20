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
