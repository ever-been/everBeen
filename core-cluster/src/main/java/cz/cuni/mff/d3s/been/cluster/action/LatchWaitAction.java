package cz.cuni.mff.d3s.been.cluster.action;

import static com.hazelcast.core.Instance.InstanceType.COUNT_DOWN_LATCH;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.InstanceDestroyedException;
import com.hazelcast.core.MemberLeftException;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.Replays;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class LatchWaitAction implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public LatchWaitAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Replay goGetSome() {
		String latchName = request.getSelector();
		if (!ctx.containsInstance(COUNT_DOWN_LATCH, latchName)) {
			return Replays.createErrorReplay("No such Count Down Latch '%s'", latchName);
		}

		long timeout = request.getTimeout();

		if (timeout < 0) {
			return Replays.createErrorReplay("Timeout must be >= 0, but was %d'", timeout);
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
				return Replays.createOkReplay(Boolean.toString(true));
			} else {
				return Replays.createErrorReplay("TIMEOUT");
			}

		} catch (InstanceDestroyedException | MemberLeftException
				| InterruptedException e) {
			return Replays.createErrorReplay(e.getMessage());
		}

	}
}
