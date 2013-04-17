package cz.cuni.mff.d3s.been.hostruntime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.action.Action;
import cz.cuni.mff.d3s.been.cluster.action.Actions;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.Replays;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * 
 * 
 * @author Martin Sixta
 */
public class TaskRequestThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(TaskRequestThread.class);
	private final ZMQ.Context context;
	private final ClusterContext ctx;

	BlockingQueue<Integer> portQueue = new LinkedBlockingQueue<>();

	TaskRequestThread(ZMQ.Context context, ClusterContext ctx) {

		this.context = context;
		this.ctx = ctx;

	}

	@Override
	public void run() {
		ZMQ.Socket socket = context.socket(ZMQ.REP);
		int reqPort = socket.bindToRandomPort("tcp://localhost");
		portQueue.add(reqPort);

		while (!Thread.currentThread().isInterrupted() && reqPort > 0) {
			String json = socket.recvStr();

			log.debug("REQUEST FROM TASK: {}", json);

			Request request = null;

			try {
				request = Request.fromJson(json);
			} catch (JSONUtils.JSONSerializerException e) {
				e.printStackTrace();
				socket.send(Replays.createErrorReplay("Cannot deserialize").toJson());
				continue;
			}

			Action action = Actions.createAction(request, ctx);
			Replay replay = action.goGetSome();

			try {
				socket.send(replay.toJson());
			} catch (Exception e) {
				e.printStackTrace();
				if (socket != null) {
					socket.close();
					socket = null;
				}
			}
		}

		if (socket != null) {
			socket.close();
		}

	}

	public int getPort() {
		try {
			return portQueue.take();
		} catch (InterruptedException e) {
			return -1;
		}
	}
}
