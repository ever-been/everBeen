package cz.cuni.mff.d3s.been.task.msg;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.task.action.TaskAction;

/**
 * @author Martin Sixta
 */
public class PoisonMessage implements TaskMessage {
	@Override
	public TaskAction createAction(ClusterContext ctx) {
		throw new UnsupportedOperationException("Poison message does not execute actions!");
	}
}
