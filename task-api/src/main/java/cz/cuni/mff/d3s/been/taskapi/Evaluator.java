package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tasks which stores Evaluator data to the persistent storage.
 *
 * @author Kuba Brecka
 */
public abstract class Evaluator extends Task {

	/**
	 * logging
	 */
	private static final Logger log = LoggerFactory.getLogger(Evaluator.class);


	/**
	 * Method which is responsible for generating {@link EvaluatorResult}.
	 * <p/>
	 * The result will be stored to appropriate persistent storage on behalf of this {@link Evaluator}.
	 *
	 * @return result to be stored to persistent storage
	 * @throws TaskException
	 * @throws MessagingException
	 * @throws DAOException
	 */
	protected abstract EvaluatorResult evaluate() throws TaskException, MessagingException, DAOException;

	@Override
	public void run(String[] args) throws TaskException, MessagingException, DAOException {
		EvaluatorResult evaluatorResult = evaluate();

		if (evaluatorResult == null) {
			log.warn("Evaluator returned no data!");
			return;
		}

		final EntityID evaluatorId = Entities.RESULT_EVALUATOR.getId();

		try (final ResultPersister rp = results.createResultPersister(evaluatorId)) {
			rp.persist(evaluatorResult);
		}
	}
}
