package cz.cuni.mff.d3s.been.task;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * @author Martin Sixta
 */

interface TaskMessage extends Serializable {
	public TaskAction createAction(ClusterContext ctx);
}
