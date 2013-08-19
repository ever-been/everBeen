package cz.cuni.mff.d3s.been.web.services;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiFactory;
import cz.cuni.mff.d3s.been.api.ClusterConnectionUnavailableException;

/**
 * User: donarus Date: 4/27/13 Time: 11:51 AM
 */
public class BeenApiServiceImpl implements BeenApiService {

	private BeenApi api = null;

	@Override
	public boolean isConnected() {
		return (api != null) && api.isConnected();
	}

	@Override
	public boolean connect(String host, int port, String groupName, String groupPassword) {
		if (api != null) {
			api.shutdown();
			api = null;
		}
		if (!isConnected()) {
			if (api != null) {
				api.shutdown();
			}
			api = null;
		}
		api = BeenApiFactory.connect(host, port, groupName, groupPassword);
		return isConnected();
	}

	@Override
	public BeenApi getApi() throws ClusterConnectionUnavailableException {
		if (!isConnected())
			throw new ClusterConnectionUnavailableException("API is not connected.");

		return api;
	}

	@Override
	public void disconnect() {
		if (isConnected()) {
			api.shutdown();
		}
		api = null;
	}

}
