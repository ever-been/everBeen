package cz.cuni.mff.d3s.been.cluster.action;

import static com.hazelcast.core.Instance.InstanceType.COUNT_DOWN_LATCH;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.InstanceDestroyedException;
import com.hazelcast.core.MemberLeftException;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;

/**
 * An {@link Action} that handles a request for a waiting until a latch becomes
 * zero.
 * 
 * @author Martin Sixta
 */
final class LatchWaitAction implements Action {

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
	public LatchWaitAction(CheckpointRequest request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply handle() {
		String latchName = Actions.latchNameForRequest(request);

		if (!ctx.containsInstance(COUNT_DOWN_LATCH, latchName)) {
			return Replies.createErrorReply("No such Count Down Latch '%s'", latchName);
		}

		long timeout = request.getTimeout();

		if (timeout < 0) {
			return Replies.createErrorReply("Timeout must be >= 0, but was %d'", timeout);
		}

		final ICountDownLatch countDownLatch = ctx.getCountDownLatch(latchName);
		try {

			boolean waitResult;

			if (request.getTimeout() == 0) {
				// await() will return after hazelcast.max.operation.timeout no matter what
				while (countDownLatch.hasCount()) {
					countDownLatch.await();
				}
				waitResult = true;
			} else {
				waitResult = countDownLatch.await(timeout, MILLISECONDS);
			}

			if (waitResult) {
				return Replies.createOkReply(Boolean.toString(true));
			} else {
				return Replies.createErrorReply("TIMEOUT");
			}

		} catch (InstanceDestroyedException | MemberLeftException | InterruptedException e) {
			return Replies.createErrorReply(e.getMessage());
		}
	}

}
