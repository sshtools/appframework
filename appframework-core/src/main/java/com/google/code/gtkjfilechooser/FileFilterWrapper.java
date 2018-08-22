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
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return filter.getDescription();
	}

}
