package cz.cuni.mff.d3s.been.web.pages.result;

import java.util.Collection;

import org.apache.tapestry5.annotations.Property;

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
public class List extends Page {

	@Property
	private EvaluatorResult result;

	public Collection<EvaluatorResult> getResults() throws DAOException, BeenApiException {
		return api.getApi().getEvaluatorResults();
	}

	public boolean mimeTypeSupported(String mimeType) {
		if (mimeType.equals(EvaluatorResult.MIME_TYPE_HTML))
			return true;
		if (mimeType.equals(EvaluatorResult.MIME_TYPE_IMAGE_GIF))
			return true;
		if (mimeType.equals(EvaluatorResult.MIME_TYPE_IMAGE_JPEG))
			return true;
		if (mimeType.equals(EvaluatorResult.MIME_TYPE_IMAGE_PNG))
			return true;
		if (mimeType.equals(EvaluatorResult.MIME_TYPE_PLAIN))
			return true;
		return false;
	}

	Object onDeleteResult(String resultId) throws BeenApiException, InterruptedException {
		new ResultSupport(getApi()).deleteResult(resultId);
		return List.class;
	}

}
