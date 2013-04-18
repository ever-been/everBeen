package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.Replays;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class TaskStatusGetAction implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public TaskStatusGetAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Replay goGetSome() {
		String taskId = request.getSelector();

		TaskEntry taskEntry = ctx.getTasksUtils().getTask(taskId);

		if (taskEntry == null) {
			return Replays.createErrorReplay("No such task '%s'", taskId);
		} else {
			return Replays.createOkReplay("%s", taskEntry.getState().toString());
		}
	}
}
