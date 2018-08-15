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
