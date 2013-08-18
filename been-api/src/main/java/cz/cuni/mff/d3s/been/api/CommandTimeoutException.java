package cz.cuni.mff.d3s.been.api;

/**
 * This exception should be thrown when a command execution times out.
 * 
 * @author donarus
 */
public class CommandTimeoutException extends BeenApiException {

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *          the detail message
	 */
	public CommandTimeoutException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message
	 *          the detail message
	 * @param cause
	 *          cause of the exception
	 */
	public CommandTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified {@link Throwable} as a cause.
	 * 
	 * @param cause
	 *          cause of the exception
	 */
	public CommandTimeoutException(Throwable cause) {
		super(cause);
	}

}
