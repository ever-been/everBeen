package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.QueueGuard;

/**
 * Facade for two-way socket communication.
 */
public final class TwoWayMessaging {

    /**
     * Create a two-way messaging server. Each request is handled separately in a dedicated thread.
     *
     * @param hostname Hostname to bind
     * @param queueName Name of the queue
     * @param handlerFactory Factory for {@link ReadReplyHandler} objects (they will be used to handle messages)
     *
     * @return
     * @throws MessagingException
     */
    public static final QueueGuard createServer(String hostname, String queueName, ReadReplyHandlerFactory handlerFactory) throws MessagingException {
        return ReadReplyGuard.create(hostname, handlerFactory);
    }

    public static final IMessageQueue<String> createClient() {
        // TODO implement a request socket
        return null;
    }
}


