package cz.cuni.mff.d3s.been.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jeromq.ZMQ;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * 
 * TODO: race conditions 1) key ownership changes before registering the
 * membershipListener * need to rescan local keys 2) client disconnect before
 * registering the clientListener * scan connected host runtimes ?
 * 
 * @author Martin Sixta
 */
final class ClusterManager implements IClusterService {
	private final LocalTaskListener localTaskListener;
	private final MembershipListener membershipListener;
	private final ClientListener clientListener;
	private final ClusterContext clusterCtx;

	private static final String INPROC_QUEUE = "been.tm.queue";

	private volatile ZMQ.Context zmqContext;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public ClusterManager(ClusterContext clusterCtx) {

		this.clusterCtx = clusterCtx;

		zmqContext = ZMQ.context();

		localTaskListener = new LocalTaskListener(clusterCtx);
		membershipListener = new MembershipListener(clusterCtx);
		clientListener = new ClientListener(clusterCtx);

	}

	private TaskMessageProcessor taskActionProcessor;
	@Override
	public void start() {

		try {
			taskActionProcessor = new TaskMessageProcessor(clusterCtx, zmqContext, INPROC_QUEUE);
		} catch (TaskManagerException e) {
			e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
			return;
		}

		taskActionProcessor.start();

		localTaskListener.withInprocMessaging(new InprocMessaging(zmqContext, INPROC_QUEUE));
		membershipListener.withInprocMessaging(new InprocMessaging(zmqContext, INPROC_QUEUE));
		clientListener.withInprocMessaging(new InprocMessaging(zmqContext, INPROC_QUEUE));

		localTaskListener.start();
		membershipListener.start();
		clientListener.start();

		scheduler.scheduleAtFixedRate(new LocalKeyScanner(clusterCtx), 5, 5, TimeUnit.SECONDS);

		System.out.println("My ID is: " + clusterCtx.getId());

	}

	@Override
	public void stop() {
		clientListener.stop();
		membershipListener.stop();
		localTaskListener.stop();
		scheduler.shutdown();
	}

}
