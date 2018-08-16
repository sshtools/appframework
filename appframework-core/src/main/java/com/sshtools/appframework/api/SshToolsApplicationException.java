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
package com.sshtools.appframework.api;

import java.lang.reflect.Method;

/**
 *
 *
 * @author $author$
 */
public class SshToolsApplicationException extends Exception {
	/**
	 * Creates a new SshToolsApplicationException object.
	 */
	public SshToolsApplicationException() {
		this(null, null);
	}

	/**
	 * Creates a new SshToolsApplicationException object.
	 *
	 * @param msg message
	 */
	public SshToolsApplicationException(String msg) {
		this(msg, null);
	}

	/**
	 * Creates a new SshToolsApplicationException object.
	 *
	 * @param msg message
	 * @param cause cause
	 */
	public SshToolsApplicationException(String msg, Throwable cause) {
		super(msg);
		if (cause != null) {
			try {
				Method m = getClass().getMethod("initCause", new Class[] { Throwable.class });
				m.invoke(this, new Object[] { cause });
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Creates a new SshToolsApplicationException object.
	 *
	 * @param cause cause
	 */
	public SshToolsApplicationException(Throwable cause) {
		this(null, cause);
	}
}
