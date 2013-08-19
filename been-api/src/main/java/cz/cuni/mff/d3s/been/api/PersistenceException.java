package cz.cuni.mff.d3s.been.api;

/**
 * This exception indicates an error with the persistence layer.
 * 
 * @author donarus
 */
public class PersistenceException extends BeenApiException {

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *          the detail message
	 */
	public PersistenceException(String message) {
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
	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified {@link Throwable} as a cause.
	 * 
	 * @param cause
	 *          cause of the exception
	 */
	public PersistenceException(Throwable cause) {
		super(cause);
	}

}
