package cz.cuni.mff.d3s.been.manager;

import static cz.cuni.mff.d3s.been.core.task.TaskState.*;
import static cz.cuni.mff.d3s.been.manager.TaskManagerConfiguration.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.manager.msg.Messages;
import cz.cuni.mff.d3s.been.manager.msg.TaskMessage;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.util.PropertyReader;

/**
 * 
 * Scans local keys of tasks map and looks for irregularities.
 * 
 * Hazelcast (as of version 2.5) is not capable to inform client application
 * when a key migrates. So the idea is to periodically scan local keys and see
 * if a key needs our attention. This covers leaving (or crashing) as well as
 * joining of data nodes.
 * 
 * @author Martin Sixta
 */
final class LocalKeyScanner extends TaskManagerService {
	/** logging */
	private static final Logger log = LoggerFactory.getLogger(LocalKeyScanner.class);

	/** Connection to the cluster */
	private final ClusterContext clusterCtx;

	/** Task Action Queue */
	private IMessageSender<TaskMessage> sender;

	/** The scanner runnable */
	private final LocalKeyScannerRunnable runnable;

	/** This node's ID */
	private final String nodeId;

	private final PropertyReader propertyReader;

	/**
	 * Creates the LocalKeyScanner {@link TaskManagerService}.
	 * 
	 * @param clusterCtx
	 *          connection to the cluster
	 */
	public LocalKeyScanner(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
		this.nodeId = clusterCtx.getCluster().getLocalMember().getUuid();
		this.runnable = new LocalKeyScannerRunnable();
		this.propertyReader = PropertyReader.on(clusterCtx.getProperties());
	}

	/** Runnable to schedule with the executor */
	private class LocalKeyScannerRunnable implements Runnable {

		@Override
		public void run() {
			// pokemon block, aka. catch-them-all (Executors tend to silently ignore Exceptions)
			try {
				doRun();
			} catch (Exception e) {
				log.error("Unknown error in TaskManager (LocalKeyScanner)", e);
			}

		}
	}

	/**
	 * The actual "run()" method
	 * 
	 * @throws Exception
	 *           when it rains
	 */
	private void doRun() throws Exception {
		IMap<String, TaskEntry> map = clusterCtx.getTasks().getTasksMap();

		Set<String> runtimeIds = new HashSet<>();
		for (RuntimeInfo info : clusterCtx.getRuntimes().getRuntimes()) {
			runtimeIds.add(info.getId());
		}

		for (String taskId : map.localKeySet()) {
			TaskEntry entry = map.get(taskId);

			if (entry == null) {
				continue;
			}

			try {
				checkEntry(runtimeIds, entry);
			} catch (Exception e) {
				log.error("Error when checking TaskEntry " + taskId, e);
			}

		}
	}

	/**
	 * Checks one {@link TaskEntry} for irregularities.
	 * 
	 * @param entry
	 *          The entry to check
	 * @throws Exception
	 *           when it rains
	 */
	private void checkEntry(final Set<String> runtimesIds, final TaskEntry entry) throws Exception {

		log.debug("TaskEntry ID: {}, status: {}", entry.getId(), entry.getState().toString());

		final TaskState state = entry.getState();

		boolean isWaiting = (state == WAITING);
		boolean isAccepted = (state == ACCEPTED);
		boolean isDone = (state == ABORTED || state == FINISHED);
		boolean isScheduled = (state == SCHEDULED);
		boolean isRunning = (state == RUNNING);
		boolean isRuntimeOffline = !runtimesIds.contains(entry.getRuntimeId());
		boolean isFromPersistence = entry.isLoadedFromPersistence();

		// Cluster restart
		if (!isDone && isRuntimeOffline && isFromPersistence) {
			String logMsg = String.format("Will abort task '%s' because of cluster restart", entry.getId());
			log.debug(logMsg);

			sender.send(Messages.createAbortTaskMessage(entry, logMsg));
			return;
		}

		// Failed Host Runtime of a scheduled task
		if ((isScheduled || isAccepted) && isRuntimeOffline) {
			String logMsg = String.format("Will reschedule '%s' because of Host Runtime failure", entry.getId());
			log.debug(logMsg);

			sender.send(Messages.createRescheduleTaskMessage(entry));
			return;
		}

		// Failed Host Runtime of a running task
		if (isRunning && isRuntimeOffline) {
			String logMsg = String.format("Will abort '%s' because of Host Runtime failure", entry.getId());
			log.debug(logMsg);

			sender.send(Messages.createAbortTaskMessage(entry, logMsg));
			return;
		}

		if (isWaiting) {
			log.debug("Will try to schedule WAITING task {}", entry.getState());
			TaskMessage msg = Messages.createCheckSchedulabilityMessage(entry);
			sender.send(msg);
			return;
		}

	}

	@Override
	public void start() throws ServiceException {
		sender = createSender();
		int delay = propertyReader.getInteger(SCANNER_INITIAL_DELAY, DEFAULT_SCANNER_INITIAL_DELAY);
		int period = propertyReader.getInteger(SCANNER_PERIOD, DEFAULT_SCANNER_PERIOD);
		clusterCtx.schedule(runnable, delay, period, TimeUnit.SECONDS);
	}

	@Override
	public void stop() {
		sender.close();
	}

}
