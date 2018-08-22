/**
 * Maverick Virtual Session - Framework for a tabbed user interface of connections to some local or remote resources.
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
/* HEADER */
package com.sshtools.profile;

/**
 * Exception thrown when there are errors caused by handling of a
 * {@link ResourceProfile}.
 */
public class ProfileException extends Exception {
	private static final long serialVersionUID = 1320347127357230845L;

	/**
	 * Construct a new ProfileException with no message
	 */
	public ProfileException() {
		super();
	}

	/**
	 * Construct a new ProfileException with a message.
	 *
	 * @param message message
	 */
	public ProfileException(String message) {
		super(message);
	}

	/**
	 * Construct a new ProfileException with a message.
	 *
	 * @param message message
	 * @param cause cause
	 */
	public ProfileException(String message, Throwable cause) {
		super(message);
		setCause(cause);
	}

	/**
	 * Construct a new ProfileException with an underlying cause.
	 *
	 * @param cause cause
	 */
	public ProfileException(Throwable cause) {
		super();
		setCause(cause);
	}

	private void setCause(Throwable cause) {
		initCause(cause);
	}

	@Override
	public Throwable getCause() {
		return super.getCause();
	}
}
