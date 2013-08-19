package cz.cuni.mff.d3s.been.task.msg;

import static cz.cuni.mff.d3s.been.core.task.TaskState.ABORTED;
import static cz.cuni.mff.d3s.been.core.task.TaskState.FINISHED;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.task.action.Actions;
import cz.cuni.mff.d3s.been.task.action.TaskAction;
import cz.cuni.mff.d3s.been.task.selector.NoRuntimeFoundException;
import cz.cuni.mff.d3s.been.task.selector.RuntimeSelectors;

/**
 * Message which checks scheduability of a task.
 * 
 * If a task can be scheduled an appropriate action should take place.
 * 
 * @author Martin Sixta
 */
public class CheckSchedulabilityMessage implements TaskMessage {
	private final TaskEntry entry;

	public CheckSchedulabilityMessage(TaskEntry entry) {
		this.entry = entry;
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {

		if (isWaitingOnTask(ctx)) {
			return Actions.createNullAction();
		}

		try {
			RuntimeSelectors.fromEntry(entry, ctx).select();
			return Actions.createScheduleTaskAction(ctx, entry);
		} catch (NoRuntimeFoundException e) {
			// do nothing, will have to wait
		}

		return Actions.createNullAction();
	}

	/**
	 * Checks whether the task is waiting on another task.
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @return true if the task is waiting on another task, false otherwise
	 */
	private boolean isWaitingOnTask(final ClusterContext ctx) {
		final String taskDependency = entry.getTaskDependency();

		if (taskDependency == null || taskDependency.isEmpty()) {
			return false;
		}

		final TaskEntry task = ctx.getTasks().getTask(taskDependency);

		if (task == null) {
			return false;
		} else {
			return !isTaskDone();
		}

	}

	/**
	 * Checks whether the task is done executing
	 * 
	 * @return whether the task is in ABORTED or FINISHED state
	 */
	private boolean isTaskDone() {
		final TaskState state = entry.getState();
		return (state == ABORTED) || (state == FINISHED);
	}
}
