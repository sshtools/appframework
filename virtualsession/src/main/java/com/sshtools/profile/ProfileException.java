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
