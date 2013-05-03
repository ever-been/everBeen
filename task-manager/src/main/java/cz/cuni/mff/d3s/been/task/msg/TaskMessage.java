package cz.cuni.mff.d3s.been.task.msg;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.task.action.TaskAction;

/**
 * @author Martin Sixta
 */

public interface TaskMessage extends Serializable {
	public TaskAction createAction(ClusterContext ctx);
}
