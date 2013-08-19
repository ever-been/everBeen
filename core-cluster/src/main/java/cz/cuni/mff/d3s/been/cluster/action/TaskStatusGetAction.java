package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.socketworks.twoway.Request;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;

/**
 * An {@link Action} that handles a request for retrieving the status of a task.
 * 
 * @author Martin Sixta
 */
final class TaskStatusGetAction implements Action {

	/** the request to handle */
	private final Request request;

	/** BEEN cluster instance */
	private final ClusterContext ctx;

	/**
	 * Default constructor, creates the action with the specified request and
	 * cluster context.
	 * 
	 * @param request
	 *          the request to handle
	 * @param ctx
	 *          the cluster context
	 */
	public TaskStatusGetAction(CheckpointRequest request, ClusterContext ctx) {
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
