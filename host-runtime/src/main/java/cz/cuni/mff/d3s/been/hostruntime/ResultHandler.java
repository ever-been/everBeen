package cz.cuni.mff.d3s.been.hostruntime;

import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;
import cz.cuni.mff.d3s.been.socketworks.oneway.ReadOnlyHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static cz.cuni.mff.d3s.been.cluster.Names.PERSISTENCE_QUEUE_NAME;

/**
 * {@link HostRuntime}'s logic of handling
 */
public class ResultHandler implements ReadOnlyHandler {
    private static final Logger log = LoggerFactory.getLogger(ResultHandler.class);

    private final IQueue<EntityCarrier> resultQueue;
    private final ObjectMapper objectMapper;
    private final ObjectReader resultReader;

    private ResultHandler(IQueue<EntityCarrier> resultQueue) {
        this.resultQueue = resultQueue;
        this.objectMapper = new ObjectMapper();
        this.resultReader = objectMapper.reader(EntityCarrier.class);
    }

    /**
     * Create a result handler
     *
     * @param ctx Hazelcast context to create this handler under. Is used to load distributed memory data structures, namely the queue containing results.
     *
     * @return A ready handler
     */
    public static ReadOnlyHandler create(ClusterContext ctx) {
        final IQueue<EntityCarrier> resultQueue = ctx.getQueue(PERSISTENCE_QUEUE_NAME);
        return new ResultHandler(resultQueue);
    }

    @Override
    public void handle(String message) throws SocketHandlerException {
        log.info("Unmarshalling result: {}", message);
        try {
            final EntityCarrier rc = resultReader.readValue(message);
            if (resultQueue.add(rc)) {
                log.debug("Queued result {}", rc.toString());
            } else {
                log.error("Could not put result {} to queue, delaying.", rc.toString());
                // TODO consider handling
            }
        } catch (IOException e) {
            log.error("Cannot deserialize result carrier:", e);
        }
    }
}
