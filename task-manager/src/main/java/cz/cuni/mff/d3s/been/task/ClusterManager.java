package cz.cuni.mff.d3s.been.task;

import static cz.cuni.mff.d3s.been.task.TaskManagerNames.ACTION_QUEUE_NAME;

import cz.cuni.mff.d3s.been.cluster.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.MessageQueues;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * Manages local cluster resources.
 * 
 * @author Martin Sixta
 */
final class ClusterManager implements IClusterService {
	private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);

	private final LocalRuntimeListener localRuntimeListener;
	private final LocalTaskListener localTaskListener;
	private final LocalContextListener localContextListener;
	private final MembershipListener membershipListener;
	private final ClientListener clientListener;
	private final ClusterContext clusterCtx;
	LocalKeyScanner keyScanner;    // used only in DATA members

	private final MessageQueues messageQueues = MessageQueues.getInstance();

	public ClusterManager(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;

		localRuntimeListener = new LocalRuntimeListener(clusterCtx);
		localTaskListener = new LocalTaskListener(clusterCtx);
		localContextListener = new LocalContextListener(clusterCtx);
		membershipListener = new MembershipListener(clusterCtx);
		clientListener = new ClientListener(clusterCtx);

	}

	private TaskMessageProcessor taskMessageProcessor;

	@Override
	public void start() throws ServiceException {
		log.info("Starting Task Manager...");
		try {
			messageQueues.createInprocQueue(ACTION_QUEUE_NAME);
		} catch (MessagingException e) {
			throw new ServiceException("Cannot start clustered Task Manager", e);
		}

		taskMessageProcessor = new TaskMessageProcessor(clusterCtx);

		taskMessageProcessor.start();
		localRuntimeListener.start();
		localTaskListener.start();
		localContextListener.start();
		membershipListener.start();
		clientListener.start();
        if (clusterCtx.getInstanceType() == NodeType.DATA) {
            // keyScanner on Lite member is nonsense .. lite member hasno data
            // keyScanner on Native instance is invalid .. native instance is not member
            keyScanner = new LocalKeyScanner(clusterCtx);
            keyScanner.start();
        }
		log.info("Task Manager started.");
	}

	@Override
	public void stop() {
		log.info("Stopping Task Manager...");
        if (keyScanner != null) {
		    keyScanner.stop();
        }
		clientListener.stop();
		localRuntimeListener.stop();
		localContextListener.stop();
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
