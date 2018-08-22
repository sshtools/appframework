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
/*
 * Copyright 2010 Costantino Cerbo.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact me at c.cerbo@gmail.com if you need additional information or
 * have any questions.
 */
package com.google.code.gtkjfilechooser.filewatcher;

import java.io.File;
import java.util.EventObject;

/**
 * <p>
 * FileEvent
 * </p>
 * 
 * 
 * 
 * @author $Author:$
 * @version $Revision:$
 */
public class FileEvent extends EventObject {

	public enum FileEventType {
		CREATED, DELETED, MODIFIED
	}

	private File file;

	private FileEventType type;

	private long when;

	public FileEvent(Object aSource, File aFile, FileEventType aType) {
		super(aSource);
		source = aSource;
		file = aFile;
		type = aType;
		when = System.currentTimeMillis();
	}

	public File getFile() {
		return file;
	}

	public FileEventType getType() {
		return type;
	}

	/**
	 * Returns the timestamp in milliseconds of when this event occurred. By default is the time when the current event was created, but
	 * you can also change it with {@link #setWhen(long)}.
	 * 
	 * @return this event's timestamp
	 */
	public long getWhen() {
		return when;
	}

	public void setWhen(long aWhen) {
		when = aWhen;
	}

}
