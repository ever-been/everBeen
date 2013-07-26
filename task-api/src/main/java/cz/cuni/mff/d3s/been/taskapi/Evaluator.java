package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * @author Kuba Brecka
 */
public abstract class Evaluator extends Task {

	private static final Logger log = LoggerFactory.getLogger(Evaluator.class);

	public abstract EvaluatorResult evaluate();

	@Override
	public void run(String[] args) {
		EvaluatorResult evaluatorResult = evaluate();

		try {
			ResultPersister rp = results.createResultPersister(Entities.RESULT_EVALUATOR.getId());
			rp.persist(evaluatorResult);
			rp.close();
		} catch (DAOException e) {
			log.error("Cannot store evaluation result.", e);
		}
	}
}
