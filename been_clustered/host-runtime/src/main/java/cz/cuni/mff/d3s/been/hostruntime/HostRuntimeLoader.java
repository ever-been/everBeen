package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.protocol.api.AbstractNodeRunner;
import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;

/**
 * 
 * @author donarus
 * 
 */
public class HostRuntimeLoader extends AbstractNodeRunner {

	public static void main(String[] args) {
		new HostRuntimeLoader().start();
	}

	@Override
	protected void createAndStartService(Messaging messaging, DataPersistence dataPersistence, String nodeId) {
		new HostRuntime(messaging, dataPersistence, new TaskRunner(), nodeId).start();
	}

}
