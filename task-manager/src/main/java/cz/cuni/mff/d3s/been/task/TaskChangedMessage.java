package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * @author Martin Sixta
 */
final class TaskChangedMessage extends AbstractEntryTaskMessage {

	public TaskChangedMessage(TaskEntry entry) {
		super(entry);
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		TaskState state = this.getEntry().getState();

		if (state == TaskState.SUBMITTED || state == TaskState.WAITING)
			return new ScheduleTaskAction(ctx, getEntry());

		if (state == TaskState.FINISHED || state == TaskState.ABORTED) {
			/*
				Check that the Task Context is in our local keyset, so that
				we only delete a context once.
			 */
			TaskEntry taskEntry = getEntry();
			String taskContextId = taskEntry.getTaskContextId();
			if (ctx.getTaskContextsUtils().getTaskContextsMap().localKeySet().contains(taskContextId)) {
				return new TaskContextCheckerAction(ctx, taskEntry);
			}
		}

		return null;
	}
}
