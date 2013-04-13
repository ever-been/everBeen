package cz.cuni.mff.d3s.been.mq;

import static cz.cuni.mff.d3s.been.mq.TestParams.INPROC_QUEUE_NAME;

/**
 * @author Martin Sixta
 */
public class InprocMessageQueueTest extends BasicQueueTests {
	@Override
	protected IMessageQueue<String> getQueue() {
		return new InprocMessageQueue<String>(INPROC_QUEUE_NAME);
	}
}
