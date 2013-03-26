package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Client;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.IMessageSender;

/**
 * 
 * Listens for client connection events. Takes appropriate actions.
 * 
 * 
 * 
 * @author Martin Sixta
 */
public class ClientListener implements com.hazelcast.core.ClientListener, IClusterService {

	private ClusterContext clusterCtx;
	private IMessageSender inprocMessaging;

	public ClientListener(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
	}

	private static final Logger log = LoggerFactory.getLogger(ClientListener.class);

	@Override
	public void clientConnected(Client client) {
		log.info("Client connected: " + client);
	}

	@Override
	public void clientDisconnected(Client client) {
		log.info("Client disconnected: " + client);
	}

	@Override
	public void start() {
		clusterCtx.getClientService().addClientListener(this);
	}

	@Override
	public void stop() {
		clusterCtx.getClientService().removeClientListener(this);
	}

	public void withSender(IMessageSender inprocMessaging) {
		this.inprocMessaging = inprocMessaging;
	}
}
