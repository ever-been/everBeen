package cz.cuni.mff.d3s.been.socketworks;

/**
 * An exception signaling that a handler failed to handle the message passed to
 * it.
 * 
 * @author darklight
 * 
 */
public class SocketHandlerException extends Exception {
	/** Serialization ID */
	private static final long serialVersionUID = -4635021280075068264L;

	/**
	 * Create a socket handler exception
	 */
	public SocketHandlerException() {}

	/**
	 * Create a socket handler exception with an error message
	 *
	 * @param message Error message
	 */
	public SocketHandlerException(String message) {
		super(message);
	}

	/**
	 * Create a socket handler exception with a cause
	 *
	 * @param cause Cause of this socket handler exception
	 */
	public SocketHandlerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a socket handler exception with an error message and a cause
	 *
	 * @param message Error message
	 * @param cause Cause of this exception
	 */
	public SocketHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a socket handler exception with a suppressable stack trace
	 *
	 * @param message Error message
	 * @param cause Cause of this exception
	 * @param enableSuppression Whether the stack trace should be suppressed
	 * @param writableStackTrace The stack trace
	 */
	public SocketHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
