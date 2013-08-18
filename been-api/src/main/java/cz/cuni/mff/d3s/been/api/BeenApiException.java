package cz.cuni.mff.d3s.been.api;

/**
 * This class serves as a wrapper exception for inner exception within the
 * {@link BeenApi} class.
 * 
 * @author donarus
 */
public class BeenApiException extends Exception {

	/** detailed reason why the exception was thrown */
	private String detailedReason;

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *          the detail message
	 */
	public BeenApiException(String message) {
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
	public BeenApiException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified {@link Throwable} as a cause.
	 * 
	 * @param cause
	 *          cause of the exception
	 */
	public BeenApiException(Throwable cause) {
		super(cause);
	}

	/**
	 * Sets the detail message string of this throwable.
	 * 
	 * @param detailedReason
	 *          the detail message string of this instance
	 */
	public void setDetailedReason(String detailedReason) {
		this.detailedReason = detailedReason;
	}

	/**
	 * Returns the detail message string of this throwable.
	 * 
	 * @return the detail message string of this instance
	 */
	public String getDetailedReason() {
		return detailedReason;
	}

}
