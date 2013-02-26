package cz.cuni.mff.d3s.been.bpk;

/**
 * Wraps all exceptions which can happen while manipulating a BpkConfiguration.
 * Can include file exceptions, JAXB related exceptions, and whatnot.
 * 
 * @author Martin Sixta
 */
public class BpkConfigurationException extends Exception {
	public BpkConfigurationException(String message) {
		super(message);
	}

	public BpkConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public BpkConfigurationException(Throwable cause) {
		super(cause);
	}

	protected BpkConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
