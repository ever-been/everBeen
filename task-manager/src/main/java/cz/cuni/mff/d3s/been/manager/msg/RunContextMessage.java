package cz.cuni.mff.d3s.been.manager.msg;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.manager.action.Actions;
import cz.cuni.mff.d3s.been.manager.action.TaskAction;

/**
 * 
 * Message which handles new task contexts.
 * 
 * @author Martin Sixta
 */
final class RunContextMessage implements TaskMessage {
	private final String contextId;

	/**
	 * Creates RunContextMessage.
	 * 
	 * @param contextId
	 *          targeted context ID
	 */
	public RunContextMessage(String contextId) {

		this.contextId = contextId;
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		return Actions.createRunContextAction(ctx, contextId);
	}
}
