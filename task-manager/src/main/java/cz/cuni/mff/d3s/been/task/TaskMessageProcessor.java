package cz.cuni.mff.d3s.been.task;

import org.apache.commons.lang3.SerializationUtils;
import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * @author Martin Sixta
 */
final class TaskMessageProcessor extends Thread {

	private static Logger log = LoggerFactory.getLogger(TaskMessageProcessor.class);;

	private final ClusterContext clusterCtx;
	private final ZMQ.Context zmqCtx;

	private ZMQ.Socket socket;

	private static final String INPROC_PROTO = "inproc";

	private String INPROC_CONN;

	private int port;

	public TaskMessageProcessor(ClusterContext clusterCtx, ZMQ.Context zmqCtx, String queue)
			throws TaskManagerException {
		this.clusterCtx = clusterCtx;

		this.zmqCtx = zmqCtx;

		INPROC_CONN = String.format("%s://%s", INPROC_PROTO, queue);

		socket = zmqCtx.socket(ZMQ.PULL);

		port = socket.bind(INPROC_CONN);

		log.info("Task Message Processor bind to port {}", port);

	}

	@Override
	public void run() {
		while (true) {
			byte[] bytes = socket.recv();

			TaskMessage message;

			try {
				message = (TaskMessage) SerializationUtils.deserialize(bytes);
				log.info("Task Action of type '{}' received", message.getClass());

				TaskAction action = TaskActions.createAction(clusterCtx, message);

				action.execute();

			} catch (TaskManagerException e) {

			} catch (Exception e) {
				log.error("Cannot deserialize received message.", e);
				continue;
			}

		}

	}
}
