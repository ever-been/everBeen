package cz.cuni.mff.d3s.been.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Client;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.IMessageSender;

/**
 * 
 * Listens for client connection events.
 * 
 * @author Martin Sixta
 */
final class ClientListener extends TaskManagerService implements com.hazelcast.core.ClientListener {

	private ClusterContext clusterCtx;
	private IMessageSender sender;

	public ClientListener(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
	}

	private static final Logger log = LoggerFactory.getLogger(ClientListener.class);

	@Override
	public void clientConnected(Client client) {
		log.debug("Client connected: " + client);
	}

	@Override
	public void clientDisconnected(Client client) {
		log.debug("Client disconnected: " + client);
	}

	@Override
	public void start() throws ServiceException {
		sender = createSender();
		clusterCtx.getClientService().addClientListener(this);
	}

	@Override
	public void stop() {
		clusterCtx.getClientService().removeClientListener(this);
		sender.close();
	}

}
