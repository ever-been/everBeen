package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.core.protocol.api.AbstractNode;
import cz.cuni.mff.d3s.been.core.protocol.api.AbstractNodeRunner;
import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;

/**
 * 
 * @author donarus
 * 
 */
public class SoftwareRepositoryRunner extends AbstractNodeRunner {

	public static void main(String[] args) {
		SoftwareRepositoryRunner runner = new SoftwareRepositoryRunner();
		runner.start();
	}

	@Override
	protected void createAndStartService(Messaging messaging, DataPersistence dataPersistence) {
		// FIXME host and port configuration
		HttpServer httpServer = new HttpServer("localhost", 8000);
		AbstractNode node = new SoftwareRepository(messaging, dataPersistence, httpServer);
		node.start();
	}

}
