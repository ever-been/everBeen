package cz.cuni.mff.d3s.been.cluster;

/**
 * An exception that should be thrown on BEEN service bootstrap failure.
 * 
 * @author darklight
 * 
 */
@SuppressWarnings("serial")
public class ServiceException extends Exception {

	/**
	 * Creates ServiceException.
	 */
	public ServiceException() {}

	/**
	 * Creates ServiceException with a message
	 * 
	 * @param message
	 *          message of the exception
	 */
	public ServiceException(String message) {
		super(message);
	}

	/**
	 * Creates ServiceException with a cause
	 * 
	 * @param cause
	 *          the cause of the exception
	 */
	public ServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates ServiceException with a message and a cause
	 * 
	 * @param message
	 *          message of the exception
	 * @param cause
	 *          the cause of the exception
	 */
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
