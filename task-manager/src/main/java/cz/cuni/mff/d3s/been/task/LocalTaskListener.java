package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Transaction;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.ClusterUtils;
import cz.cuni.mff.d3s.been.core.TasksUtils;
import cz.cuni.mff.d3s.been.core.TopicUtils;
import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * Listens for local key events of the Task Map.
 * 
 * 
 * @author Martin Sixta
 */
final class LocalTaskListener implements EntryListener<String, TaskEntry>, IClusterService {
	private static final Logger log = LoggerFactory.getLogger(LocalTaskListener.class);
	private static final String RUNTIME_TOPIC = Context.GLOBAL_TOPIC.getName();

	private IRuntimeSelection runtimeSelection;

	private IMap<String, TaskEntry> taskMap;
	private final TasksUtils tasksUtils;
	private final TaskEntries taskEntries;

	public LocalTaskListener(TasksUtils tasksUtils, TaskEntries taskEntries) {
		this.tasksUtils = tasksUtils;
		this.taskEntries = taskEntries;
		taskMap = tasksUtils.getTasksMap();
		MapConfig cfg = tasksUtils.getTasksMapConfig();

		if (cfg == null) {
			throw new RuntimeException("BEEN_MAP_TASKS! does not have a config!");
		}
		if (cfg.isCacheValue() == true) {
			throw new RuntimeException("Cache value == true for BEEN_MAP_TASKS!");
		}

		runtimeSelection = new RandomRuntimeSelection();

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
		String taskId = event.getKey();

		log.info("Received new task " + taskId);

		// TODO: check that the entry is correct

		String nodeId = ClusterUtils.getId();
		Transaction txn = null;
		String receiverId = null;

		try {

			// 1) Find suitable Host Runtime
			receiverId = runtimeSelection.select(entry);

			// 2) Change task state to SCHEDULED and send message to the Host Runtime
			txn = ClusterUtils.getTransaction();

			{
				txn.begin(); // BEGIN TRANSACTION -----------------------------
				// assert than nobody messed with the entry we are processing
				// The reason we are doing it here is to get a lock
				// on the entry (IMap.get under transaction) and make sure than
				// nobody got the chance to modify the entry ...
				TaskEntry entryCopy = tasksUtils.assertClusterEqualCopy(entry);

				// Claim ownership of the node
				entry.setOwnerId(nodeId);

				// Update content of the entry
				taskEntries.setState(entry, TaskState.SCHEDULED, "Task sheduled on " + nodeId);
				entry.setRuntimeId(receiverId);

				// Update entry
				TaskEntry oldEntry = tasksUtils.putTask(entry);

				// Again, check that the entry did not change
				// This SHOULD NOT be necessary ... but leave it here for now
				tasksUtils.assertEqual(oldEntry, entryCopy);

				// Send a message to the runtime
				TopicUtils.publish(RUNTIME_TOPIC, newRunTaskMessage(entry));

				ClusterUtils.getAtomicNumber(entry.getId()).set(5);

				txn.commit(); // END TRANSACTION ------------------------------
			}

		} catch (NoRuntimeFoundException e) {
			// TODO: Abort the task?
			log.warn("No runtime found for task " + entry.getId(), e);

			return;
		} catch (Throwable e) {
			log.error("Will rollback the transaction", e);
			e.printStackTrace();
			// TODO: try again or abort the task!
			if (txn != null) {
				try {
					log.info("Rollback on " + taskId);
					txn.rollback();
				} catch (Throwable t) {
					//quell
				};
			}
		}

		log.info("Task " + taskId + " scheduled on " + receiverId);
	}

	private RunTaskMessage newRunTaskMessage(TaskEntry taskEntry) {
		String senderId = taskEntry.getOwnerId();
		String recieverId = taskEntry.getRuntimeId();
		String taskId = taskEntry.getId();
		return new RunTaskMessage(senderId, recieverId, taskId);
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
		String taskId = event.getKey();

		switch (entry.getState()) {
			case ABORTED:
				log.info("Task has been ABORTED: " + taskId);
				break;
			case SUBMITTED:
				if (!entry.getRuntimeId().equals("0")) {
					//timeout exceeded, stale task
					// what now?
				} else {
					//
				}

				break;

		}

		log.info("Entry updated " + taskId);
		log.info(tasksUtils.toXml(entry));
	}

	@Override
	public void entryEvicted(EntryEvent<String, TaskEntry> event) {
		event.getValue();
		String taskId = event.getKey();

		log.info("Entry evicted " + taskId);
	}
}
