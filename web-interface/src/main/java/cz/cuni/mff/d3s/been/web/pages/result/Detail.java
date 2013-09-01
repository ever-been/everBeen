package cz.cuni.mff.d3s.been.web.pages.result;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.model.ResultSupport;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.RESULTS_LIST)
public class Detail extends Page {

	@Property
	private EvaluatorResult result;

	@Property
	private String resultId;

	void onActivate(String resultId) throws DAOException, BeenApiException {
		this.resultId = resultId;
		result = this.api.getApi().getEvaluatorResult(resultId);
	}

	Object onPassivate() {
		return resultId;
	}

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	Link getResultIframeUrl() {
		return pageRenderLinkSource.createPageRenderLinkWithContext(Raw.class, resultId);
	}

	Object onDeleteResult(String resultId) throws BeenApiException, InterruptedException {
		new ResultSupport(getApi()).deleteResult(resultId);
		return List.class;
	}

}
