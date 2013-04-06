package cz.cuni.mff.d3s.been.hostruntime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.Messaging;
import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * @author Martin Sixta
 */
final class ResultsDispatcher implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ResultsDispatcher.class);

	private final ClusterContext clusterContext;
	private final RuntimeInfo hostRuntimeInfo;
	private final String host;

	final IMessageQueue<String> resultsQueue;
	final IMessageReceiver<String> receiver;

	ResultsDispatcher(ClusterContext clusterContext, RuntimeInfo hostRuntimeInfo, String host)
			throws MessagingException {
		this.clusterContext = clusterContext;
		this.hostRuntimeInfo = hostRuntimeInfo;
		this.host = host;
		resultsQueue = Messaging.createTcpQueue(host);

		receiver = resultsQueue.getReceiver();
	}

	public int getPort() {
		return receiver.getPort();
	}

	@Override
	public void run() {
		try {

			while (true) {
				String msg = receiver.receive();

				log.debug("Results received: {}", msg);

			}

		} catch (MessagingException e) {

			log.error("Cannot receive results", e);

		}
	}
}
