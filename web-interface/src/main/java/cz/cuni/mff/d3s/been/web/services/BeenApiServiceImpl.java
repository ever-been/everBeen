package cz.cuni.mff.d3s.been.web.services;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiImpl;
import cz.cuni.mff.d3s.been.api.ServiceUnavailableException;

import java.net.InetSocketAddress;

/**
 * User: donarus
 * Date: 4/27/13
 * Time: 11:51 AM
 */
public class BeenApiServiceImpl implements BeenApiService {

    private BeenApi api = null;

    @Override
    public boolean isConnected() {
        return api != null;
    }

    @Override
    public boolean connect(String host, int port, String groupName, String groupPassword) {
        api = new BeenApiImpl(host, port, groupName, groupPassword);
        return true;
    }

    @Override
	public BeenApi getApi() {
		if (! isConnected())
			throw new ServiceUnavailableException("API is not connected.");

		return api;
	}
}
