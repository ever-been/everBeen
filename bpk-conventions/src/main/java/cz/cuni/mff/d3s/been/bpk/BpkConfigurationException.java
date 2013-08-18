package cz.cuni.mff.d3s.been.bpk;

/**
 * Wraps all exceptions which can happen while manipulating a BpkConfiguration.
 * Can include file exceptions, JAXB related exceptions, and whatnot.
 * 
 * @author Martin Sixta
 */
public class BpkConfigurationException extends Exception {

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *          the detail message
	 */
	public BpkConfigurationException(String message) {
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
	public BpkConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified {@link Throwable} as a cause.
	 * 
	 * @param cause
	 *          cause of the exception
	 */
	public BpkConfigurationException(Throwable cause) {
		super(cause);
	}

}
