package cz.cuni.mff.d3s.been.task.msg;

import static cz.cuni.mff.d3s.been.core.task.TaskState.ABORTED;
import static cz.cuni.mff.d3s.been.core.task.TaskState.FINISHED;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.task.action.Actions;
import cz.cuni.mff.d3s.been.task.action.ScheduleTaskAction;
import cz.cuni.mff.d3s.been.task.action.TaskAction;
import cz.cuni.mff.d3s.been.task.selector.NoRuntimeFoundException;
import cz.cuni.mff.d3s.been.task.selector.RuntimeSelectors;

/**
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
			return new ScheduleTaskAction(ctx, entry);
		} catch (NoRuntimeFoundException e) {
			// do nothing, will have to wait
		}

		return Actions.createNullAction();
	}

	private boolean isWaitingOnTask(final ClusterContext ctx) {
		final String taskDependency = entry.getTaskDependency();

		if (taskDependency == null || taskDependency.isEmpty()) {
			return false;
		}

		final TaskEntry task = ctx.getTasks().getTask(taskDependency);

		if (task == null) {
			return false;
		} else {
			return !isTaskDone(entry);
		}

	}

	private boolean isTaskDone(final TaskEntry entry) {
		final TaskState state = entry.getState();
		return (state == ABORTED) || (state == FINISHED);
	}
}
