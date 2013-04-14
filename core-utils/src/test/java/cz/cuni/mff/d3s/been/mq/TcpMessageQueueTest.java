package cz.cuni.mff.d3s.been.mq;

import static cz.cuni.mff.d3s.been.mq.TestParams.HOSTNAME;

/**
 * @author Martin Sixta
 */
public class TcpMessageQueueTest extends BasicQueueTests {

	protected IMessageQueue<String> getQueue() {
		return Messaging.createTcpQueue(HOSTNAME);
	}

}
