package cz.cuni.mff.d3s.been.socketworks.oneway;

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

public class ReadOnlyGuardTest extends Assert {

	private ReadOnlyGuard guard;
	private IMessageSender<String> sender;
	private String message;
	private String queueName;

	@Mock
	private ReadOnlyHandler handler;

	@Before
	public void setUpGuard() throws MessagingException {
		initMocks(this);
		queueName = UUID.randomUUID().toString();
		guard = ReadOnlyGuard.create("localhost", queueName, handler);
		guard.listen();
		sender = MessageQueues.getInstance().createSender(queueName);
		message = UUID.randomUUID().toString();
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
	public void testRecieve() throws MessagingException, SocketHandlerException {
		sender.send(message);
		verify(handler).handle(eq(message));
	}

	@Test(timeout = 1000)
	public void testTerminateInterruptedBeforeMessage() throws MessagingException {
		guard.listener.interrupt();
		sender.send(message);
	}

	@Test(timeout = 1000)
	public void testReopenQueueWithSameName() throws MessagingException, InterruptedException, SocketHandlerException {
		sender.send(message);

		sender.close();
		guard.terminate();

		guard = ReadOnlyGuard.create("localhost", queueName, handler);
		guard.listen();
		sender = MessageQueues.getInstance().createSender(queueName);

		sender.send(message);

		guard.listener.join();
		verify(handler, times(2)).handle(eq(message));
	}
}
