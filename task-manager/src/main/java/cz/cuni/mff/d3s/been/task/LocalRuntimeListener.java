package cz.cuni.mff.d3s.been.task;

import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * Listens on local changes of Host Runtimes.
 * 
 * Takes necessary actions (i.e. rescheduling of waiting tasks).
 * 
 * @author Martin Sixta
 */
final class LocalRuntimeListener extends TaskManagerService implements EntryListener<String, RuntimeInfo> {

	private static final String WAITING_TASKS_QUERY = "state = cz.cuni.mff.d3s.been.core.task.TaskState.WAITING";

	/** logging */
	private static final Logger log = LoggerFactory.getLogger(LocalRuntimeListener.class);

	/** connection to the cluster */
	private ClusterContext clusterCtx;

	/** for sending task manager messages for processing */
	private IMessageSender<TaskMessage> sender;

	/** Map with Host Runtimes */
	private final IMap<String, RuntimeInfo> runtimesMap;

	/** Map with tasks */
	private final IMap<String, TaskEntry> tasksMap;

	public LocalRuntimeListener(ClusterContext clusterCtx) {

		this.clusterCtx = clusterCtx;
		this.runtimesMap = clusterCtx.getRuntimesUtils().getRuntimeMap();
		this.tasksMap = clusterCtx.getTasksUtils().getTasksMap();

	}

	@Override
	public synchronized void entryAdded(EntryEvent<String, RuntimeInfo> event) {
		log.debug("Host Runtime added: {}", event.getKey());

		scheduleWaitingTasks();

		log.debug("DONE");

	}

	@Override
	public synchronized void entryRemoved(EntryEvent<String, RuntimeInfo> event) {
		log.debug("Host Runtime removed: {}", event.getKey());

	}

	@Override
	public synchronized void entryUpdated(EntryEvent<String, RuntimeInfo> event) {
		log.debug("Host Runtime updated: {}", event.getKey());

		RuntimeInfo oldValue = event.getOldValue();
		RuntimeInfo currentValue = event.getValue();

		if (oldValue.getTaskCount() != currentValue.getTaskCount()) {
			if (currentValue.getTaskCount() == 0) {
				scheduleWaitingTasks();
			}
		}

	}

	@Override
	public synchronized void entryEvicted(EntryEvent<String, RuntimeInfo> event) {
		log.debug("Host Runtime evicted: {}", event.getKey());
	}

	@Override
	public void start() throws ServiceException {
		sender = createSender();

		runtimesMap.addLocalEntryListener(this);
	}

	@Override
	public void stop() {
		runtimesMap.removeEntryListener(this);
		sender.close();
	}

	/**
	 * Reschedules all waiting tasks.
	 */
	private void scheduleWaitingTasks() {
		for (TaskEntry entry : getWaitingTasks()) {
			try {
				sender.send(new TaskChangedMessage(entry));
			} catch (MessagingException e) {
				String msg = String.format("Cannot send message to '%s'", sender.getConnection());
				log.error(msg, e);
			}

		}
	}

	/**
	 * Returns all waiting tasks
	 * 
	 * @return all waiting tasks
	 */
	private Collection<TaskEntry> getWaitingTasks() {
		try {

			return tasksMap.values(new SqlPredicate(WAITING_TASKS_QUERY));

		} catch (Exception e) {
			log.error("Error while looking for waiting tasks", e);
		}

		return Collections.emptyList();

	}
}
