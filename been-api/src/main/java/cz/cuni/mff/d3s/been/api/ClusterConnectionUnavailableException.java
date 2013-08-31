package cz.cuni.mff.d3s.been.api;

/**
 * This exception indicates that the connection to the BEEN cluster has been
 * lost.
 * 
 * @author donarus
 */
public class ClusterConnectionUnavailableException extends BeenApiException {

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *          the detail message
	 */
	public ClusterConnectionUnavailableException(String message) {
		super(message);
	}

}
