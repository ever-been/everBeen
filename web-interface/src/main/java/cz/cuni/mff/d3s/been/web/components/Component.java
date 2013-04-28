package cz.cuni.mff.d3s.been.web.components;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.web.services.BeenApiService;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: donarus
 * Date: 4/28/13
 * Time: 12:16 PM
 */
public abstract class Component {

    @Inject
    protected BeenApiService api;

}
