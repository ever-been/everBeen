package cz.cuni.mff.d3s.been.taskapi;

import java.util.Collection;

import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.results.ResultContainerId;
import cz.cuni.mff.d3s.been.results.ResultFilter;
import cz.cuni.mff.d3s.been.taskapi.results.ResultFacade;
import cz.cuni.mff.d3s.been.taskapi.results.ResultPersister;

/**
 * Task's internal wrapper that enables us to make the {@link ResultFacade}
 * field final for the {@link Task} implementor not to tamper with it.
 * 
 * @author darklight
 * 
 */
class TaskFieldResultFacadeWrapper implements ResultFacade {

	private ResultFacade results;

	void setResultFacade(ResultFacade results) {
		this.results = results;
	}

	@Override
	public ResultPersister createResultPersister(ResultContainerId containerId) {
		checkInitialized();
		return results.createResultPersister(containerId);
	}

	@Override
	public void persistResult(Result result, ResultContainerId containerId) throws DAOException {
		checkInitialized();
		results.persistResult(result, containerId);
	}

	@Override
	public Collection<Result> retrieveResults(ResultFilter filter) {
		checkInitialized();
		return results.retrieveResults(filter);
	}

	private void checkInitialized() {
		if (results == null) {
			throw new IllegalStateException("The results facade is not yet initialized. Use it only in the main method.");
		}
	}
}