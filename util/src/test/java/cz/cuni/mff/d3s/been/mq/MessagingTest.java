package cz.cuni.mff.d3s.been.mq;

import org.junit.Assert;
import org.junit.Test;

public class MessagingTest extends Assert {

    private static final String HOSTNAME = "localhost";

	@Test
	public void testExpected() throws Exception {
		assertEquals(InprocMessageQueue.class, Messaging.createInprocQueue(null).getClass());
		assertEquals(TaskMessageQueue.class, Messaging.createTaskQueue(HOSTNAME,1234).getClass());
		assertEquals(TcpMessageQueue.class, Messaging.createTcpQueue(null).getClass());
	}

}
