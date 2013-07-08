package cz.cuni.mff.d3s.been.mq;

import static cz.cuni.mff.d3s.been.mq.TestParams.HOSTNAME;

import org.junit.Test;

/**
 * Simulates and tests Task-to-HR message queue.
 * 
 * @author Martin Sixta
 */
public class TaskMessageQueueTest extends BasicQueueTests {

	/**
	 * Simulates Task-to-HR message queue.
	 */
	private static class TestTaskQueue implements IMessageQueue<String> {
		/** Simulates Host Runtime end of the message queue. */
		private final IMessageQueue<String> hrTestQueue;

		/** Simulates Task end of the message queue */
		private final IMessageQueue<String> taskMessageQueue;

		/** Creates simulated Task-to-HR message queue */
		TestTaskQueue() throws MessagingException {
			hrTestQueue = Messaging.createTcpQueue(HOSTNAME);
			taskMessageQueue = Messaging.createTaskQueue(HOSTNAME,hrTestQueue.getReceiver().getPort());
		}

		@Override
		public IMessageReceiver<String> getReceiver() throws MessagingException {
			return hrTestQueue.getReceiver();
		}

		@Override
		public IMessageSender<String> createSender() throws MessagingException {
			return taskMessageQueue.createSender();
		}

		@Override
		public void terminate() throws MessagingException {
			hrTestQueue.terminate();
			taskMessageQueue.terminate();
		}
	}

	@Override
	protected IMessageQueue<String> getQueue() throws MessagingException {
		return new TestTaskQueue();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testname() throws Exception {
		Messaging.createTaskQueue(HOSTNAME,1234).getReceiver();
	}
}
