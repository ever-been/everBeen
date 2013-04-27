package cz.cuni.mff.d3s.been.web.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import cz.cuni.mff.d3s.been.web.services.BeenApiService;
import org.slf4j.Logger;

/**
 * User: donarus Date: 4/27/13 Time: 1:05 PM
 */
public abstract class Page {

	@Inject
    @Property
	protected BeenApiService api;

    @Inject
    protected Logger log;


	Object onActivate() {
		if (!api.isConnected()) {
            log.info("Been Api is not connected. Redirecting to Connect page.");
            return Connect.class;
		}
		return null;
	}
}
