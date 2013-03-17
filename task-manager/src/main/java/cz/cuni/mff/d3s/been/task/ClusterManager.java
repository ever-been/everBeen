package cz.cuni.mff.d3s.been.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public ClusterManager(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
		localTaskListener = new LocalTaskListener(clusterCtx);
		membershipListener = new MembershipListener(clusterCtx);
		clientListener = new ClientListener(clusterCtx);

	}

	@Override
	public void start() {
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
