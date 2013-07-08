package cz.cuni.mff.d3s.been.socketworks;

import cz.cuni.mff.d3s.been.mq.MessagingException;

import java.net.URL;

/**
 * This interface denotes a TCP connection keeper object.
 * @author darklight
 */
public interface QueueGuard {

    /**
     * Start listening.
     */
	void listen();

    /**
     * Stop listening.
     *
     * @throws MessagingException When the connection could not be terminated
     */
	void terminate() throws MessagingException;

    /**
     * @return The hostname on which this guard is listening
     */
    String getHostname();

    /**
     * @return The port on which this guard is listening; will be <code>null</code> if the guard isn't listening
     */
    Integer getPort();

    /**
     * @return A {@link String} representation of the URL on which this guard is available
     */
    String getConnection();
}
