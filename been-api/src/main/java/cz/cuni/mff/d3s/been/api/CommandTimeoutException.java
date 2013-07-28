package cz.cuni.mff.d3s.been.api;

/**
 * This exception should be thrown no command execution timeout.
 *
 * @author donarus
 */
public class CommandTimeoutException extends BeenApiException {

    public CommandTimeoutException(String message) {
        super(message);
    }

    public CommandTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandTimeoutException(Throwable cause) {
        super(cause);
    }

}
