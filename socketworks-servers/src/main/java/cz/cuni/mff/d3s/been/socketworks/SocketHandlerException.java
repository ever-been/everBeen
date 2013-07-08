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

	public SocketHandlerException() {}

	public SocketHandlerException(String message) {
		super(message);
	}

	public SocketHandlerException(Throwable cause) {
		super(cause);
	}

	public SocketHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SocketHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
