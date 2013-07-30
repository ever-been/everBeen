package cz.cuni.mff.d3s.been.api;

/**
 * @author donarus
 */
public class PersistenceException extends BeenApiException {

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }

}
