package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.Client;
import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.ClusterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Listens for client connection events. Takes appropriate actions.
 *
 *
 *
 * @author Martin Sixta
 */
public class ClientListener implements com.hazelcast.core.ClientListener, IClusterService {

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
		ClusterUtils.getClientService().addClientListener(this);
	}

	@Override
	public void stop() {
		ClusterUtils.getClientService().removeClientListener(this);
	}
}
