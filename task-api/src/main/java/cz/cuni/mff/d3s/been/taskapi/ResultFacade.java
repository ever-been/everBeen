package cz.cuni.mff.d3s.been.taskapi;

import java.util.Collection;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.results.ResultFilter;

public interface ResultFacade {
	ResultPersister createResultPersister(EntityID entityId) throws DAOException;
	void persistResult(Result result, EntityID entityId) throws DAOException;
	Collection<Result> retrieveResults(ResultFilter filter);

	/*
	Collection<String> listGroupsOfKind(String kind);
	Collection<Result> retrieveResults(EntityID entityId);
	Collection<Result> retrieveResults(EntityID entityId, Entity filter);
	*/
}
