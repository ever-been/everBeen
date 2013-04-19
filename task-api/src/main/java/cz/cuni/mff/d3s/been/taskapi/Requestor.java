package cz.cuni.mff.d3s.been.taskapi;

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
 * @author Martin Sixta
 */
@NotThreadSafe
public class Requestor {
	private static Logger log = LoggerFactory.getLogger(Requestor.class);
	private static String address = String.format("tcp://localhost:%s", System.getenv("REQUEST_PORT"));

	ZMQ.Socket socket;

	public Requestor() {
		ZMQ.Context ctx = Context.getReference();
		this.socket = ctx.socket(ZMQ.REQ);
		socket.setLinger(0);
		socket.connect(address);
	}

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

	public void close() {
		socket.close();
		Context.releaseContext();
	}

	public void checkPointSet(String key, String value) {
		Request request = new Request(RequestType.SET, "cp#" + key, value);
		Replay replay = send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Address set failed");
		}
	}

	public String checkPointGet(String key, String value) {
		Request request = new Request(RequestType.GET, "cp#" + key, value);
		Replay replay = send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Address set failed");
		}

		return replay.getValue();
	}

	public String checkPointWait(String key, long timeout) {
		Request request = new Request(RequestType.WAIT, "cp#" + key, timeout);
		Replay replay = send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Wait failed");
		}

		return replay.getValue();
	}

	public String checkPointWait(String key) {
		return checkPointWait(key, 0);
	}

	public void latchWait(String name, long timeout) {
		Request request = new Request(RequestType.LATCH_WAIT, name, timeout);
		Replay replay = send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Wait for count down failed");
		}
	}

	public void latchWait(String name) {
		latchWait(name, 0);
	}

	public void latchCountDown(String name) {
		Request request = new Request(RequestType.LATCH_DOWN, name, null);
		Replay replay = send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Wait failed");
		}
	}

	public void latchSet(String name, int count) {
		Request request = new Request(RequestType.LATCH_SET, name, Integer.toString(count));
		Replay replay = send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Wait failed");
		}
	}
}
