package cz.cuni.mff.d3s.been.web.model;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;

/**
 * @author donarus
 */
public final class ResultSupport {

	private final BeenApi api;

	public ResultSupport(BeenApi api) {
		this.api = api;
	}

	public void deleteResult(String resultId) throws BeenApiException {
		this.api.deleteResult(resultId);
	}

}
