package cz.cuni.mff.d3s.been.web.pages.benchmark;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.web.pages.Page;
import cz.cuni.mff.d3s.been.web.pages.task.Tree;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;

import java.io.IOException;

/**
 * @author Kuba Brecka
 */
public class DisallowResubmits extends Page {

	@Inject
	private Response response;

	Object onActivate(String benchmarkId) throws BeenApiException, IOException {
		this.api.getApi().disallowResubmitsForBenchmark(benchmarkId);

		response.sendRedirect("/benchmark/detail/" + benchmarkId);
		return null;
	}

}
