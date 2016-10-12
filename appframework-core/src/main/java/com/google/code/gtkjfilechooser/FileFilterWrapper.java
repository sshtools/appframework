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
