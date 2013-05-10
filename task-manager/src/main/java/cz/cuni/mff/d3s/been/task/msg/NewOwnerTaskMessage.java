package cz.cuni.mff.d3s.been.task.msg;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.task.action.ChangeOwnerTaskAction;
import cz.cuni.mff.d3s.been.task.action.TaskAction;

/**
 * @author Martin Sixta
 */
final class NewOwnerTaskMessage extends AbstractEntryTaskMessage {

	public NewOwnerTaskMessage(TaskEntry entry) {
		super(entry);
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		return new ChangeOwnerTaskAction(ctx, getEntry());
	}
}
