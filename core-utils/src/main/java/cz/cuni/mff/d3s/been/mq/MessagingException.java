package cz.cuni.mff.d3s.been.mq;

/**
 * High-level exception for all things that can go wrong with messaging.
 * 
 * @author Martin Sixta
 */
public class MessagingException extends Exception {

	/** {@inheritDoc} */
	public MessagingException() {
		super();
	}
	/** {@inheritDoc} */
	public MessagingException(String message) {
		super(message);
	}

	/** {@inheritDoc} */
	public MessagingException(String message, Throwable cause) {
		super(message, cause);
	}

	/** {@inheritDoc} */
	public MessagingException(Throwable cause) {
		super(cause);
	}

	/** {@inheritDoc} */
	protected MessagingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
