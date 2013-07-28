package cz.cuni.mff.d3s.been.api;

/**
 * User: donarus
 * Date: 4/27/13
 * Time: 11:54 AM
 */
public class ClusterConnectionUnavailableException extends BeenApiException {

    public ClusterConnectionUnavailableException(String message) {
        super(message);
    }

    public ClusterConnectionUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClusterConnectionUnavailableException(Throwable cause) {
        super(cause);
    }
}
