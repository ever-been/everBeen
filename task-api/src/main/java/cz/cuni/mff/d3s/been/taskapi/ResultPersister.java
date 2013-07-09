package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.results.Result;

public interface ResultPersister extends AutoCloseable{
	void persist(Result result) throws DAOException;
	void close();
}
