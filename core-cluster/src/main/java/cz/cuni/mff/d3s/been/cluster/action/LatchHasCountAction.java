package cz.cuni.mff.d3s.been.cluster.action;

import static com.hazelcast.core.Instance.InstanceType.COUNT_DOWN_LATCH;

import com.hazelcast.core.ICountDownLatch;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;

/**
 * @author Martin Sixta
 */
final class LatchHasCountAction implements Action {
	private final CheckpointRequest request;
	private final ClusterContext ctx;

	public LatchHasCountAction(CheckpointRequest request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply handle() {
		String latchName = Actions.latchNameForRequest(request);
		if (!ctx.containsInstance(COUNT_DOWN_LATCH, latchName)) {
			return Replies.createErrorReply("No such Count Down Latch '%s'", latchName);
		}

		final ICountDownLatch countDownLatch = ctx.getCountDownLatch(latchName);
		return Replies.createOkReply(Boolean.toString(countDownLatch.hasCount()));
	}
}
