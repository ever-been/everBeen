package cz.cuni.mff.d3s.been.logging;

/**
 * Protocol for cluster-wide handling of BEEN service log messages
 *
 * @author darklight
 */
public interface ServiceLogHandler {

    /**
     * Handle a single log message
     *
     * @param msg Log message to handle
     *
     * @throws Exception When something goes wrong when handling the message
     */
    void log(LogMessage msg) throws Exception;
}
