package cz.cuni.mff.d3s.been.web.pages.result;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.persistence.DAOException;
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

    @Property
    private String resultId;

    void onActivate(String resultId) throws DAOException, BeenApiException {
        this.resultId = resultId;
        result = this.api.getApi().getEvaluatorResult(resultId);
    }

    Object onPassivate() {
        return resultId;
    }

}
