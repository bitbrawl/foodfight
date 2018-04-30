package org.bitbrawl.foodfight.controller;

/**
 * Signals a problem with a controller's code, whether it's because the
 * controller threw an exception or there's a problem with the controller's
 * structure. This exception is only used to wrap other exceptions.
 * 
 * @author Finn
 */
public final class ControllerException extends Exception {

	/**
	 * Constructs a new exception with the given detail message and cause.
	 * 
	 * @param message
	 *            the detail message
	 * @param cause
	 *            the cause, an exception thrown by the controller
	 */
	public ControllerException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
