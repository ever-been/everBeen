package cz.cuni.mff.d3s.been.api;

/**
 * User: donarus
 * Date: 4/27/13
 * Time: 11:54 AM
 */
public class ServiceUnavailableException extends RuntimeException {
	public ServiceUnavailableException(String message) {
		super(message);
	}
}
