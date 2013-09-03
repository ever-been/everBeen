package cz.cuni.mff.d3s.been.taskapi;

import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.NamedSockets;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReplyType;
import cz.cuni.mff.d3s.been.socketworks.twoway.RequestException;
import cz.cuni.mff.d3s.been.socketworks.twoway.Requestor;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequestType;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * Sends checkpoint requests of tasks to its Host Runtime.
 * <p/>
 * The CheckpointController provides checkpoint REQ-REP semantics for tasks.
 * Requests are handled by the corresponding Host Runtime. The methods block
 * until the request is handled. The blocking time is unbounded for some
 * requests (use timeout if you don't want to block for arbitrary long time).
 * <p/>
 * Calls are not thread safe. Create a requester for each thread (and inside the
 * thread) which might want to issue request.
 * <p/>
 * <p/>
 * After you are done, {@link #close()} must be called. Otherwise the process
 * will not terminate.
 * 
 * @author Martin Sixta
 */
@NotThreadSafe
public class CheckpointController implements AutoCloseable {

	private final Requestor requestor;
	/**
	 * logging
	 */
	private static final Logger log = LoggerFactory.getLogger(CheckpointController.class);

	/**
	 * Creates a new CheckpointController. Each thread must create its own
	 * CheckpointController, the class is not thread safe. Also the object should
	 * be created in the thread that wants to use it.
	 */
	private CheckpointController(Requestor requestor) {
		this.requestor = requestor;
	}

	/**
	 * Creates default checkpoint controller.
	 * 
	 * @return default checkpoint controller
	 * 
	 * @throws MessagingException
	 *           when connection cannot be established
	 */
	public static CheckpointController create() throws MessagingException {
		return create(NamedSockets.TASK_CHECKPOINT_0MQ.getConnection());
	}

	/**
	 * Creates controller.
	 * 
	 * @param connection
	 *          where to request requests
	 * @return checkpoint controller
	 * @throws MessagingException
	 *           when connection cannot be established
	 */
	public static CheckpointController create(String connection) throws MessagingException {
		final Requestor requestor = Requestor.create(connection);
		return new CheckpointController(requestor);
	}

	/**
	 * Closes the requestor. No further request will be handled by the object.
	 * <p/>
	 * Must be called to release associated resources. Failing to do so will hand
	 * the process on exit.
	 */
	public void close() throws MessagingException {
		requestor.close();
	}

	/**
	 * Sets value of a checkpoint.
	 * 
	 * @param checkPointName
	 *          name of the check point to set
	 * @param value
	 *          value of the check point
	 * @throws RequestException
	 *           when the request fails
	 */
	public void checkPointSet(String checkPointName, String value) throws RequestException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.SET, checkPointName, value);
		Reply reply = send(request);

		// TODO handle error reply better
		if (reply.getReplyType() != ReplyType.OK) {
			throw new RequestException("Address set failed");
		}
	}

	/**
	 * Send a premade {@link CheckpointRequest}
	 * 
	 * @param request
	 *          Request to send
	 * 
	 * @return The reply, or <code>null</code> if anything goes awry
	 */
	public Reply request(CheckpointRequest request) {
		return send(request);
	}

	/**
	 * Retrieves value of a check point.
	 * 
	 * @param name
	 *          name of the check point
	 * @return value of the check point
	 * @throws RequestException
	 *           when the request fails
	 */
	public String checkPointGet(String name) throws RequestException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.GET, name);
		Reply reply = send(request);

		if (reply.getReplyType() != ReplyType.OK) {
			log.error(reply.getValue());
			throw new RequestException("Address set failed");
		}

		return reply.getValue();
	}

	/**
	 * Waits for a check point with timeout. The method will return once the
	 * checkpoint has a value or the request timeouts.
	 * 
	 * @param name Name of the checkpoint
	 * @param timeout
	 *          Timeout in seconds
	 *
	 * @return value of the check point
	 *
	 * @throws RequestException
	 *           when the request fails
	 * @throws TimeoutException
	 *           when the request timeouts
	 */
	public String checkPointWait(String name, long timeout) throws RequestException, TimeoutException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.WAIT, name, timeout);
		Reply reply = send(request);

		if (reply.getReplyType() == ReplyType.ERROR) {
			String value = reply.getValue();
			if (value.equals("TIMEOUT")) {
				throw new TimeoutException(String.format("Wait for %s timed out", name));
			} else {
				throw new RequestException(String.format("Wait for %s failed", name));
			}
		}

		return reply.getValue();
	}

	/**
	 * Waits until a check point is set.
	 * 
	 * @param name
	 *          Name of the check point
	 * @return Value of the checkpoint
	 * @throws RequestException
	 *           when the request fails
	 */
	public String checkPointWait(String name) throws RequestException {
		try {
			return checkPointWait(name, 0);
		} catch (TimeoutException e) {
			// should not time out
			throw new RequestException(e);
		}
	}

	/**
	 * Waits for count down of a Latch with timeout.
	 * 
	 * @param name
	 *          name of the latch
	 * @param timeout
	 *          timeout in seconds
	 * @throws RequestException
	 *           when the request fails
	 * @throws TimeoutException
	 *           when the request timeouts
	 */
	public void latchWait(String name, long timeout) throws RequestException, TimeoutException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.LATCH_WAIT, name, timeout);
		Reply reply = send(request);

		if (reply.getReplyType() == ReplyType.ERROR) {
			String value = reply.getValue();
			if (value.equals("TIMEOUT")) {
				throw new TimeoutException(String.format("Wait for %s count down timed out", name));
			} else {
				throw new RequestException(String.format("Wait for %s count down failed", name));
			}
		}
	}

	/**
	 * Waits for count down of a Latch.
	 * 
	 * @param name
	 *          name of the latch
	 * @throws RequestException
	 *           when the request fails
	 */
	public void latchWait(String name) throws RequestException {
		try {
			latchWait(name, 0);
		} catch (TimeoutException e) {
			// should not time out
			throw new RequestException(e);
		}
	}

	/**
	 * Counts down a Latch.
	 * 
	 * @param name
	 *          name of the latch
	 * @throws RequestException
	 *           when the request fails
	 */
	public void latchCountDown(String name) throws RequestException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.LATCH_DOWN, name, null);
		Reply reply = send(request);

		if (reply.getReplyType() != ReplyType.OK) {
			throw new RequestException(String.format("Count down of %s failed", name));
		}
	}

	/**
	 * Sets value of a latch.
	 * <p/>
	 * The desired value must be set before any attempt to count it down.
	 * <p/>
	 * The count down can be reset but only if the value reaches zero.
	 * 
	 * @param name
	 *          name of the latch
	 * @param count
	 *          desired count
	 * @throws RequestException
	 *           when the request fails
	 */
	public void latchSet(String name, int count) throws RequestException {
		CheckpointRequest request = new CheckpointRequest(CheckpointRequestType.LATCH_SET, name, Integer.toString(count));
		Reply reply = send(request);

		if (reply.getReplyType() != ReplyType.OK) {
			log.error(reply.getValue());
			throw new RequestException("Wait failed");
		}
	}

	/**
	 * Sends an arbitrary request, waits for reply.
	 * <p/>
	 * The call will block until the request is handled by the Host Runtime.
	 * 
	 * @param request
	 *          a request
	 * @return reply for the request
	 */
	private Reply send(CheckpointRequest request) {
		request.fillInTaskAndContextId();
		final String replyString = requestor.request(request.toJson());
		try {
			return Reply.fromJson(replyString);
		} catch (JsonException e) {
			log.error("Failed to deserialize reply {}", replyString, e);
			return null;
		}
	}
}
