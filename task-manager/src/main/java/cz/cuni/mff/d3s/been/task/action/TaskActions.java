package cz.cuni.mff.d3s.been.task.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.task.TaskManagerException;
import cz.cuni.mff.d3s.been.task.message.NewTaskMessage;
import cz.cuni.mff.d3s.been.task.message.TaskMessage;
import cz.cuni.mff.d3s.been.task.message.UpdatedTaskMessage;

/**
 * @author Martin Sixta
 */
public class TaskActions {
	public static TaskAction createAction(ClusterContext ctx, TaskMessage msg) throws TaskManagerException {
		if (msg instanceof NewTaskMessage) {
			return new NewTaskAction(ctx, msg);
		} else if (msg instanceof UpdatedTaskMessage) {
			return new UpdatedTaskAction(ctx, msg);
		}

		String errorMsg = String.format("Unknown action of type '%s'", msg.getClass());
		throw new TaskManagerException(errorMsg);

	}
}
