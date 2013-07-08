package cz.cuni.mff.d3s.been.task.action;

import static cz.cuni.mff.d3s.been.core.task.TaskState.SCHEDULED;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cz.cuni.mff.d3s.been.core.PropertyReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.Tasks;
import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.task.NoRuntimeFoundException;

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
public final class ScheduleTaskAction implements TaskAction {

	/** name of the lock timeout property */
	public static final String TM_LOCK_TIMEOUT = "been.schedule.lock.timeout";

	/** default lock timeout value */
	private static final int DEFAULT_LOCK_TIMEOUT = 60;

	/** shortcut for Host Runtime topic name */
	private static final String RUNTIME_TOPIC = Context.GLOBAL_TOPIC.getName();

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
	public ScheduleTaskAction(ClusterContext ctx, TaskEntry entry) {
		this.ctx = ctx;
		this.entry = entry;
		this.tasks = ctx.getTasks();
		this.map = tasks.getTasksMap();
	}

	@Override
	public void execute() {

		final String id = entry.getId();
		final String nodeId = ctx.getId(); // cluster id of this member

		log.debug("Received new task to schedule {}", id);

		// TODO check task dependencies

		try {

			// 1) Find suitable Host Runtime
			String receiverId = findHostRuntime();

			// 2) Lock the entry
			TaskEntry entryCopy = map.tryLockAndGet(id, getLockTimeout(), SECONDS);

			// check that we are processing unchanged entry
			if (!areEqual(entry, entryCopy)) {
				map.unlock(id);
				return;
			}

			// 3) change the entry

			// Claim ownership of the node
			entry.setOwnerId(nodeId);

			// Update content of the entry
			TaskEntries.setState(entry, SCHEDULED, "Task scheduled on %s", receiverId);

			entry.setRuntimeId(receiverId);

			// 4) Update entry
			tasks.putTask(entry, 60, SECONDS);

			map.unlock(id);

			// 5) Send a message to the runtime
			ctx.getTopics().publish(RUNTIME_TOPIC, newRunTaskMessage());

			log.info("Task {} scheduled on {}", id, receiverId);

		} catch (NoRuntimeFoundException e) {
			String msg = String.format("No runtime found for task %s", entry.getId());
			log.info(msg);

			stashTask("No suitable host found");
		} catch (TimeoutException e) {
			log.warn("Could not lock task {} in {}. Will try later if needed.", id, getLockTimeout());
			// will get to it later
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
				tasks.putTask(entry, 300, TimeUnit.SECONDS);
			}

		} finally {
			map.unlock(id);
		}
	}

	/**
	 * Tries to find a suitable Host Runtime for the task.
	 * 
	 * @return ID of a Host Runtime to schedule the task on
	 * 
	 * @throws NoRuntimeFoundException
	 *           if no suitable Host Runtime is found
	 */
	private String findHostRuntime() throws NoRuntimeFoundException {

		IRuntimeSelection selection = createSelection(entry.getTaskDescriptor());

		return selection.select(entry);

	}

	/**
	 * Creates appropriate Host Runtime selection method
	 * 
	 * 
	 * @param td
	 *          descriptor of the task
	 * 
	 * @return appropriate implementation of Host Runtime selection
	 */
	private IRuntimeSelection createSelection(final TaskDescriptor td) {
		boolean useXPath = td.isSetHostRuntimes() && td.getHostRuntimes().isSetXpath();

		if (useXPath) {
			return new XPathRuntimeSelection(ctx);
		} else {
			return new RandomRuntimeSelection(ctx);
		}
	}

	/**
	 * Auxiliary which creates a message to be send to a selected HostRuntime
	 * 
	 * @return Run task message
	 */
	private RunTaskMessage newRunTaskMessage() {
		String senderId = entry.getOwnerId();
		String receiverId = entry.getRuntimeId();
		String taskId = entry.getId();
		return new RunTaskMessage(senderId, receiverId, taskId);
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

	private int getLockTimeout() {
		return PropertyReader.system().getInteger(TM_LOCK_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
	}
}
