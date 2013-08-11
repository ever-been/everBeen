package cz.cuni.mff.d3s.been.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.task.msg.Messages;
import cz.cuni.mff.d3s.been.task.msg.TaskMessage;

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

	/** Executor to periodically schedule the scanner */
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	/** The scanner runnable */
	private final LocalKeyScannerRunnable runnable;

	/** This node's ID */
	private final String nodeId;

	/**
	 * Creates the LocalKeyScanner {@link TaskManagerService}.
	 * 
	 * @param clusterCtx
	 *          connection to the cluster
	 */
	public LocalKeyScanner(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
		this.nodeId = clusterCtx.getId();
		this.runnable = new LocalKeyScannerRunnable();
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

		for (String taskId : map.localKeySet()) {
			TaskEntry entry = map.get(taskId);

			if (entry == null) {
				continue;
			}

			try {
				checkEntry(entry);
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
	private void checkEntry(TaskEntry entry) throws Exception {

		log.debug("TaskEntry ID: {}, status: {}", entry.getId(), entry.getState().toString());

		if (entry.getState() == TaskState.WAITING) {
			log.debug("Will try to schedule WAITING task {}", entry.getState());
			TaskMessage msg = Messages.createCheckSchedulabilityMessage(entry);
			sender.send(msg);
			return;
		}

		if (!TMUtils.isOwner(entry, nodeId)) {
			log.debug("Will take over the task {}", entry.getId());
			TaskMessage msg = Messages.createNewTaskOwnerMessage(entry);
			sender.send(msg);
		}
	}

	@Override
	public void start() throws ServiceException {
		sender = createSender();
		// TODO configurable period
		scheduler.scheduleAtFixedRate(runnable, 30, 30, TimeUnit.SECONDS);

	}

	@Override
	public void stop() {
		scheduler.shutdown();
		sender.close();
	}
}
