package cz.cuni.mff.d3s.been.web.services;

import cz.cuni.mff.d3s.been.api.BeenApi;

/**
 * User: donarus
 * Date: 4/27/13
 * Time: 11:48 AM
 */
public interface BeenApiService {


    public boolean isConnected();

    public boolean connect(String host, int port, String groupName, String groupPassword);

    public BeenApi getApi();

}
