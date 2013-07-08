package cz.cuni.mff.d3s.been.taskapi.results;

import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.results.Result;

public interface ResultPersister {
	void persist(Result result) throws DAOException;
}
