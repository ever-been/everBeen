package cz.cuni.mff.d3s.been.mq;

import java.util.UUID;

import org.junit.Test;

/**
 * @author Martin Sixta
 */
public class TcpMessageQueueTest extends BasicQueueTests {

	@Override
	protected IMessageQueue<String> getQueue() throws MessagingException {
		return MessageQueues.getInstance().createTcpQueue(UUID.randomUUID().toString(), "localhost");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadHostname() throws MessagingException {
		IMessageQueue<String> queue = new TcpMessageQueue("!@#$%^^&");
		IMessageReceiver<String> receiver = queue.getReceiver();
	}

}
