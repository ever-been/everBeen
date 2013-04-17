package cz.cuni.mff.d3s.been.task;

import static cz.cuni.mff.d3s.been.task.TaskManagerNames.ACTION_QUEUE_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * 
 * TODO: race conditions 1) key ownership changes before registering the
 * membershipListener * need to rescan local keys 2) client disconnect before
 * registering the clientListener * scan connected host runtimes ?
 * 
 * @author Martin Sixta
 */
final class ClusterManager implements IClusterService {
	private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);

	private final LocalTaskListener localTaskListener;
	private final MembershipListener membershipListener;
	private final ClientListener clientListener;
	private final ClusterContext clusterCtx;
	LocalKeyScanner keyScanner;

	private final MessageQueues messageQueues = MessageQueues.getInstance();

	public ClusterManager(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;

		localTaskListener = new LocalTaskListener(clusterCtx);
		membershipListener = new MembershipListener(clusterCtx);
		clientListener = new ClientListener(clusterCtx);

	}

	private TaskMessageProcessor taskMessageProcessor;

	@Override
	public void start() throws ServiceException {

		keyScanner = new LocalKeyScanner(clusterCtx);
		try {
			messageQueues.createInprocQueue(ACTION_QUEUE_NAME);
		} catch (MessagingException e) {
			throw new ServiceException("Cannot start clustered Task Manager", e);
		}

		taskMessageProcessor = new TaskMessageProcessor(clusterCtx);

		taskMessageProcessor.start();
		localTaskListener.start();
		membershipListener.start();
		clientListener.start();
		keyScanner.start();

	}

	@Override
	public void stop() {
		keyScanner.stop();
		clientListener.stop();
		localTaskListener.stop();
		membershipListener.stop();
		taskMessageProcessor.poison();

		log.info("Task Manager stopped.");

	}

	@Override
	public Reaper createReaper() {
		return new Reaper() {

			@Override
			protected void reap() throws InterruptedException {
				ClusterManager.this.stop();
			}
		};
	}
}
