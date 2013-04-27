package cz.cuni.mff.d3s.been.web.services;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiImpl;

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
    public boolean connect(InetSocketAddress address) {
        api = new BeenApiImpl(address);
        return true;
    }
}
