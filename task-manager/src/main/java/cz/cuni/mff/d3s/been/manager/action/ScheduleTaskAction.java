package cz.cuni.mff.d3s.been.manager.action;

import static cz.cuni.mff.d3s.been.core.task.TaskState.SCHEDULED;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.Tasks;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.manager.selector.NoRuntimeFoundException;
import cz.cuni.mff.d3s.been.manager.selector.RuntimeSelectors;

/**
 * 
 * Action which tries to schedule a task on a Host Runtime.
 * 
 * The action may not succeed (i.e. no suitable Host Runtime is found). In such
 * case the task is put into WAITING state or left alone (if serious problem
 * arises).
 * 
 * @author Martin Sixta
 */
final class ScheduleTaskAction implements TaskAction {

	/** default lock timeout value */
	private static final int LOCK_TIMEOUT = 60;

	/** logging */
	private static Logger log = LoggerFactory.getLogger(ScheduleTaskAction.class);

	/** map with tasks */
	final IMap<String, TaskEntry> map;

	/** tasks utility class */
	final Tasks tasks;

	/** connection to the cluster */
	private final ClusterContext ctx;

	/** the task to schedule */
	private TaskEntry entry;

	/**
	 * Creates a new action that schedules tasks
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @param entry
	 *          task to schedule
	 */
	public ScheduleTaskAction(final ClusterContext ctx, final TaskEntry entry) {
		this.ctx = ctx;
		this.entry = entry;
		this.tasks = ctx.getTasks();
		this.map = tasks.getTasksMap();
	}

	@Override
	public void execute() {

		final String id = entry.getId();

		// we are sure that the node is of type DATA (see constructor assertion)
		final String nodeId = ctx.getCluster().getLocalMember().getUuid(); // cluster id of this member

		log.debug("Received new task to schedule {}", id);

		try {

			// 1) Find suitable Host Runtime
			String receiverId = RuntimeSelectors.fromEntry(entry, ctx).select();

			// 2) Lock the entry
			TaskEntry entryCopy = map.tryLockAndGet(id, LOCK_TIMEOUT, SECONDS);

			// check that we are processing unchanged entry
			if (!areEqual(entry, entryCopy)) {
				map.unlock(id);
				return;
			}

			// 3) change the entry

			// Update content of the entry
			TaskEntries.setState(entry, SCHEDULED, "Task scheduled on %s", receiverId);

			entry.setRuntimeId(receiverId);

			// 4) Update entry
			tasks.putTask(entry);

			map.unlock(id);

			// 5) Send a message to the runtime
			ctx.getTopics().publishInGlobalTopic(newRunTaskMessage());

			log.debug("Task {} scheduled on {}", id, receiverId);

		} catch (NoRuntimeFoundException e) {
			String msg = String.format("No runtime found for task %s", entry.getId());
			log.debug(msg);

			stashTask("No suitable host found");
		} catch (TimeoutException e) {
			log.warn("Could not lock task {} in {}. Will try later if needed.", id, LOCK_TIMEOUT);
			// will get to it later
		} finally {
			if (map.isLocked(id)) {
				map.unlock(id);
			}

		}

	}

	/**
	 * Sets the state of the task to WAITING to be rescheduled when appropriate
	 * event happens.
	 */
	private void stashTask(String msg) {
		final String id = entry.getId();

		map.lock(entry.getId());

		try {
			TaskEntry entryCopy = map.get(id);
			if (!areEqual(entry, entryCopy)) {
				return;
			}

			if (entry.getState() != TaskState.WAITING) {
				TaskEntries.setState(entry, TaskState.WAITING, msg);
				tasks.putTask(entry);
			}

		} finally {
			map.unlock(id);
		}
	}

	/**
	 * Auxiliary which creates a message to be send to a selected HostRuntime
	 * 
	 * @return Run task message
	 */
	private RunTaskMessage newRunTaskMessage() {
		String receiverId = entry.getRuntimeId();
		String taskId = entry.getId();
		return new RunTaskMessage(receiverId, taskId);
	}

	/**
	 * Checks equality of two task entries
	 * 
	 * @param entry1
	 *          first entry
	 * @param entry2
	 *          second entry
	 * @return true if entries are equal, false otherwise
	 */
	private boolean areEqual(TaskEntry entry1, TaskEntry entry2) {
		return entry1.equals(entry2);
	}
}
