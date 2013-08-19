package cz.cuni.mff.d3s.been.task.msg;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.task.action.Actions;
import cz.cuni.mff.d3s.been.task.action.TaskAction;

/**
 * Message which drives abortion of a task.
 * 
 * @author Martin Sixta
 */
final class AbortTaskMessage extends AbstractEntryTaskMessage {
	private final String msg;
	public AbortTaskMessage(TaskEntry entry, String reasonFormat, Object... args) {
		super(entry);
		msg = String.format(reasonFormat, args);
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		return Actions.createAbortAction(ctx, getEntry(), msg);
	}
}
