package cz.cuni.mff.d3s.been.resultsrepository.storage;

import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.ResultContainerId;

public interface Storage {
	void start() throws StorageException;
	void stop();
	void storeResult(ResultContainerId containerId, String JSON) throws DAOException;
}
