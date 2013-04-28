package cz.cuni.mff.d3s.been.taskapi.results;

import java.util.Collection;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.results.ResultFilter;

public interface ResultFacade {
	ResultPersister createResultPersister(EntityID entityId);
	void persistResult(Result result, EntityID entityId) throws DAOException;
	Collection<Result> retrieveResults(ResultFilter filter);
}
