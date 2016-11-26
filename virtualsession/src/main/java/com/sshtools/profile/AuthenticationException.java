/* HEADER */
package com.sshtools.profile;

/**
 * Exception thrown if the authentication with a host fails for some reason
 * during connection.
 */
public class AuthenticationException extends Exception {

	private static final long serialVersionUID = -437309166509232776L;

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
