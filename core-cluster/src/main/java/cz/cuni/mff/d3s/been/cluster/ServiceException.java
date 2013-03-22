package cz.cuni.mff.d3s.been.cluster;

/**
 * An exception that should be thrown on BEEN service bootstrap failure.
 * 
 * @author darklight
 * 
 */
@SuppressWarnings("serial")
public class ServiceException extends Exception {

	public ServiceException() {}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(
			String message,
			Throwable cause,
			boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
