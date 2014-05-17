package cz.everbeen.restapi;

/**
 * An indication tha the REST API failed to connect to the cluster
 * @author darklight
 */
public class ClusterConnectionException extends Exception {
	public ClusterConnectionException(String message) {
		super(message);
	}
	public ClusterConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
}
