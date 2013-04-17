package cz.cuni.mff.d3s.been.cluster.action;

import static com.hazelcast.core.Instance.InstanceType.COUNT_DOWN_LATCH;

import com.hazelcast.core.ICountDownLatch;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.Replays;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class LatchSetAction implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public LatchSetAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Replay goGetSome() {
		String latchName = request.getSelector();
		if (!ctx.containsInstance(COUNT_DOWN_LATCH, latchName)) {
			//return Replays.createErrorReplay("No such Count Down Latch '%s'", latchName);
		}

		int count;
		try {
			count = Integer.valueOf(request.getValue());
		} catch (NumberFormatException e) {
			return Replays.createErrorReplay("Cannot convert to int: %s", request.getValue());
		}

		final ICountDownLatch countDownLatch = ctx.getCountDownLatch(latchName);
		boolean isCountSet = countDownLatch.setCount(count);

		if (isCountSet) {
			return Replays.createOkReplay("");
		} else {
			return Replays.createErrorReplay(Boolean.FALSE.toString());
		}
	}
}
