package cz.cuni.mff.d3s.been.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.Messaging;
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

	private static final String ACTION_QUEUE_NAME = "been.tm.queue";

	private final IMessageQueue<TaskMessage> actionQueue;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public ClusterManager(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;

		localTaskListener = new LocalTaskListener(clusterCtx);
		membershipListener = new MembershipListener(clusterCtx);
		clientListener = new ClientListener(clusterCtx);

		this.actionQueue = Messaging.createInprocQueue(ACTION_QUEUE_NAME);

	}

	private TaskMessageProcessor taskMessageProcessor;

	@Override
	public void start() throws ServiceException {

		IMessageReceiver<TaskMessage> receiver;
		LocalKeyScanner keyScanner = new LocalKeyScanner(clusterCtx);
		try {
			receiver = actionQueue.getReceiver();

			localTaskListener.withSender(actionQueue.createSender());
			membershipListener.withSender(actionQueue.createSender());
			clientListener.withSender(actionQueue.createSender());
			keyScanner.withSender(actionQueue.createSender());

		} catch (MessagingException e) {
			throw new ServiceException("Cannot start clustered Task Manager", e);
		}

		taskMessageProcessor = new TaskMessageProcessor(clusterCtx, receiver);

		taskMessageProcessor.start();

		localTaskListener.start();
		membershipListener.start();
		clientListener.start();

		scheduler.scheduleAtFixedRate(keyScanner, 5, 10, TimeUnit.SECONDS);

	}

	@Override
	public void stop() {
		clientListener.stop();
		membershipListener.stop();
		localTaskListener.stop();
		scheduler.shutdown();
		actionQueue.terminate();
	}

}
