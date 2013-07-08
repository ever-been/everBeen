package cz.cuni.mff.d3s.been.socketworks.oneway;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;

public class ReadOnlyGuardTest {

	private ReadOnlyGuard guard;
	private IMessageSender<String> sender;
	private String message;
	private String queueName;
	private TestHandler handler;
    private Object lock;

	@Before
	public void setUpGuard() throws MessagingException {
		initMocks(this);
        lock = new Object();
        queueName = UUID.randomUUID().toString();
        message = UUID.randomUUID().toString();
        handler = new TestHandler(message, lock);
        guard = ReadOnlyGuard.create("localhost", queueName, handler);
        guard.listen();
        sender = MessageQueues.getInstance().createSender(queueName);
    }

	@After
	public void tearDownGuard() throws MessagingException {
		sender.close();
		guard.terminate();
	}

	@Test(timeout = 1000)
	public void testTerminateEmpty() throws MessagingException {
		// just dry-run the @Before and @After methods
	}

	@Test(timeout = 1000)
	public void testReceive() throws MessagingException, SocketHandlerException, InterruptedException {
        sendAndWait();
        assertEquals(1, handler.getCount());
	}

	@Test(timeout = 1000)
	public void testReopenQueueWithSameName() throws MessagingException, InterruptedException, SocketHandlerException {
		sendAndWait();

		sender.close();
		guard.terminate();

		guard = ReadOnlyGuard.create("localhost", queueName, handler);
		guard.listen();
		sender = MessageQueues.getInstance().createSender(queueName);

        sendAndWait();

		assertEquals(2, handler.getCount());
	}

    /**
     * Send a message and wait on {@link #lock} until {@link #handler} wakes us up
     *
     * @throws InterruptedException If the {@link Object#wait()} on {@link #lock} gets interrupted
     * @throws MessagingException
     */
    private void sendAndWait() throws InterruptedException,MessagingException {
        synchronized (lock) {
           sender.send(message);
           lock.wait();
        }
    }

    class TestHandler implements ReadOnlyHandler {

        private final String message;
        private final Object lock;
        private int count;

        TestHandler(String message, Object lock) {
            this.message = message;
            this.lock = lock;
            this.count = 0;
        }

        @Override
        public void handle(String message) throws SocketHandlerException {
            assertEquals(this.message, message);
            ++count;
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        int getCount() {
            return count;
        }
    }
}
