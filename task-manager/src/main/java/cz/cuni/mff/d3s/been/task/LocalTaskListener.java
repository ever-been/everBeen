package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * Listens for local key events of the Task Map.
 * 
 * 
 * @author Martin Sixta
 */
final class LocalTaskListener extends TaskManagerService implements EntryListener<String, TaskEntry> {
	private static final Logger log = LoggerFactory.getLogger(LocalTaskListener.class);

	private IMap<String, TaskEntry> taskMap;
	private ClusterContext clusterCtx;
	private IMessageSender<TaskMessage> sender;

	public LocalTaskListener(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
		taskMap = clusterCtx.getTasksUtils().getTasksMap();
		MapConfig cfg = clusterCtx.getTasksUtils().getTasksMapConfig();

		if (cfg == null) {
			throw new RuntimeException("BEEN_MAP_TASKS! does not have a config!");
		}
		if (cfg.isCacheValue()) {
			throw new RuntimeException("Cache value == true for BEEN_MAP_TASKS!");
		}

	}

	@Override
	public void start() throws ServiceException {
		sender = createSender();

		taskMap.addLocalEntryListener(this);
	}

	@Override
	public void stop() {
		taskMap.removeEntryListener(this);
		sender.close();
	}

	@Override
	public synchronized void entryAdded(EntryEvent<String, TaskEntry> event) {
		log.debug("TaskEntry {} added", event.getKey());

		TaskEntry entry = event.getValue();
		try {
			sender.send(new NewTaskMessage(entry));
		} catch (MessagingException e) {
			String msg = String.format("Cannot send message to '%s'", sender.getConnection());
			log.error(msg, e);
		}
	}

	@Override
	public synchronized void entryRemoved(EntryEvent<String, TaskEntry> event) {
		log.info("TaskEntry {} removed ", event.getKey());
	}

	@Override
	public synchronized void entryUpdated(EntryEvent<String, TaskEntry> event) {
		log.debug("TaskEntry {} updated", event.getKey());

		TaskEntry entry = event.getValue();

		// skip waiting tasks
		if (entry.getState() == TaskState.WAITING) {
			return;
		}

		try {
			sender.send(new TaskChangedMessage(entry));
		} catch (MessagingException e) {
			String msg = String.format("Cannot send message to '%s'", sender.getConnection());
			log.error(msg, e);
		}
	}

	@Override
	public synchronized void entryEvicted(EntryEvent<String, TaskEntry> event) {
		log.info("TaskEntry {} evicted", event.getKey());

		// TODO figure out why the entry was evicted (i.e. stale task)

	}
}
