package cz.cuni.mff.d3s.been.cluster.action;

import com.hazelcast.core.ICountDownLatch;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;

/**
 * An {@link Action} that handles a request for a setting a new value to a
 * latch.
 * 
 * @author Martin Sixta
 */
final class LatchSetAction implements Action {

	/** the request to handle */
	private final CheckpointRequest request;

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
	public LatchSetAction(CheckpointRequest request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply handle() {
		String latchName = Actions.latchNameForRequest(request);

		int count;
		try {
			count = Integer.valueOf(request.getValue());
		} catch (NumberFormatException e) {
			return Replies.createErrorReply("Cannot convert to int: %s", request.getValue());
		}

		final ICountDownLatch countDownLatch = ctx.getCountDownLatch(latchName);
		boolean isCountSet = countDownLatch.setCount(count);

		if (isCountSet) {
			return Replies.createOkReply(Boolean.TRUE.toString());
		} else {
			return Replies.createErrorReply(Boolean.FALSE.toString());
		}
	}

}
