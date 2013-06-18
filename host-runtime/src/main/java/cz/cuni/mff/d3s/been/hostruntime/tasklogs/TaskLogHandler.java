package cz.cuni.mff.d3s.been.hostruntime.tasklogs;

import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.TaskMessageType;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.socketworks.oneway.ReadOnlyHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A listen handler for task log messages.
 *
 * @author darklight
 */
public class TaskLogHandler implements ReadOnlyHandler {

    private static final Logger log = LoggerFactory.getLogger(TaskLogHandler.class);
    private static final EntityID LOG_ENTITY_ID = new EntityID().withKind("BEEN.log").withGroup("task");
    private static final String PREFIX_SEPARATOR = "#";

    private final ClusterContext ctx;
    private final IQueue<EntityCarrier> logQueue;

    private TaskLogHandler(ClusterContext ctx) {
        this.ctx = ctx;
        this.logQueue = ctx.getQueue(Names.LOG_QUEUE_NAME);
    }

    /**
     * Create a handler that listens to task log messages within the context of a cluster node.
     *
     * @param ctx Cluster context
     *
     * @return The handler
     */
    public static TaskLogHandler create(ClusterContext ctx) {
        return new TaskLogHandler(ctx);
    }

    private void handleMessage(TaskMessageType messageType, String message) {
        switch (messageType) {
            case LOG_MESSAGE:
                handleLogMessage(message);
                break;
            case TASK_RUNNING:
                handleTaskRunningMessage(message);
                break;
            case UNKNOWN:
                break;
        }
    }

    @Override
    public void handle(String message) {
        TaskMessageType messageType = getType(message);

        try {
            handleMessage(messageType, message);
        } catch (Exception e) {
            String msg = String.format("Cannot handle message '%s' of type %s", message, messageType.toString());
            log.error(msg, e);
        }
    }

    private void handleTaskRunningMessage(String message) {
        String taskId = stripPrefix(TaskMessageType.TASK_RUNNING, message);

        DebugAssistant debugAssistant = new DebugAssistant(ctx);
        debugAssistant.setSuspended(taskId, false);
    }

    private void handleLogMessage(String message) {
        try {
        logQueue.put(fabricateEntityTransport(stripPrefix(TaskMessageType.LOG_MESSAGE, message)));
        } catch (InterruptedException e) {
            log.error("Interrupted when trying to submit log message {} to cluster", message);
        }
    }

    private EntityCarrier fabricateEntityTransport(String message) {
        EntityCarrier ec = new EntityCarrier();
        ec.setEntityId(LOG_ENTITY_ID);
        ec.setEntityJSON(message);
        return ec;
    }

    private TaskMessageType getType(String message) {
        final int separatorIndex = message.indexOf(PREFIX_SEPARATOR);
        try {
            return (separatorIndex >= 0) ? TaskMessageType.valueOf(message.substring(0,separatorIndex)) : TaskMessageType.UNKNOWN;
        } catch (IllegalArgumentException e) {
            return TaskMessageType.UNKNOWN;
        }
    }

    /**
     * Strips prefix from the message.
     *
     * The message must contain the prefix.
     *
     * @param type
     *          message type
     * @param message
     *          message to strip the prefix from
     * @return message without prefix
     */
    private String stripPrefix(TaskMessageType type, String message) {
        String prefix = type.toString() + PREFIX_SEPARATOR;
        return message.substring(prefix.length());
    }

}
