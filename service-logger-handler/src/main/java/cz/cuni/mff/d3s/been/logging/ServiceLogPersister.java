package cz.cuni.mff.d3s.been.logging;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.IQueue;

import com.hazelcast.core.RuntimeInterruptedException;
import cz.cuni.mff.d3s.been.BeenPackageIdentifier;
import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * A persistence hook for service log messages
 */
public class ServiceLogPersister implements IClusterService {

	volatile private IQueue<EntityCarrier> logPersistence;
	private BlockingQueue<LogMessage> logs = new LinkedBlockingQueue<>();
	private ClusterContext ctx;
	private String beenId;
	private String hostRuntimeId;
	private ServiceLogPersisterThread persisterThread;

	/**
	 * Create a BEEN service log persister. Once activated, this persister will
	 * hook up to {@link PersistentServiceLogHandler} instances, making them dump
	 * logs to the persistence layer.
	 */
	ServiceLogPersister() {}

	/**
	 * Get the static instance hooked up to service log handlers. Needs to be
	 * activated with a cluster context to start working.
	 * 
	 * @param hostRuntimeId
	 *          ID of the runtime that's doing the logging. Should be
	 *          <code>null</code> if there is no host runtime running in this
	 *          instance
	 * @param beenId
	 *          ID of this BEEN instance
	 * @param ctx Cluster context of this BEEN instance
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
	 * @param msg
	 *          message to log
	 */
	void log(LogMessage msg) {
		logs.add(msg);
	}

	@Override
	public void start() throws ServiceException {
		this.logPersistence = ctx.getQueue(Names.PERSISTENCE_QUEUE_NAME);

		this.persisterThread = new ServiceLogPersisterThread(logPersistence, logs, beenId, hostRuntimeId);

		this.persisterThread.start();

	}

	@Override
	public void stop() {
		logPersistence = null;
		ctx = null;
		hostRuntimeId = null;
		beenId = null;

		persisterThread.setStop();

		try {
			persisterThread.interrupt();
			persisterThread.join(TimeUnit.SECONDS.toMillis(30));
		} catch (InterruptedException e) {
			// not much we can do at this point
			e.printStackTrace();
		}
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

	private static class ServiceLogPersisterThread extends Thread {

		private final IQueue<EntityCarrier> logPersistence;
		private final BlockingQueue<LogMessage> logs;
		private final String beenId;
		private final String hostRuntimeId;
		private volatile boolean run = true;

		private final JSONUtils jsonUtils = JSONUtils.newInstance();

		ServiceLogPersisterThread(
				final IQueue<EntityCarrier> logPersistence,
				final BlockingQueue<LogMessage> logs,
				String beenId,
				String hostRuntimeId) {

			this.logPersistence = logPersistence;
			this.logs = logs;
			this.beenId = beenId;
			this.hostRuntimeId = hostRuntimeId;
		}

		void setStop() {
			this.run = false;
		}

		@Override
		public void run() {

			LogMessage polledMsg;

			Collection<LogMessage> drain = new ArrayList<>();

			while (run && !Thread.currentThread().isInterrupted()) {

				try {
					polledMsg = logs.poll(30, SECONDS);
				} catch (InterruptedException e) {
					break;
				}

				if (polledMsg == null) {
					continue;
				}

				persist(polledMsg);

				logs.drainTo(drain);

				for (LogMessage logMessage : drain) {
					persist(logMessage);
				}

				drain.clear();

			}

			// drain what we can
			logs.drainTo(drain);

			for (LogMessage logMessage : drain) {
				persist(logMessage);
			}

		}

		private void persist(final LogMessage polledMsg) {
			final ServiceLogMessage serviceMessage = new ServiceLogMessage().withMessage(polledMsg).withHostRuntimeId(
					hostRuntimeId).withBeenId(beenId).withServiceName(extractServiceName(polledMsg.getName()));
			try {
				logPersistence.put(new EntityCarrier().withId(Entities.LOG_SERVICE.getId()).withData(
						jsonUtils.serialize(serviceMessage)));
			} catch (InterruptedException | RuntimeInterruptedException e) {
				System.err.println(String.format(
						"Cannot log following message to cluster: threads handling distributed data structures were unexpectedly interrupted.\n%s",
						polledMsg.toString()));
			} catch (JsonException e) {
				System.err.println(String.format("Cannot serialize following message: %s", polledMsg.toString()));
			}
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
}
