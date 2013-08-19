package cz.cuni.mff.d3s.been.manager.msg;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.manager.action.Actions;
import cz.cuni.mff.d3s.been.manager.action.TaskAction;

/**
 * Message which changes a task owner.
 * 
 * @author Martin Sixta
 */
final class NewOwnerTaskMessage extends AbstractEntryTaskMessage {

	public NewOwnerTaskMessage(TaskEntry entry) {
		super(entry);
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		return Actions.createChangeOwnerTaskAction(ctx, getEntry());
	}
}
