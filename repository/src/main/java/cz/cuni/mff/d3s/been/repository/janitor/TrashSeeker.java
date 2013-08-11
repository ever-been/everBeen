package cz.cuni.mff.d3s.been.repository.janitor;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.task.TaskContextState;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.QueryBuilder;
import cz.cuni.mff.d3s.been.persistence.task.PersistentContextState;
import cz.cuni.mff.d3s.been.persistence.task.PersistentTaskState;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Looks up trash that needs taking out
 *
 * @author darklight
 */
class TrashSeeker {

	private static final Logger log = LoggerFactory.getLogger(TrashSeeker.class);

	private final ClusterContext ctx;
	private final JSONUtils jsonUtils;

	TrashSeeker(ClusterContext ctx) {
		this.ctx = ctx;
		this.jsonUtils = JSONUtils.newInstance();
	}

	/**
	 * Lookup failed tasks that need cleanup
	 *
	 * @return A list of failed tasks
	 */
	Collection<PersistentTaskState> getFailedTasksPastDue() {
		try {
			return jsonUtils.deserialize(
					ctx.getPersistence().query(
							new QueryBuilder()
									.on(Entities.OUTCOME_TASK.getId())
									.with("taskState").equal(TaskState.ABORTED)
									.with("contextId").differentFrom(Names.BENCHMARKS_CONTEXT_ID)
									.fetch()).getData(), PersistentTaskState.class);
		} catch (DAOException | JsonException e) {
			log.warn("Cannot load failed task list for cleanup", e);
			return new ArrayList<PersistentTaskState>(0);
		}
	}

	/**
	 * Lookup finished tasks that need cleanup
	 *
	 * @return A list of finished tasks
	 */
	Collection<PersistentTaskState> getFinishedTasksPastDue() {
		try {
			return jsonUtils.deserialize(
					ctx.getPersistence().query(
							new QueryBuilder()
									.on(Entities.OUTCOME_TASK.getId())
									.with("taskState").equal(TaskState.FINISHED)
									.with("contextId").differentFrom(Names.BENCHMARKS_CONTEXT_ID)
									.fetch()).getData(), PersistentTaskState.class);
		} catch (DAOException | JsonException e) {
			log.warn("Cannot load finished task list for cleanup", e);
			return new ArrayList<PersistentTaskState>(0);
		}
	}

	/**
	 * Lookup failed task contexts that need cleanup
	 *
	 * @return A list of failed contexts
	 */
	Collection<PersistentContextState> getFailedContextsPastDue() {
		try {
			return jsonUtils.deserialize(
					ctx.getPersistence().query(
							new QueryBuilder()
									.on(Entities.OUTCOME_CONTEXT.getId())
									.with("contextState").equal(TaskContextState.FAILED)
									.fetch()).getData(), PersistentContextState.class);
		} catch (DAOException | JsonException e) {
			log.warn("Cannot load failed task context list for cleanup", e);
			return new ArrayList<PersistentContextState>(0);
		}
	}

	/**
	 * Lookup finished task contexts that need cleanup
	 *
	 * @return A list of finished contexts
	 */
	Collection<PersistentContextState> getFinishedContextsPastDue() {
		try {
			return jsonUtils.deserialize(
					ctx.getPersistence().query(
							new QueryBuilder()
									.on(Entities.OUTCOME_CONTEXT.getId())
									.with("contextState").equal(TaskContextState.FINISHED)
									.fetch()).getData(), PersistentContextState.class);
		} catch (DAOException | JsonException e) {
			log.warn("Cannot load finished task contextlist for cleanup", e);
			return new ArrayList<PersistentContextState>(0);
		}
	}

	/**
	 * Lookup failed benchmarks that need cleanup
	 *
	 * @return A list of failed benchmarks
	 */
	Collection<PersistentTaskState> getFailedBenchmarksPastDue() {
		try {
			return jsonUtils.deserialize(
					ctx.getPersistence().query(
							new QueryBuilder()
									.on(Entities.OUTCOME_TASK.getId())
									.with("taskState").equal(TaskState.ABORTED)
									.with("contextId").equal(Names.BENCHMARKS_CONTEXT_ID)
									.fetch()).getData(), PersistentTaskState.class);
		} catch (DAOException | JsonException e) {
			log.warn("Cannot load failed benchmark list for cleanup", e);
			return new ArrayList<PersistentTaskState>(0);
		}
	}

	/**
	 * Lookup finished benchmarks that need cleanup
	 *
	 * @return A list of finished benchmarks
	 */
	Collection<PersistentTaskState> getFinishedBenchmarksPastDue() {
		try {
			return jsonUtils.deserialize(
					ctx.getPersistence().query(
							new QueryBuilder()
									.on(Entities.OUTCOME_TASK.getId())
									.with("taskState").equal(TaskState.FINISHED)
									.with("contextId").equal(Names.BENCHMARKS_CONTEXT_ID)
									.fetch()).getData(), PersistentTaskState.class);
		} catch (DAOException | JsonException e) {
			log.warn("Cannot load finished benchmark list for cleanup", e);
			return new ArrayList<PersistentTaskState>(0);
		}
	}
}
