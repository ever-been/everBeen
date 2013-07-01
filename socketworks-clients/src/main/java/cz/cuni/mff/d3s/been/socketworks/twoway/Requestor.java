package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;
import cz.cuni.mff.d3s.been.core.TaskPropertyNames;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.mq.Context;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.mq.ZMQContext;
import cz.cuni.mff.d3s.been.socketworks.Socketworks;
import org.jeromq.ZMQ;
import org.jeromq.ZMQ.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends requests of tasks to its Host Runtime.
 * 
 * The Requestor provides REQ-REP semantics for tasks. Requests are handled by
 * the corresponding Host Runtime. The methods block until the request is
 * handled. The blocking time is unbounded for some requests (use timeout if you
 * don't want to block for arbitrary long time).
 * 
 * Calls are not thread safe. Create a requester for each thread (and inside the
 * thread) which might want to issue request.
 * 
 * 
 * After you are done, {@link #close()} must be called. Otherwise the process
 * will not terminate.
 * 
 * 
 * @author Martin Sixta
 */
@NotThreadSafe
public class Requestor {
	/** logging */
	private static final Logger log = LoggerFactory.getLogger(Requestor.class);

	/** Address of the Host Runtime request handler. */
	private final String address;
	/** 0MQ context of this requestor */
	private final ZMQContext zctx;
	/** The socket used to communicate with a Host Runtime. */
	private final Socket socket;

	/**
	 * Creates a new Requestor. Each thread must create its own Requestor, the
	 * class is not thread safe. Also the object should be created in the thread
	 * that wants to use it.
	 */
	private Requestor(String address, ZMQContext zctx, Socket socket) {
        this.address = address;
		this.zctx = zctx;
		this.socket = socket;
		socket.setLinger(0);
		socket.connect(address);
	}

    public static Requestor create(String address) throws MessagingException {
        final ZMQContext zctx = Context.getReference();
        return new Requestor(address, zctx, zctx.socket(ZMQ.REQ));
    }

	/**
	 * Sends an arbitrary request, waits for reply.
	 *
	 * The call will block until the request is handled by the Host Runtime.
	 *
	 * @param request
	 *          a request
	 * @return reply for the request
	 */
	public Reply send(Request request) {
		String json = request.toJson();

		socket.send(json);
        log.debug("Sent {}", json);

		final String replyString = socket.recvStr();
        log.debug("Received {}", replyString);

		try {
			return Reply.fromJson(replyString);
		} catch (JSONUtils.JSONSerializerException e) {
			return Replies.createErrorReply("Cannot deserialize '%s'", json);
		}
	}

	/**
	 * Closes the requestor. No further request will be handled by the object.
	 *
	 * Must be called to release associated resources. Failing to do so will hand
	 * the process on exit.
	 */
	public void close() throws MessagingException {
		socket.close();
		zctx.term();
	}
}
