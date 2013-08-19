package cz.cuni.mff.d3s.been.task.msg;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.task.action.TaskAction;

/**
 * Interface for driving task and context lifecycle
 * 
 * @author Martin Sixta
 */

public interface TaskMessage extends Serializable {
	/**
	 * Creates an appropriate {@link TaskAction} which handles the message.
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @return action to take, or null (nothing will happen)
	 */
	public TaskAction createAction(ClusterContext ctx);
}
