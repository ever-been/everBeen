package cz.cuni.mff.d3s.been.hostruntime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.action.Action;
import cz.cuni.mff.d3s.been.cluster.action.Actions;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.mq.Context;
import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * 
 * 
 * @author Martin Sixta
 */
public class TaskRequestBrokerThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(TaskRequestBrokerThread.class);
	private final ZMQ.Context context;
	private final ClusterContext ctx;
	final ExecutorService executorService;
	final String WORKERS_PROTO = "inproc";
	final String WORKERS_NAME = "been.hr.task.request.workers";
	final String WORKERS_ADDRESS = String.format("%s://%s", WORKERS_PROTO, WORKERS_NAME);

	BlockingQueue<Integer> portQueue = new LinkedBlockingQueue<>();

	private static final int UNBOUNDED_PORT = -1;
	private int clientsPort = UNBOUNDED_PORT;

	TaskRequestBrokerThread(ClusterContext ctx) {
		this.context = Context.getReference();
		this.ctx = ctx;

		executorService = Executors.newCachedThreadPool();

	}

	@Override
	public void run() {
		ZMQ.Socket clients = context.socket(ZMQ.ROUTER);
		final int reqPort = clients.bindToRandomPort("tcp://localhost");
		log.debug("{} accepts requests on port tcp://localhost:{}", TaskRequestBrokerThread.class, reqPort);
		//TODO check reqPort

		portQueue.add(reqPort);

		ZMQ.Socket workers = context.socket(ZMQ.DEALER);
		workers.bind(WORKERS_ADDRESS);

		try {
			proxy(clients, workers);
		} finally {
			clients.close();
			workers.close();
			Context.releaseContext();
		}

		log.debug("{} exiting.", TaskRequestBrokerThread.class);
	}

	private void proxy(final ZMQ.Socket clients, final ZMQ.Socket workers) {

		// Initialize poll set
		ZMQ.Poller items = context.poller(2);

		items.register(clients, ZMQ.Poller.POLLIN);
		items.register(workers, ZMQ.Poller.POLLIN);

		boolean more = false;
		byte[] message;

		while (!Thread.currentThread().isInterrupted()) {
			items.poll();

			if (items.pollin(0)) {

				final List<byte[]> frames = new ArrayList<>(3);

				while (true) {
					message = clients.recv(0);
					more = clients.hasReceiveMore();

					frames.add(message);

					if (!more) {
						break;
					}
				}

				executorService.submit(new RequestorRunnable(frames));
			}

			if (items.pollin(1)) {
				while (true) {
					message = workers.recv(0);
					more = workers.hasReceiveMore();

					while (!clients.send(message, more ? ZMQ.SNDMORE : 0));
					if (!more) {
						break;
					}
				}
			}
		}

	}

	/*
	private static class Frames {
		private List<byte[]> frames;

		void add(byte[] frame) {
			frames.add(frame);
		}

	}
	*/

	private class RequestorRunnable implements Runnable {

		private final List<byte[]> frames;

		RequestorRunnable(List<byte[]> frames) {

			this.frames = frames;
		}

		private Reply handleRequestor(String json) {
			log.debug("REQUEST FROM A TASK: {}", json);

			Request request = null;

			try {
				request = Request.fromJson(json);
			} catch (JSONUtils.JSONSerializerException e) {
				return Replies.createErrorReply("Cannot deserialize");
			}

			Action action = Actions.createAction(request, ctx);
			return action.handle();

		}

		@Override
		public void run() {
			String json = new String(frames.get(2));
			Reply reply = handleRequestor(json);
			sendReply(reply);

		}

		private void sendReply(Reply reply) {
			ZMQ.Socket socket = context.socket(ZMQ.DEALER);
			try {
				socket.connect(WORKERS_ADDRESS);
				socket.send(frames.get(0), ZMQ.SNDMORE);
				socket.send(frames.get(1), ZMQ.SNDMORE);
				socket.send(reply.toJson().getBytes(), 0);
			} catch (Exception e) {
				//TODO
			} finally {
				socket.close();
			}

		}
	}

	// TODO REVIEW, RETHINK
	public synchronized int getPort() {
		if (clientsPort == UNBOUNDED_PORT) {
			try {
				clientsPort = portQueue.take();
			} catch (InterruptedException e) {
				return UNBOUNDED_PORT;
			}
		}
		return clientsPort;
	}

}
