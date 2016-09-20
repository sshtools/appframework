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
/**
 * Wrap a {@link javax.swing.filechooser.FileFilter} into a {@link java.io.FileFilter}.
 * 
 * @author Costantino Cerbo
 *
 */
public class FileFilterWrapper implements java.io.FileFilter {

	private javax.swing.filechooser.FileFilter filter;

	public FileFilterWrapper(javax.swing.filechooser.FileFilter filter) {
		this.filter = (filter != null) ? filter : new AcceptAllFileFilter();		
	}

	@Override
	public boolean accept(File pathname) {
		return filter.accept(pathname);
	}

	@Override
	public String toString() {
		return filter.getDescription();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}

}
