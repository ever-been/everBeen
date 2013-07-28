package cz.cuni.mff.d3s.been.web.pages.result;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * @author Kuba Brecka
 */
public class Delete extends Page {

	Object onActivate(String resultId) throws BeenApiException {
		this.api.getApi().deleteResult(resultId);

		return List.class;
	}

}
