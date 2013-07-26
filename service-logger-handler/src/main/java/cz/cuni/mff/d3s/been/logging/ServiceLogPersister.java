package cz.cuni.mff.d3s.been.logging;

import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.BeenPackageIdentifier;
import cz.cuni.mff.d3s.been.cluster.*;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A persistence hook for service log messages
 */
public class ServiceLogPersister implements IClusterService {

    private IQueue<EntityCarrier> logPersistence;
    private Queue<LogMessage> logs = new ConcurrentLinkedQueue<LogMessage>();
    private final JSONUtils jsonUtils = JSONUtils.newInstance();
    private ClusterContext ctx;
    private String beenId;
    private String hostRuntimeId;

    /**
     * Create a BEEN service log persister. Once activated, this persister will hook up to {@link PersistentServiceLogHandler} instances, making them dump logs to the persistence layer.
     *
     * @return A new log persister
     */
    ServiceLogPersister() {
    }

    /**
     * Get the static instance hooked up to service log handlers. Needs to be activated with a cluster context to start working.
     *
     * @param hostRuntimeId ID of the runtime that's doing the logging. Should be <code>null</code> if there is no host runtime running in this instance
     * @param beenId ID of this BEEN instance
     *
     * @return The log persister
     */
    public static ServiceLogPersister getHandlerInstance(ClusterContext ctx, String beenId, String hostRuntimeId) {
        final ServiceLogPersister persister = PersistentServiceLogHandler.persister;
        persister.ctx = ctx;
        persister.beenId = beenId;
        persister.hostRuntimeId = hostRuntimeId;
        return persister;
    }

    /**
     * Log a message
     *
     * @param msg message to log
     */
    void log(LogMessage msg) {
        logs.add(msg);
        if (isActive()) {
            pushLogs();
        }
    }

    /**
     * Keep processing the queue until it is empty
     */
    void pushLogs() {
        LogMessage polledMsg;
        while((polledMsg = logs.poll()) != null) {
            final ServiceLogMessage serviceMessage = new ServiceLogMessage().withMessage(polledMsg).withHostRuntimeId(hostRuntimeId).withBeenId(beenId).withServiceName(extractServiceName(polledMsg.getName()));
            try {
                logPersistence.put(new EntityCarrier().withId(Entities.LOG_SERVICE).withData(jsonUtils.serialize(serviceMessage)));
            } catch (InterruptedException e) {
                System.err.println(String.format("Cannot log following message to cluster: threads handling distributed data structures were unexpectedly interrupted.\n%s", polledMsg.toString()));
            } catch (JsonException e) {
                System.err.println(String.format("Cannot serialize following message: %s", polledMsg.toString()));
            }
        }
    }

    /**
     * See whether the log persister is active
     *
     * @return <code>true</code> if the persister is active (initialized with a cluster context), <code>false</code> otherwise
     */
    boolean isActive() {
        return this.logPersistence != null;
    }

    @Override
    public void start() throws ServiceException {
        this.logPersistence = ctx.getQueue(Names.PERSISTENCE_QUEUE_NAME);

        pushLogs();
    }

    @Override
    public void stop() {
        logPersistence = null;
        ctx = null;
        hostRuntimeId = null;
        beenId = null;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Reaper createReaper() {
        return new Reaper() {
            @Override
            protected void reap() throws InterruptedException {
                ServiceLogPersister.this.stop();
            }
        };
    }

    private String extractServiceName(String loggerName) {
        final String BEEN_PREFIX = BeenPackageIdentifier.class.getPackage().getName();
        if (loggerName.startsWith(BEEN_PREFIX)) {
            return loggerName.substring(BEEN_PREFIX.length(), loggerName.indexOf('.', BEEN_PREFIX.length()));
        } else {
            return loggerName.substring(0, loggerName.lastIndexOf('.'));
        }
    }
}
