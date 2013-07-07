package cz.cuni.mff.d3s.been.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.taskapi.CheckpointController;
import cz.cuni.mff.d3s.been.taskapi.Task;

/**
 * @author Martin Sixta
 */
public class ExampleSyncTask extends Task {

	private CheckpointController requestor;

	enum TaskType {
		SERVER, CLIENT, FINALIZER
	}

	// what am i?
	static final String TYPE_KEY = "been.task.sync.type";

	// how many clients
	static final String CLIENT_COUNT_KEY = "been.task.sync.count";

	// how many runs a client should do
	static final String CLIENT_RUNS_KEY = "been.task.sync.runs";

	// where to get the address of the server
	private static final String CHECKPOINT_ADDRESS = "been.task.sync.address";

	// how to tell that a client should start the test
	private static final String CHECKPOINT_GO = "been.task.sync.go";

	// wait for all clients to connect
	private static final String RENDEZVOUS = "been.task.sync.rendezvous";

	private static final Logger log = LoggerFactory.getLogger(ExampleSyncTask.class);

	public static void main(String[] args) {
		new ExampleSyncTask().doMain(args);
	}

	@Override
	public void run(String[] args) {

		TaskType type = TaskType.valueOf(System.getenv(TYPE_KEY));

		try {
			requestor = CheckpointController.create();

			switch (type) {
				case SERVER:
					runServer();
					break;
				case CLIENT:
					runClient();
					break;
				case FINALIZER:
					System.out.println("FINALIZER");
					break;
			}

			requestor.close();
		} catch (Exception e) {
			log.error("Error while running test: ", e);
		}

		log.info("END-OF-TIME-AS-WE-KNOW-IT");

	}

	/**
	 * Client code
	 */
	private void runClient() {
		// get server address and port
		String address = requestor.checkPointWait(CHECKPOINT_ADDRESS);

		// connect to the server
		ZMQ.Context context = ZMQ.context();
		ZMQ.Socket socket = context.socket(ZMQ.REQ);
		socket.connect(address);

		try {
			// count down
			requestor.latchCountDown(RENDEZVOUS);

			// wait for others
			requestor.checkPointWait(CHECKPOINT_GO);
			log.info("CHECKPOINT_GO reached");

			// --------------------------------------------------------------------
			// test
			// --------------------------------------------------------------------
			int runs = Integer.valueOf(System.getenv(CLIENT_RUNS_KEY));

			long start = System.nanoTime();
			for (int run = 0; run < runs; ++run) {
				String msg = String.format("CLIENT: %s, RUN: %d", getId(), run);
				socket.send(msg);

				//do nothing with the reply
				socket.recvStr();
			}

			// print something
			long end = System.nanoTime();
			log.info("Test completed in {} ms", (end - start) / 1000000);

		} finally {
			// don't forget to close the connection
			socket.close();
			context.term();

		}

	}

	/**
	 * Server code
	 */
	private void runServer() throws UnknownHostException {
		// prepare the server
		String host = InetAddress.getLocalHost().getHostName();
		if (host == null) {
			host = "localhost";
		}
		String address = String.format("tcp://%s", host);
		ZMQ.Context context = ZMQ.context();

		ZMQ.Socket socket = context.socket(ZMQ.REP);
		int port = socket.bindToRandomPort(address);

		address = String.format("%s:%d", address, port);

		try {
			// set count down latch, this must be done before address checkpoint!
			requestor.latchSet(RENDEZVOUS, getNumberOfClients());

			// set checkpoint
			requestor.checkPointSet(CHECKPOINT_ADDRESS, address);

			// wait for all clients
			requestor.latchWait(RENDEZVOUS);

			requestor.checkPointSet(CHECKPOINT_GO, "go");

			int totalNumberOfConnections = getTotalNumberOfConnections();
			long start = System.nanoTime();
			for (int i = 0; i < totalNumberOfConnections; ++i) {
				String msg = socket.recvStr();
				socket.send("OK: " + msg);
			}

			// print something
			long end = System.nanoTime();
			log.info("Server completed test in {} ms ", (end - start) / 1000000);

		} finally {
			// don't forget to close the connection
			socket.close();
			context.term();
		}

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

}
