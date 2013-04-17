package cz.cuni.mff.d3s.been.task;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.ReplayType;
import cz.cuni.mff.d3s.been.mq.req.Request;
import cz.cuni.mff.d3s.been.mq.req.RequestType;
import cz.cuni.mff.d3s.been.taskapi.Requestor;
import cz.cuni.mff.d3s.been.taskapi.Task;

/**
 * @author Martin Sixta
 */
public class ExampleSyncTask extends Task {

	private Requestor requestor;

	enum TaskType {
		SERVER, CLIENT
	}

	// what am i?
	private static final String TYPE_KEY = "been.task.sync.type";

	// how many clients
	private static final String CLIENT_COUNT_KEY = "been.task.sync.count";

	// how many runs a client should do
	private static final String CLIENT_RUNS_KEY = "been.task.sync.runs";

	// where to get the address of the server
	private static final String CHECKPOINT_ADDRESS = "been.task.sync.address";

	// how to tell that a client should start the test
	private static final String CHECKPOINT_GO = "been.task.sync.go";

	// wait for all clients to connect
	private static final String RENDEZVOUS = "been.task.sync.rendezvous";

	private static final Logger log = LoggerFactory.getLogger(Task.class);

	public static void main(String[] args) {
		new ExampleSyncTask().doMain(args);
	}

	@Override
	public void run() {
		requestor = new Requestor();
		TaskType type = TaskType.valueOf(System.getenv(TYPE_KEY));

		try {
			if (type == TaskType.SERVER) {
				runServer();
			} else {
				runClient();
			}
		} catch (Exception e) {
			log.error("Error while running test: ", e);
		}

		requestor.close();

		log.info("END-OF-TIME-AS-WE-KNOW-IT");

	}

	/**
	 * Client code
	 */
	private void runClient() {
		// get server address and port
		String address = waitCheckPoint(CHECKPOINT_ADDRESS);

		// connect to the server
		ZMQ.Context context = ZMQ.context();
		ZMQ.Socket socket = context.socket(ZMQ.REQ);
		socket.connect(address);

		// count down
		countDown(RENDEZVOUS);

		// wait for others
		waitCheckPoint(CHECKPOINT_GO);
		log.info("CHECKPOINT_GO reached");

		// --------------------------------------------------------------------
		// test
		// --------------------------------------------------------------------
		int runs = Integer.valueOf(System.getenv(CLIENT_RUNS_KEY));

		long start = System.nanoTime();
		for (int run = 0; run < runs; ++run) {
			String msg = String.format("CLIENT: %s, RUN: %d", getId(), run);
			socket.send(msg);

			//do nothing with the replay
			socket.recvStr();
		}

		// don't forget to close the connection
		socket.close();
		context.term();

		// print something
		long end = System.nanoTime();
		log.info("Test complected in {} ms", (end - start) / 1000000);

	}

	/**
	 * Server code
	 */
	private void runServer() {
		// prepare the server
		String address = "tcp://localhost";
		ZMQ.Context context = ZMQ.context();

		ZMQ.Socket socket = context.socket(ZMQ.REP);
		int port = socket.bindToRandomPort(address);

		address = String.format("%s:%d", address, port);

		// set count down latch, this must be done before address checkpoint!
		countSet(RENDEZVOUS, getNumberOfClients());

		// set checkpoint
		setCheckPoint(CHECKPOINT_ADDRESS, address);

		// wait for all clients
		countWait(RENDEZVOUS);

		setCheckPoint(CHECKPOINT_GO, "go");

		int totalNumberOfConnections = getTotalNumberOfConnections();
		long start = System.nanoTime();
		for (int i = 0; i < totalNumberOfConnections; ++i) {
			String msg = socket.recvStr();
			socket.send("OK: " + msg);
		}

		// don't forget to close the connection
		socket.close();
		context.term();

		// print something
		long end = System.nanoTime();
		log.info("Server completed test in {} ms ", (end - start) / 1000000);

	}

	// ------------------------------------------------------------------------
	// some dumb helpers
	// ------------------------------------------------------------------------
	private int getNumberOfClients() {
		return Integer.valueOf(System.getenv(CLIENT_COUNT_KEY));
	}

	private int getNumberOfRuns() {
		return Integer.valueOf(System.getenv(CLIENT_RUNS_KEY));
	}

	private int getTotalNumberOfConnections() {
		return getNumberOfClients() * getNumberOfRuns();
	}

	// ------------------------------------------------------------------------
	// TODO: following should be part of task-api, this is a scatch
	// ------------------------------------------------------------------------

	private void setCheckPoint(String key, String value) {
		Request request = new Request(RequestType.SET, "cp#" + key, value);
		Replay replay = requestor.send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Address set failed");
		}
	}

	private String waitCheckPoint(String key) {
		Request request = new Request(RequestType.WAIT, "cp#" + key, null);
		Replay replay = requestor.send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Wait failed");
		}

		return replay.getValue();
	}

	private void countWait(String name) {
		Request request = new Request(RequestType.LATCH_WAIT, name, null);
		Replay replay = requestor.send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Wait for count down failed");
		}
	}

	private void countDown(String name) {
		Request request = new Request(RequestType.LATCH_DOWN, name, null);
		Replay replay = requestor.send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Wait failed");
		}
	}

	private void countSet(String name, int count) {
		Request request = new Request(RequestType.LATCH_SET, name, Integer.toString(count));
		Replay replay = requestor.send(request);

		if (replay.getReplayType() != ReplayType.OK) {
			log.error(replay.getValue());
			throw new RuntimeException("Wait failed");
		}
	}
}
