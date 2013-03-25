package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * @author Martin Sixta
 */
public class TaskActions {
	public static TaskAction createAction(ClusterContext ctx, TaskMessage msg) throws TaskManagerException {
		if (msg instanceof NewTaskMessage) {
			return new NewTaskAction(ctx, msg);
		}

		String errorMsg = String.format("Unknown action of type '%s'", msg.getClass());
		throw new TaskManagerException(errorMsg);

	}
}
