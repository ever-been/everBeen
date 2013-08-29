package cz.cuni.mff.d3s.been.mq;

/**
 * High-level exception for all things that can go wrong with messaging.
 * 
 * @author Martin Sixta
 */
@SuppressWarnings("serial")
public class MessagingException extends Exception {

	public MessagingException() {
		super();
	}

	public MessagingException(String message) {
		super(message);
	}

	public MessagingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessagingException(Throwable cause) {
		super(cause);
	}

	protected MessagingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
