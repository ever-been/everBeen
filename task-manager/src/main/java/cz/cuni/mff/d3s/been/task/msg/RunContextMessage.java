package cz.cuni.mff.d3s.been.task.msg;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.task.action.Actions;
import cz.cuni.mff.d3s.been.task.action.TaskAction;

/**
 * 
 * Message which handles new task contexts.
 * 
 * @author Martin Sixta
 */
final class RunContextMessage implements TaskMessage {
	private final String contextId;

	public RunContextMessage(String contextId) {

		this.contextId = contextId;
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		return Actions.createRunContextAction(ctx, contextId);
	}
}
