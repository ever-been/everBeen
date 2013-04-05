package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * @author Martin Sixta
 */
public class NewOwnerTaskMessage extends AbstractEntryTaskMessage {

	public NewOwnerTaskMessage(TaskEntry entry) {
		super(entry);
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		return new ChangeOwnerTaskAction(ctx, getEntry());
	}
}
