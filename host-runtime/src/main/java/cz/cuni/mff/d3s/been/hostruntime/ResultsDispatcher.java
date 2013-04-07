package cz.cuni.mff.d3s.been.hostruntime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.Messaging;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * This dispatcher recieves serialized results from the Task and queues them up
 * to the results queue, from which they are processed further.
 * 
 * @author Martin Sixta
 */
final class ResultsDispatcher implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ResultsDispatcher.class);

	private final ClusterContext clusterContext;
	private final String hostName;

	final IMessageQueue<String> resultsQueue;
	IMessageReceiver<String> receiver;

	ResultsDispatcher(ClusterContext clusterContext, String host) {
		this.clusterContext = clusterContext;
		this.hostName = host;
		resultsQueue = Messaging.createTcpQueue(host);
	}

	public int getPort() {
		return receiver.getPort();
	}

	public void start() throws MessagingException {
		receiver = resultsQueue.getReceiver();
	}

	public void stop() {
		Thread.currentThread().interrupt();
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				String msg = receiver.receive();
				log.debug("Results received: {}", msg);
			} catch (MessagingException e) {
				log.error("Cannot receive result:", e);
			}
		}
		resultsQueue.terminate();
	}

}
