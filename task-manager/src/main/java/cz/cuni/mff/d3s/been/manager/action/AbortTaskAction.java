package cz.cuni.mff.d3s.been.manager.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.Tasks;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * @author Martin Sixta
 */
final class AbortTaskAction implements TaskAction {
	/** logging */
	private static Logger log = LoggerFactory.getLogger(AbortTaskAction.class);

	/** map with tasks */
	final IMap<String, TaskEntry> map;

	/** tasks utility class */
	final Tasks tasks;

	/** the task to schedule */
	private TaskEntry entry;
	private final String msg;

	/**
	 * Creates a new action that schedules tasks
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @param entry
	 *          the entry to take action on
	 * @param msg
	 *          why the task was aborted
	 */
	public AbortTaskAction(final ClusterContext ctx, final TaskEntry entry, final String msg) {
		this.entry = entry;
		this.msg = msg;
		this.tasks = ctx.getTasks();
		this.map = tasks.getTasksMap();
	}
	@Override
	public void execute() throws TaskActionException {
		final String id = entry.getId();

		log.info("Will abort task: {}, reason: {}", id, msg);
		map.lock(id);

		try {
			TaskEntry currentValue = tasks.getTask(id);
			TaskState currentState = currentValue.getState();
			if (currentState != TaskState.ABORTED && currentState != TaskState.FINISHED) {
				TaskEntries.setState(entry, TaskState.ABORTED, msg);
				tasks.putTask(entry);
			}
		} finally {
			map.unlock(id);
		}

	}
}
