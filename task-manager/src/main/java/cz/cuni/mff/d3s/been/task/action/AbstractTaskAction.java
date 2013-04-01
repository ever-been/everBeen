package cz.cuni.mff.d3s.been.task.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.task.message.TaskMessage;

/**
 * @author Martin Sixta
 */
abstract class AbstractTaskAction implements TaskAction {
	protected final ClusterContext clusterCtx;
	protected final TaskMessage msg;

	public AbstractTaskAction(ClusterContext ctx, TaskMessage msg) {
		this.clusterCtx = ctx;
		this.msg = msg;
	}
}
