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
		TaskEntry taskEntry = event.getValue();
		String taskId = event.getKey();

		log.info("Received new task " + taskId);



		String nodeId = ClusterUtils.getId();
		Transaction txn = null;
		String receiverId = null;


		try {
			// 1) Find suitable Host Runtime
			receiverId = runtimeSelection.select(taskEntry);

			// 2) Change task state to SCHEDULED and send message to the Host Runtime
			txn = ClusterUtils.getTransaction();

			{
				txn.begin(); // BEGIN TRANSACTION -----------------------------

				TaskEntries.setState(taskEntry, TaskState.SCHEDULED, "Task sheduled on " + nodeId);
				TasksUtils.setTask(taskEntry);

				RunTaskMessage msg = newRunTaskMessage(taskId, nodeId, receiverId);

				TopicUtils.publish(Context.GLOBAL_TOPIC.getName(), msg);

				txn.commit(); // END TRANSACTION ------------------------------
			}

		} catch (NoRuntimeFoundException e) {
			// Abort the task

			return;
		} catch (Throwable e) {
			if (txn != null) {
				try {
					txn.rollback();
				} catch (Throwable t) {/*quell*/};
			}
		}

		log.info("Task " + taskId + " scheduled on " + receiverId);

	}

	private RunTaskMessage newRunTaskMessage(String taskId, String nodeId, String receiverId) {
		RunTaskMessage msg = new RunTaskMessage();
		msg.senderId = nodeId;
		msg.recieverId = receiverId;
		msg.taskId = taskId;
		return msg;
	}

	@Override
	public void entryRemoved(EntryEvent<String, TaskEntry> event) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void entryUpdated(EntryEvent<String, TaskEntry> event) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void entryEvicted(EntryEvent<String, TaskEntry> event) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
