package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.mq.IMessageSender;

/**
 * 
 * Scans local keys of tasks map and looks for irregularities.
 * 
 * Hazelcast (as of version 2.5) is not capable to inform client application
 * when a key migrates. So the idea is to periodically scan local keys and see
 * if a key needs our attention. This covers leaving (or crashing) as well as
 * joining of data nodes.
 * 
 * This method is also used by Mozilla organization in their crash analyzing
 * system.
 * 
 * TODO: maybe add reference to discussion TODO: create a document describing
 * all cases taken into account instead of inline comments TODO: reformat the
 * code (after it stabilizes a bit)
 * 
 * @author Martin Sixta
 */
final class LocalKeyScanner implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(LocalKeyScanner.class);

	private final ClusterContext clusterCtx;
	private IMessageSender<TaskMessage> sender;

	private final String nodeId;

	public LocalKeyScanner(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
		this.nodeId = clusterCtx.getId();
	}

	@Override
	public void run() {

		IMap<String, TaskEntry> map = clusterCtx.getTasksUtils().getTasksMap();

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

	private void checkEntry(TaskEntry entry) throws Exception {

		// log.debug("TaskEntry ID: {}, status: {}", entry.getId(), entry.getState().toString());

		if (!TMUtils.isOwner(entry, nodeId)) {
			log.debug("Will take over the task {}", entry.getId());
			sender.send(new NewOwnerTaskMessage(entry));
		}
	}

	public void withSender(IMessageSender<TaskMessage> sender) {
		this.sender = sender;
	}
}
