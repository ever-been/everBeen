package cz.cuni.mff.d3s.been.web.pages.cluster;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.api.ClusterConnectionUnavailableException;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.CLUSTER_LOGS)
public class Logs extends Page {

	@Property
	private Collection<Date> dates;

	@Property
	private Date date;

	void onActivate() throws BeenApiException {
		this.dates = this.api.getApi().getServiceLogsAvailableDates();
	}

}
