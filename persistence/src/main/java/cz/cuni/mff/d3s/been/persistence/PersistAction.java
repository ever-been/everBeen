package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.persistence.DAOException;

/**
 * An action that specifies what should be done with an item once it's decided
 * that it should be persisted.
 * 
 * @author darklight
 * 
 * @param <T>
 */
public interface PersistAction<T> {
	void persist(T what) throws DAOException;
}
