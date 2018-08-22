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
 * Exception thrown if the authentication with a host fails for some reason
 * during connection.
 */
public class AuthenticationException extends Exception {
	private static final long serialVersionUID = -437309166509232776L;

	/**
	 * Constructor.
	 */
	public AuthenticationException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message message
	 * @param cause caused
	 */
	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause cause
	 */
	public AuthenticationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message message
	 */
	public AuthenticationException(String message) {
		super(message);
	}
}
