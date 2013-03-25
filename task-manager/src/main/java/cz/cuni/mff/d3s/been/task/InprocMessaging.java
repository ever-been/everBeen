package cz.cuni.mff.d3s.been.task;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.jeromq.ZMQ;

/**
 * Inter-process communication (PUSH-PULL model in 0MQ). This is the PUSH part.
 * 
 * Implementation notes: It is implemented with zmq. I would rather use an actor
 * library (Akka!!) but the use case is simple enough and we already depend on
 * zmq (jeromq). So consider this as "poor man's actor library".
 * 
 * The class IS NOT thread safe (it's not supposed to be).
 * 
 * @author Martin Sixta
 */
final class InprocMessaging {
	private final ZMQ.Context context;
	private ZMQ.Socket socket;

	private static String INPROC_PROTO = "inproc";

	private String INPROC_CONN;

	public InprocMessaging(final ZMQ.Context context, final String queue) {
		this.context = context;
		INPROC_CONN = String.format("%s://%s", INPROC_PROTO, queue);

		try {
			connect();
		} catch (TaskManagerException e) {
			e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public void connect() throws TaskManagerException {
		if (socket == null) {
			socket = context.socket(ZMQ.PUSH);
			boolean connected = socket.connect(INPROC_CONN);
			if (connected = false) {
				String msg = String.format("Cannot connect to %s", INPROC_CONN);
			}
		}
	}

	public void disconnect() {
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}

	public void send(final Serializable object) {
		checkIsConnected();
		socket.send(SerializationUtils.serialize(object));
	}

	public void send(final String msg) {
		checkIsConnected();
		socket.send(msg);
	}

	private void checkIsConnected() {
		if (socket == null) {
			throw new IllegalStateException("Not connected!");
		}
	}
}
