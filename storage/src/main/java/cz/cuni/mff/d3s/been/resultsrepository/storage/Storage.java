package cz.cuni.mff.d3s.been.resultsrepository.storage;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.results.DAOException;

public interface Storage {
	void start() throws StorageException;
	void stop();
	void store(EntityID entityId, String JSON) throws DAOException;
}
