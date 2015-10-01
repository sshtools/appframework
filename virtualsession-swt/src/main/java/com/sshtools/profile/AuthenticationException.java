/* HEADER */
package com.sshtools.profile;

/**
 * Exception thrown if the authentication with a host fails for some reason
 * during connection.
 */
public class AuthenticationException extends Exception {

	public AuthenticationException() {
		super();
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}

	public AuthenticationException(String message) {
		super(message);
	}
}
