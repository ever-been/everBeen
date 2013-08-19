package cz.cuni.mff.d3s.been.api;

/**
 * This exception indicates that the software repository is not running or is
 * not available.
 * 
 * @author donarus
 */
public class SoftwareRepositoryUnavailableException extends BeenApiException {

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *          the detail message
	 */
	public SoftwareRepositoryUnavailableException(String message) {
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
	public SoftwareRepositoryUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified {@link Throwable} as a cause.
	 * 
	 * @param cause
	 *          cause of the exception
	 */
	public SoftwareRepositoryUnavailableException(Throwable cause) {
		super(cause);
	}

}
