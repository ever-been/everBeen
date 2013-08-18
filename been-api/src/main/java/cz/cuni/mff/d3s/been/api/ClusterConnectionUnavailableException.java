package cz.cuni.mff.d3s.been.api;

/**
 * This exception indicates that the connection to the BEEN cluster has been
 * lost.
 * 
 * @author donarus
 */
public class ClusterConnectionUnavailableException extends BeenApiException {

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *          the detail message
	 */
	public ClusterConnectionUnavailableException(String message) {
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
	public ClusterConnectionUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified {@link Throwable} as a cause.
	 * 
	 * @param cause
	 *          cause of the exception
	 */
	public ClusterConnectionUnavailableException(Throwable cause) {
		super(cause);
	}

}
