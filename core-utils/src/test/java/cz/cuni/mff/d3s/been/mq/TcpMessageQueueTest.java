package cz.cuni.mff.d3s.been.mq;

import static cz.cuni.mff.d3s.been.mq.TestParams.HOSTNAME;

import org.junit.Test;

/**
 * @author Martin Sixta
 */
public class TcpMessageQueueTest extends BasicQueueTests {

	protected IMessageQueue<String> getQueue() {
		return Messaging.createTcpQueue(HOSTNAME);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadHostname() throws MessagingException {
		IMessageQueue<String> queue = new TcpMessageQueue("!@#$%^^&");
		IMessageReceiver<String> receiver = queue.getReceiver();

	}

}
