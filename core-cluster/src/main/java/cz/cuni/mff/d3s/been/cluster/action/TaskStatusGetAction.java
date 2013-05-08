package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
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
	public Reply handle() {
		String taskId = request.getSelector();

		TaskEntry taskEntry = ctx.getTasks().getTask(taskId);

		if (taskEntry == null) {
			return Replies.createErrorReply("No such task '%s'", taskId);
		} else {
			return Replies.createOkReply("%s", taskEntry.getState().toString());
		}
	}
}
