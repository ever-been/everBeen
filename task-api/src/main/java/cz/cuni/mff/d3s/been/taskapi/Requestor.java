package cz.cuni.mff.d3s.been.taskapi;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.Replays;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
@NotThreadSafe
public class Requestor {
	private static Logger log = LoggerFactory.getLogger(Requestor.class);

	private static ZMQ.Context context = null;
	private static int count = 0;

	private static String address = String.format("tcp://localhost:%s", System.getenv("REQUEST_PORT"));

	private static synchronized ZMQ.Context getContext() {
		if (context == null) {
			context = ZMQ.context();
		}

		++count;
		return context;
	}

	ZMQ.Socket socket;

	public Requestor() {
		ZMQ.Context ctx = getContext();
		this.socket = ctx.socket(ZMQ.REQ);
		socket.setLinger(0);
		socket.connect(address);
		socket.setLinger(0);
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
		releaseContext();
	}

	private static synchronized void releaseContext() {
		if (--count == 0) {
			context.term();
			log.debug("Requestor context terminated");
			context = null;
		}
	}
}
