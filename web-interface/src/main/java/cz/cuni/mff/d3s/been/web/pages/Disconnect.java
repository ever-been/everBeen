package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.web.services.BeenApiService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * @author Kuba Brecka
 */
public class Disconnect extends Page {

	@Override
	Object onActivate() {
		this.api.disconnect();
		return Connect.class;
	}

}
