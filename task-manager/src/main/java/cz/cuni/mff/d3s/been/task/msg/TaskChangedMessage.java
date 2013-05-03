package cz.cuni.mff.d3s.been.task.msg;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.task.action.ScheduleTaskAction;
import cz.cuni.mff.d3s.been.task.action.TaskAction;
import cz.cuni.mff.d3s.been.task.action.TaskContextCheckerAction;

/**
 * @author Martin Sixta
 */
public final class TaskChangedMessage extends AbstractEntryTaskMessage {

	public TaskChangedMessage(TaskEntry entry) {
		super(entry);
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		TaskState state = this.getEntry().getState();

		if (state == TaskState.SUBMITTED || state == TaskState.WAITING)
			return new ScheduleTaskAction(ctx, getEntry());

		if (state == TaskState.FINISHED || state == TaskState.ABORTED) {

			return new TaskContextCheckerAction(ctx, getEntry());

		}

		return null;
	}
}
