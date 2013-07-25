package cz.cuni.mff.d3s.been.web.pages.result;

import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Property;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.RESULTS_LIST)
public class Detail extends Page {

	@Property
	private EvaluatorResult result;

	void onActivate(String resultId) {
		result = this.api.getApi().getEvaluatorResult(resultId);
	}

}
