package cz.cuni.mff.d3s.been.cluster.action;

import static com.hazelcast.core.Instance.InstanceType.COUNT_DOWN_LATCH;

import com.hazelcast.core.ICountDownLatch;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class LatchHasCountAction implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public LatchHasCountAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply goGetSome() {
		String latchName = request.getSelector();
		if (!ctx.containsInstance(COUNT_DOWN_LATCH, latchName)) {
			return Replies.createErrorReply("No such Count Down Latch '%s'", latchName);
		}

		final ICountDownLatch countDownLatch = ctx.getCountDownLatch(latchName);
		return Replies.createOkReply(Boolean.toString(countDownLatch.hasCount()));
	}
}
