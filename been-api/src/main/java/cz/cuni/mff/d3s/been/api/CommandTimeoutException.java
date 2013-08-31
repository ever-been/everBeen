package cz.cuni.mff.d3s.been.api;

/**
 * This exception should be thrown when a command execution times out.
 * 
 * @author donarus
 */
public class CommandTimeoutException extends BeenApiException {

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *          the detail message
	 */
	public CommandTimeoutException(String message) {
		super(message);
	}

}
