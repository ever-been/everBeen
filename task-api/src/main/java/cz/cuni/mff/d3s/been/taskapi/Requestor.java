package cz.cuni.mff.d3s.been.taskapi;

import java.util.concurrent.TimeoutException;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.mq.Context;
import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.ReplayType;
import cz.cuni.mff.d3s.been.mq.rep.Replays;
import cz.cuni.mff.d3s.been.mq.req.Request;
import cz.cuni.mff.d3s.been.mq.req.RequestType;

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
	private static Logger log = LoggerFactory.getLogger(Requestor.class);

	/** Address of the Host Runtime request handler. */
	private static String address = String.format("tcp://localhost:%s", System.getenv("REQUEST_PORT"));

	/** The socket used to communicate with a Host Runtime. */
	private ZMQ.Socket socket;

	/**
	 * Creates a new Requestor. Each thread must create its own Requestor, the
	 * class is not thread safe. Also the object should be created in the thread
	 * that wants to use it.
	 */
	public Requestor() {
		ZMQ.Context ctx = Context.getReference();
		this.socket = ctx.socket(ZMQ.REQ);
		socket.setLinger(0);
		socket.connect(address);
	}

	/**
	 * Sends an arbitrary request, waits for replay.
	 * 
	 * The call will block until the request is handled by the Host Runtime.
	 * 
	 * @param request
	 *          a request
	 * @return replay for the request
	 */
	public Replay send(Request request) {
		String json = request.toJson();

		socket.send(json);

		String replayString = socket.recvStr();

		try {
			return Replay.fromJson(replayString);
		} catch (JSONUtils.JSONSerializerException e) {
			return Replays.createErrorReplay("Cannot deserialize '%s'", json);
		}
	}

	/**
	 * Closes the requestor. No further request will be handled by the object.
	 * 
	 * Must be called to release associated resources. Failing to do so will hand
	 * the process on exit.
	 */
	public void close() {
		socket.close();
		Context.releaseContext();
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
		//TODO the selector must be context specific
		Request request = new Request(RequestType.SET, "cp#" + checkPointName, value);
		Replay replay = send(request);

		// TODO handle error replays better
		if (replay.getReplayType() != ReplayType.OK) {
			throw new RuntimeException("Address set failed");
		}
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
		Request request = new Request(RequestType.GET, "cp#" + name);
		Replay replay = send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Address set failed");
		}

		return replay.getValue();
	}

	/**
	 * Waits for a check point with timeout. The method will return once the
	 * checkpoint has a value or the request timeouts.
	 * 
	 * 
	 * @param name
	 * @param timeout
	 *          timeout in seconds
	 * @return value of the check point
	 * @throws RequestException
	 *           when the request fails
	 * @throws TimeoutException
	 *           when the request timeouts
	 */
	public String checkPointWait(String name, long timeout) throws RequestException, TimeoutException {
		Request request = new Request(RequestType.WAIT, "cp#" + name, timeout);
		Replay replay = send(request);

		if (replay.getReplayType() == ReplayType.ERROR) {
			String value = replay.getValue();
			if (value.equals("TIMEOUT")) {
				throw new TimeoutException(String.format("Wait for %s timed out", name));
			} else {
				throw new RuntimeException(String.format("Wait for %s failed", name));
			}
		}

		return replay.getValue();
	}

	/**
	 * 
	 * Waits until a check point is set.
	 * 
	 * @param name
	 *          name of the check point
	 * @return
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
		Request request = new Request(RequestType.LATCH_WAIT, name, timeout);
		Replay replay = send(request);

		if (replay.getReplayType() == ReplayType.ERROR) {
			String value = replay.getValue();
			if (value.equals("TIMEOUT")) {
				throw new TimeoutException(String.format("Wait for %s count down timed out", name));
			} else {
				throw new RuntimeException(String.format("Wait for %s count down failed", name));
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
		Request request = new Request(RequestType.LATCH_DOWN, name, null);
		Replay replay = send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			throw new RuntimeException(String.format("Count down of %s failed", name));
		}
	}

	/**
	 * 
	 * Sets value of a latch.
	 * 
	 * The desired value must be set before any attempt to count it down.
	 * 
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
		Request request = new Request(RequestType.LATCH_SET, name, Integer.toString(count));
		Replay replay = send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RequestException("Wait failed");
		}
	}
}
