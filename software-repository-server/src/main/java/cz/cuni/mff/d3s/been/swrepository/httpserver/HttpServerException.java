package cz.cuni.mff.d3s.been.swrepository.httpserver;

import cz.cuni.mff.d3s.been.cluster.ServiceException;

/**
 * An exception thrown by the software repository HTTP server
 * 
 * @author donarus
 * 
 */
@SuppressWarnings("serial")
public class HttpServerException extends ServiceException {

	/**
	 * Create a plain exception with null cause and a message.
	 * 
	 * @param message
	 *          The exception message
	 */
	public HttpServerException(String message) {
		super(message);
	}

	/**
	 * Create an exception with a wrapped cause.
	 * 
	 * @param message
	 *          The exception message
	 * @param cause
	 *          Cause of the exception
	 */
	public HttpServerException(String message, Throwable cause) {
		super(message, cause);
	}

}
