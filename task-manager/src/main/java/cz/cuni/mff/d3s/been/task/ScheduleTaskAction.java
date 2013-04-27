package cz.cuni.mff.d3s.been.task;

import static cz.cuni.mff.d3s.been.core.task.TaskState.SCHEDULED;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeoutException;

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

/**
 * @author Martin Sixta
 */
final class ScheduleTaskAction implements TaskAction {

	public static final String TM_LOCK_TIMEOUT = "been.schedule.timeout";

	private static final int LOCK_TIMEOUT;

	private static Logger log = LoggerFactory.getLogger(ScheduleTaskAction.class);

	// really?
	static {
		Integer timeout = 20;
		String property = System.getProperty(TM_LOCK_TIMEOUT, timeout.toString());

		try {
			timeout = Integer.valueOf(property);
		} catch (NumberFormatException e) {
			log.warn("{} expects a positive integer", TM_LOCK_TIMEOUT);
		}

		LOCK_TIMEOUT = timeout;
	}

	private final ClusterContext ctx;
	private TaskEntry entry;
	private static final String RUNTIME_TOPIC = Context.GLOBAL_TOPIC.getName();

	public ScheduleTaskAction(ClusterContext ctx, TaskEntry entry) {
		this.ctx = ctx;
		this.entry = entry;
	}

	@Override
	public void execute() {

		String taskId = entry.getId();
		final Tasks tasks = ctx.getTasksUtils();
		final IMap<String, TaskEntry> map = tasks.getTasksMap();

		final String nodeId = ctx.getId();

		log.info("Received new task " + taskId);

		String id = entry.getId();

		try {

			// 1) Find suitable Host Runtime
			String receiverId = findHostRuntime(entry);
			TaskEntry entryCopy = map.tryLockAndGet(id, LOCK_TIMEOUT, SECONDS);

			if (!areEqual(entry, entryCopy)) {
				//
				map.unlock(id);
				return;
			}

			// 2) Change task state to SCHEDULED and send message to the Host Runtime

			// Claim ownership of the node
			entry.setOwnerId(nodeId);

			// Update content of the entry
			TaskEntries.setState(entry, SCHEDULED, "Task scheduled on %s", receiverId);

			entry.setRuntimeId(receiverId);

			// Update entry
			tasks.putTask(entry, 60, SECONDS);

			map.unlock(id);

			// Send a message to the runtime
			ctx.getTopicUtils().publish(RUNTIME_TOPIC, newRunTaskMessage(entry));

			log.info("Task {} scheduled on {}", taskId, receiverId);

		} catch (NoRuntimeFoundException e) {
			String msg = String.format("No runtime found for task %s", entry.getId());
			log.warn(msg, e);

			abortTask(msg);
		} catch (TimeoutException e) {
			log.warn("Could not lock task {} in {}. Will try later if needed.", id, LOCK_TIMEOUT);
			// will get to it later
		}

	}

	private void abortTask(String msg) {
		// TODO this needs more careful approach!
		log.warn("Aborting task {}", entry.getId());
		TaskEntries.setState(entry, TaskState.ABORTED, msg);
		ctx.getTasksUtils().putTask(entry);
	}

	private String findHostRuntime(final TaskEntry entry) throws NoRuntimeFoundException {

		IRuntimeSelection selection = createSelection(entry.getTaskDescriptor());

		return selection.select(entry);

	}

	private IRuntimeSelection createSelection(final TaskDescriptor td) {
		if (td.isSetHostRuntimes() && td.getHostRuntimes().isSetXpath()) {
			return new XPathRuntimeSelection(ctx);
		} else {
			return new RandomRuntimeSelection(ctx);
		}
	}

	private RunTaskMessage newRunTaskMessage(TaskEntry taskEntry) {
		String senderId = taskEntry.getOwnerId();
		String receiverId = taskEntry.getRuntimeId();
		String taskId = taskEntry.getId();
		return new RunTaskMessage(senderId, receiverId, taskId);
	}

	private boolean areEqual(TaskEntry entry1, TaskEntry entry2) {
		return entry1.equals(entry2);
	}
}
