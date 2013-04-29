package cz.cuni.mff.d3s.been.web.components;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.ioc.annotations.Inject;

import cz.cuni.mff.d3s.been.web.services.BeenApiService;

/**
 * User: donarus Date: 4/28/13 Time: 12:16 PM
 */
public abstract class Component {

	@Inject
	protected BeenApiService api;

	@Inject
	protected AlertManager alertManager;

}
