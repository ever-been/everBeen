package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.core.utils.JsonException;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link ReadReplyGuard}
 */
public class ReadReplyGuardTest {

    private String queueName;
    private ReadReplyHandlerFactory handlerFactory;
    private ReadReplyGuard guard;
    private Requestor requestor;
    private String request;
    private String reply;

    @Before
    public void setUp() throws MessagingException {
        queueName = UUID.randomUUID().toString();
        handlerFactory = new TestHandlerFactory();
        guard = ReadReplyGuard.create("localhost", handlerFactory);
        guard.listen();
        requestor = Requestor.create(guard.getConnection());
        request = "request";
        reply = "reply";
    }

    @After
    public void tearDown() throws MessagingException {
        requestor.close();
        guard.terminate();
    }

    @Test(timeout = 2000)
    public void testEmptyShutdown() {
        // just dry run without requesting anything
    }

    @Test(timeout = 2000)
    public void testRequestReply() throws InterruptedException, JsonException {
        final String reply = requestor.request(request);
        assertEquals(this.reply, reply);
    }

    //@Test
    public void testConcurrentRequests() throws InterruptedException {
        requestor.request(request);
        requestor.request(request);
    }



    // DUMMY REQUEST HANDLER IMPLEMENTATIONS

    class TestHandlerFactory implements ReadReplyHandlerFactory {
        @Override
        public ReadReplyHandler getHandler() {
            return new TestHandler();
        }
    }

    class TestHandler implements ReadReplyHandler {
        @Override
        public String handle(String message) throws SocketHandlerException, InterruptedException {
            return reply;
        }

        @Override
        public void markAsRecyclable() {
        }

    }
}
