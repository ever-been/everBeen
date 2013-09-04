package cz.cuni.mff.d3s.been.manager.msg;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.manager.action.Actions;
import cz.cuni.mff.d3s.been.manager.action.TaskAction;

/**
 * Message which handles task scheduling.
 * 
 * @author Martin Sixta
 */
final class ScheduleTaskMessage extends AbstractEntryTaskMessage {

	/**
	 * Creates ScheduleTaskMessage
	 * 
	 * @param entry
	 *          targeted task entry
	 */
	public ScheduleTaskMessage(TaskEntry entry) {
		super(entry);
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		TaskState state = this.getEntry().getState();
		if (state == TaskState.SUBMITTED || state == TaskState.ACCEPTED || state == TaskState.WAITING || state == TaskState.SCHEDULED) {
			return Actions.createScheduleTaskAction(ctx, getEntry());
		} else {
			return Actions.createNullAction();
		}
	}
}
