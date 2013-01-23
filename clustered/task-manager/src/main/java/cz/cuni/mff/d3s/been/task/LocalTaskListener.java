package cz.cuni.mff.d3s.been.task;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public LocalTaskListener() {
		taskMap = TasksUtils.getTasksMap();

		// TODO: does it belong here?
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



		String nodeId = ClusterUtils.getId();
		Transaction txn = null;
		String receiverId = null;


		try {

			// 0) Claim ownership of the node
			// TODO: too much going on , schduling/claiming ownership should be split-up
			entry.setOwnerId(nodeId);

			// 1) Find suitable Host Runtime
			receiverId = runtimeSelection.select(entry);

			// 2) Change task state to SCHEDULED and send message to the Host Runtime
			txn = ClusterUtils.getTransaction();

			{
				txn.begin(); // BEGIN TRANSACTION -----------------------------

				// Update content of the entry
				TaskEntries.setState(entry, TaskState.SCHEDULED, "Task sheduled on " + nodeId);
				entry.setRuntimeId(receiverId);

				// Update entry
				TasksUtils.putTask(entry);

				 // Send a message to the runtime
				TopicUtils.publish(RUNTIME_TOPIC, newRunTaskMessage(entry));

				txn.commit(); // END TRANSACTION ------------------------------
			}

		} catch (NoRuntimeFoundException e) {
			// Abort the task

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
		RunTaskMessage msg = new RunTaskMessage();
		msg.senderId = taskEntry.getOwnerId();
		msg.recieverId = taskEntry.getRuntimeId();
		msg.taskId = taskEntry.getId();
		return msg;
	}

	@Override
	public void entryRemoved(EntryEvent<String, TaskEntry> event) {
		TaskEntry entry = event.getValue();
		String taskId = event.getKey();

		log.info("Entry removed " + taskId);
	}

	@Override
	public void entryUpdated(EntryEvent<String, TaskEntry> event) {
		TaskEntry entry = event.getValue();
		String taskId = event.getKey();

		log.info("Entry updated " + taskId);
	}

	@Override
	public void entryEvicted(EntryEvent<String, TaskEntry> event) {
		TaskEntry entry = event.getValue();
		String taskId = event.getKey();

		log.info("Entry evicted " + taskId);
	}
}
