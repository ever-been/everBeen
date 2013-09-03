package cz.cuni.mff.d3s.been.mq;

/**
 * High-level exception for all things that can go wrong with messaging.
 * 
 * @author Martin Sixta
 */
@SuppressWarnings("serial")
public class MessagingException extends Exception {

	/**
	 * Create a messaging exception
	 */
	public MessagingException() {
		super();
	}

	/**
	 * Create a messaging exception with an error message
	 *
	 * @param message Error message
	 */
	public MessagingException(String message) {
		super(message);
	}

	/**
	 * Create a messaging exception with an error message and a cause
	 *
	 * @param message Error message
	 * @param cause Cause of this exception
	 */
	public MessagingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a messaging exception with a cause
	 *
	 * @param cause Cause of this exception
	 */
	public MessagingException(Throwable cause) {
		super(cause);
	}

	protected MessagingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
