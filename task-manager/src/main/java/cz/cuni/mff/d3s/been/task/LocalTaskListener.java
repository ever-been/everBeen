package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.task.message.NewTaskMessage;
import cz.cuni.mff.d3s.been.task.message.TaskMessage;
import cz.cuni.mff.d3s.been.task.message.UpdatedTaskMessage;

/**
 * Listens for local key events of the Task Map.
 * 
 * 
 * @author Martin Sixta
 */
final class LocalTaskListener implements EntryListener<String, TaskEntry>, IClusterService {
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
		if (cfg.isCacheValue() == true) {
			throw new RuntimeException("Cache value == true for BEEN_MAP_TASKS!");
		}

	}

	@Override
	public void start() {
		taskMap.addLocalEntryListener(this);
	}

	@Override
	public void stop() {
		taskMap.removeEntryListener(this);
	}

	@Override
	public void entryAdded(EntryEvent<String, TaskEntry> event) {
		TaskEntry entry = event.getValue();
		try {
			sender.send(new NewTaskMessage(entry));
		} catch (MessagingException e) {
			String msg = String.format("Cannot send message to %s on entry added", sender.getConnection());
			log.error(msg, e);
		}
	}

	@Override
	public void entryRemoved(EntryEvent<String, TaskEntry> event) {
		event.getValue();
		String taskId = event.getKey();

		log.info("Entry removed " + taskId);
	}

	@Override
	public void entryUpdated(EntryEvent<String, TaskEntry> event) {
		TaskEntry entry = event.getValue();
		try {
			sender.send(new UpdatedTaskMessage(entry));
		} catch (MessagingException e) {
			String msg = String.format("Cannot send message to %s on entry updated", sender.getConnection());
			log.error(msg, e);
		}

	}

	@Override
	public void entryEvicted(EntryEvent<String, TaskEntry> event) {
		event.getValue();
		String taskId = event.getKey();

		log.info("Entry evicted " + taskId);
	}

	public void withSender(IMessageSender<TaskMessage> sender) {
		this.sender = sender;
	}
}
