package cz.cuni.mff.d3s.been.api;

/**
 * This exception should be thrown no command execution timeout.
 *
 * @author donarus
 */
public class CommandTimeoutException extends Throwable {

    public CommandTimeoutException(String message) {
        super(message);
    }

}
