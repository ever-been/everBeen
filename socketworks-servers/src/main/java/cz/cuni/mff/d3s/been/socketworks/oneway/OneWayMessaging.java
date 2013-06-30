package cz.cuni.mff.d3s.been.socketworks.oneway;

import cz.cuni.mff.d3s.been.mq.*;
import cz.cuni.mff.d3s.been.socketworks.QueueGuard;

/**
 * Facade for one-way messaging.
 */
public final class OneWayMessaging {

    /**
     * Create a one-way server (listen-only)
     *
     * @param hostname Hostname to bind
     * @param queueName Associated queue name
     * @param handler Handler that processes incoming messages
     *
     * @return A {@link QueueGuard} keeper object that enacts the role of the server
     *
     * @throws MessagingException When the server cannot be created
     */
    public static final QueueGuard createServer(String hostname, String queueName, ReadOnlyHandler handler) throws MessagingException {
        return ReadOnlyGuard.create(hostname, queueName, handler);
    }

    /**
     * Create a one-way client (send-only)
     *
     * @param hostname Hostname of the server
     * @param port Port of the server
     *
     * @return An {@link IMessageQueue} that enables the user to instantiate an {@link IMessageSender} connected to the specified server
     */
    public static final IMessageQueue<String> createClient(String hostname, int port) {
        return Messaging.createTaskQueue(hostname, port);
    }
}
