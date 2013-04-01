package cz.cuni.mff.d3s.been.task.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Transaction;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.task.IRuntimeSelection;
import cz.cuni.mff.d3s.been.task.NoRuntimeFoundException;
import cz.cuni.mff.d3s.been.task.RandomRuntimeSelection;
import cz.cuni.mff.d3s.been.task.message.TaskMessage;

/**
 * @author Martin Sixta
 */
class NewTaskAction extends AbstractTaskAction {

	private static Logger log = LoggerFactory.getLogger(NewTaskAction.class);
	private IRuntimeSelection runtimeSelection;
	private static final String RUNTIME_TOPIC = Context.GLOBAL_TOPIC.getName();

	public NewTaskAction(ClusterContext clusterCtx, TaskMessage msg) {
		super(clusterCtx, msg);

		runtimeSelection = new RandomRuntimeSelection(clusterCtx);
	}

	@Override
	public void execute() {
		TaskEntry entry = msg.getEntry();
		String taskId = entry.getId();

		log.info("Received new task " + taskId);

		// TODO: check that the entry is correct

		String nodeId = clusterCtx.getId();
		Transaction txn = null;
		String receiverId = null;

		try {

			// 1) Find suitable Host Runtime
			receiverId = runtimeSelection.select(entry);

			// 2) Change task state to SCHEDULED and send message to the Host Runtime
			txn = clusterCtx.getTransaction();

			{
				txn.begin(); // BEGIN TRANSACTION -----------------------------
				// assert than nobody messed with the entry we are processing
				// The reason we are doing it here is to get a lock
				// on the entry (IMap.get under transaction) and make sure than
				// nobody got the chance to modify the entry ...
				TaskEntry entryCopy = clusterCtx.getTasksUtils().assertClusterEqualCopy(entry);

				// Claim ownership of the node
				entry.setOwnerId(nodeId);

				// Update content of the entry
				TaskEntries.setState(entry, TaskState.SCHEDULED, "Task sheduled on " + nodeId);
				entry.setRuntimeId(receiverId);

				// Update entry
				TaskEntry oldEntry = clusterCtx.getTasksUtils().putTask(entry);

				// Again, check that the entry did not change
				// This SHOULD NOT be necessary ... but leave it here for now
				clusterCtx.getTasksUtils().assertEqual(oldEntry, entryCopy);

				clusterCtx.getAtomicNumber(entry.getId()).set(5);

				txn.commit(); // END TRANSACTION ------------------------------
			}

			// Send a message to the runtime
			clusterCtx.getTopicUtils().publish(RUNTIME_TOPIC, newRunTaskMessage(entry));

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
}
