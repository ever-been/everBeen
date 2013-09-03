package cz.cuni.mff.d3s.been.manager.msg;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.manager.action.Actions;
import cz.cuni.mff.d3s.been.manager.action.TaskAction;

/**
 * Messages which handles new tasks.
 * 
 * @author Martin Sixta
 */
final class NewTaskMessage extends AbstractEntryTaskMessage {

	/**
	 * Creates NewTaskMessage.
	 * 
	 * @param entry
	 *          targeted entry
	 */
	public NewTaskMessage(TaskEntry entry) {
		super(entry);
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		TaskState state = this.getEntry().getState();
		if (state == TaskState.SUBMITTED) {
			return Actions.createScheduleTaskAction(ctx, getEntry());
		} else {
			return Actions.createNullAction();
		}
	}
}
