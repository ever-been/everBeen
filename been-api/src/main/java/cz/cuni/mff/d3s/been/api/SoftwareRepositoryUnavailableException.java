package cz.cuni.mff.d3s.been.api;

/**
 * @author donarus
 */
public class SoftwareRepositoryUnavailableException extends BeenApiException {

    public SoftwareRepositoryUnavailableException(String message) {
        super(message);
    }

    public SoftwareRepositoryUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoftwareRepositoryUnavailableException(Throwable cause) {
        super(cause);
    }
}
