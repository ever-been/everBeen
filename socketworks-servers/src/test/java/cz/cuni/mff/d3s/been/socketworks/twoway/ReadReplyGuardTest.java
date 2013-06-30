package cz.cuni.mff.d3s.been.socketworks.twoway;

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
    private Request request;
    private Reply reply;

    @Before
    public void setUp() throws MessagingException {
        queueName = UUID.randomUUID().toString();
        handlerFactory = new TestHandlerFactory();
        guard = ReadReplyGuard.create("localhost", handlerFactory);
        guard.listen();
        requestor = Requestor.create(guard.getConnection());
        request = new Request();
        reply = new Reply();
    }

    @After
    public void tearDown() throws MessagingException{
        requestor.close();
        guard.terminate();
    }

    @Test(timeout = 2000)
    public void testEmptyShutdown() {
        // just dry run without requesting anything
    }

    @Test(timeout = 2000)
    public void testRequestReply() throws InterruptedException {
        final Reply reply = requestor.send(request);
        assertEquals(this.reply, reply);
    }

    //@Test
    public void testConcurrentRequests() throws InterruptedException {
        requestor.send(request);
        requestor.send(request);
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
            return reply.toJson();
        }

        @Override
        public void markAsRecyclable() {
        }

    }
}
