package cz.cuni.mff.d3s.been.taskapi.results;

import java.util.Collection;

import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.results.ResultContainerId;
import cz.cuni.mff.d3s.been.results.ResultFilter;

public interface ResultFacade {
	ResultPersister createResultPersister(ResultContainerId containerId);
	void persistResult(Result result, ResultContainerId containerId) throws DAOException;
	Collection<Result> retrieveResults(ResultFilter filter);
}
