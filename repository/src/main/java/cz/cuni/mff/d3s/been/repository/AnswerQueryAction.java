package cz.cuni.mff.d3s.been.repository;

import com.hazelcast.core.IMap;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.SuccessAction;
import cz.cuni.mff.d3s.been.storage.Storage;

/**
 * An action that answers a query (finds the response and shares it across the cluster).s
 */
public class AnswerQueryAction implements SuccessAction<Query> {

	private final Storage storage;
	private final IMap<Object, Object> answers;

	/**
	 * Create an answer action over a persistent storage and a map of answers
	 *
	 * @param storage Storage to query (provides answers)
	 * @param answers Map to store retrieved answers
	 */
	AnswerQueryAction(Storage storage, IMap<Object, Object> answers) {
		this.storage = storage;
		this.answers = answers;
	}

	@Override
	public void perform(Query query) throws DAOException {
		answers.put(query.getId(), storage.query(query));
	}
}
