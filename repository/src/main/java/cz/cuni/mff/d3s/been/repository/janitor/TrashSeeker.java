package cz.cuni.mff.d3s.been.repository.janitor;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.task.TaskContextState;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.persistence.QueryAnswer;
import cz.cuni.mff.d3s.been.persistence.QueryBuilder;
import cz.cuni.mff.d3s.been.persistence.task.PersistentContextState;
import cz.cuni.mff.d3s.been.persistence.task.PersistentTaskState;
import cz.cuni.mff.d3s.been.storage.Storage;
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

	private final Storage storage;
	private final JSONUtils jsonUtils;
	private final Collection<PersistentTaskState> dummyFailTaskStateCollection;
	private final Collection<PersistentContextState> dummyFailContextStateCollection;

	TrashSeeker(Storage storage) {
		this.storage = storage;
		this.jsonUtils = JSONUtils.newInstance();
		this.dummyFailTaskStateCollection = new ArrayList<PersistentTaskState>(0);
		this.dummyFailContextStateCollection = new ArrayList<PersistentContextState>(0);
	}

	/**
	 * Lookup failed tasks that need cleanup
	 *
	 * @param when Timestamp past which the tasks need to be cleaned up
	 *
	 * @return A list of failed tasks
	 */
	Collection<PersistentTaskState> getFailedTasksPastDue(Long when) {
		final QueryAnswer qAnswer = storage.query(new QueryBuilder()
						.on(Entities.OUTCOME_TASK.getId())
						.with("taskState").equal(TaskState.ABORTED.name())
						.with("contextId").differentFrom(Names.BENCHMARKS_CONTEXT_ID)
						.with("created").below(when)
						.fetch());

		if (checkDataAnswer(qAnswer)) {
			return dummyFailTaskStateCollection;
		}

		try {
			return jsonUtils.deserialize(qAnswer.getData(), PersistentTaskState.class);
		} catch (JsonException e) {
			log.warn("Cannot deserialize task list for cleanup", e);
			return dummyFailTaskStateCollection;
		}
	}

	/**
	 * Lookup finished tasks that need cleanup
	 *
	 * @param when Timestamp past which the tasks need to be cleaned up
	 *
	 * @return A list of finished tasks
	 */
	Collection<PersistentTaskState> getFinishedTasksPastDue(Long when) {
		final QueryAnswer qAnswer = storage.query(new QueryBuilder()
				.on(Entities.OUTCOME_TASK.getId())
				.with("taskState").equal(TaskState.FINISHED.name())
				.with("contextId").differentFrom(Names.BENCHMARKS_CONTEXT_ID)
				.with("created").below(when)
				.fetch());

		if (checkDataAnswer(qAnswer)) {
			return dummyFailTaskStateCollection;
		}

		try {
			return jsonUtils.deserialize(qAnswer.getData(), PersistentTaskState.class);
		} catch (JsonException e) {
			log.warn("Cannot load finished task list for cleanup", e);
			return dummyFailTaskStateCollection;
		}
	}

	/**
	 * Lookup starting states to tasks past due, which helps find tasks in zombie state
	 *
	 * @param when Timestamp past which to load starting states of tasks
	 *
	 * @return A list of started tasks
	 */
	Collection<PersistentTaskState> getStartedTasksPastDue(Long when) {
		final QueryAnswer queryAnswer = storage.query(new QueryBuilder()
				.on(Entities.OUTCOME_TASK.getId())
				.with("taskState").equal(TaskState.RUNNING.name())
				.with("contextId").differentFrom(Names.BENCHMARKS_CONTEXT_ID)
				.with("created").below(when)
				.fetch());

		if (!checkDataAnswer(queryAnswer)) {
			return dummyFailTaskStateCollection;
		}

		try {
			return jsonUtils.deserialize(queryAnswer.getData(), PersistentTaskState.class);
		} catch (JsonException e) {
			log.warn("Cannot load started task list for zombie detection");
			return dummyFailTaskStateCollection;
		}
	}

	/**
	 * Lookup failed task contexts that need cleanup
	 *
	 * @param when Timestamp pas which the contexts need to be cleaned up
	 *
	 * @return A list of failed contexts
	 */
	Collection<PersistentContextState> getFailedContextsPastDue(Long when) {
		final QueryAnswer qAnswer = storage.query(new QueryBuilder()
				.on(Entities.OUTCOME_CONTEXT.getId())
				.with("contextState").equal(TaskContextState.FAILED.name())
				.with("created").below(when)
				.fetch());

		if (!checkDataAnswer(qAnswer)) {
			return dummyFailContextStateCollection;
		}

		try {
			return jsonUtils.deserialize(qAnswer.getData(), PersistentContextState.class);
		} catch (JsonException e) {
			log.warn("Cannot load failed task context list for cleanup", e);
			return dummyFailContextStateCollection;
		}
	}

	/**
	 * Lookup finished task contexts that need cleanup
	 *
	 * @param when Timestamp pas which the contexts need to be cleaned up
	 *
	 * @return A list of finished contexts
	 */
	Collection<PersistentContextState> getFinishedContextsPastDue(Long when) {
		final QueryAnswer qAnswer = storage.query(new QueryBuilder()
				.on(Entities.OUTCOME_CONTEXT.getId())
				.with("contextState").equal(TaskContextState.FINISHED.name())
				.with("created").below(when)
				.fetch());

		if (!checkDataAnswer(qAnswer)) {
			return dummyFailContextStateCollection;
		}

		try {
			return jsonUtils.deserialize(qAnswer.getData(), PersistentContextState.class);
		} catch (JsonException e) {
			log.warn("Cannot load finished task context list for cleanup", e);
			return dummyFailContextStateCollection;
		}
	}

	/**
	 * Lookup started contexts past a timestamp. Helps zombie context detection.
	 *
	 * @param when Timestamp past which to look for started contexts
	 *
	 * @return A list of started contexts
	 */
	Collection<PersistentContextState> getStartedContextsPastDue(Long when) {
		final QueryAnswer qAnswer = storage.query(new QueryBuilder()
				.on(Entities.OUTCOME_CONTEXT.getId())
				.with("contextState").equal(TaskContextState.RUNNING.name())
				.with("created").below(when)
				.fetch());

		if (!checkDataAnswer(qAnswer)) {
			return dummyFailContextStateCollection;
		}

		try {
			return jsonUtils.deserialize(qAnswer.getData(), PersistentContextState.class);
		} catch (JsonException e) {
			log.warn("Cannot load started task context list for cleanup", e);
			return dummyFailContextStateCollection;
		}
	}

	/**
	 * Lookup failed benchmarks that need cleanup
	 *
	 * @param when Timestamp past which the benchmarks need to be cleaned up
	 *
	 * @return A list of failed benchmarks
	 */
	Collection<PersistentTaskState> getFailedBenchmarksPastDue(Long when) {
		final QueryAnswer qAnswer = storage.query(new QueryBuilder()
				.on(Entities.OUTCOME_TASK.getId())
				.with("taskState").equal(TaskState.ABORTED.name())
				.with("contextId").equal(Names.BENCHMARKS_CONTEXT_ID)
				.with("created").below(when)
				.fetch());

		if (!checkDataAnswer(qAnswer)) {
			return dummyFailTaskStateCollection;
		}

		try {
			return jsonUtils.deserialize(qAnswer.getData(), PersistentTaskState.class);
		} catch (JsonException e) {
			log.warn("Cannot load failed benchmark list for cleanup", e);
			return dummyFailTaskStateCollection;
		}
	}

	/**
	 * Lookup finished benchmarks that need cleanup
	 *
	 * @param when Timestamp past which the benchmarks need to be cleaned up
	 *
	 * @return A list of finished benchmarks
	 */
	Collection<PersistentTaskState> getFinishedBenchmarksPastDue(Long when) {
		final QueryAnswer qAnswer = storage.query(new QueryBuilder()
				.on(Entities.OUTCOME_TASK.getId())
				.with("taskState").equal(TaskState.FINISHED.name())
				.with("contextId").equal(Names.BENCHMARKS_CONTEXT_ID)
				.with("created").below(when)
				.fetch());

		if (!checkDataAnswer(qAnswer)) {
			return dummyFailTaskStateCollection;
		}

		try {
			return jsonUtils.deserialize(qAnswer.getData(), PersistentTaskState.class);
		} catch (JsonException e) {
			log.warn("Cannot load finished benchmark list for cleanup", e);
			return dummyFailTaskStateCollection;
		}
	}

	/**
	 * Get started benchmarks past a certain timestamp. Helps with zombie benchmark detection
	 *
	 * @param when Timestamp past which the benchmarks need t obe cleaned up
	 *
	 * @return A list of started benchmarks
	 */
	Collection<PersistentTaskState> getStartedBenchmarksPastDue(Long when) {
		final QueryAnswer qAnswer = storage.query(new QueryBuilder()
				.on(Entities.OUTCOME_TASK.getId())
				.with("taskState").equal(TaskState.RUNNING.name())
				.with("contextId").equal(Names.BENCHMARKS_CONTEXT_ID)
				.with("created").below(when)
				.fetch());

		if (!checkDataAnswer(qAnswer)) {
			return dummyFailTaskStateCollection;
		}

		try {
			return jsonUtils.deserialize(qAnswer.getData(), PersistentTaskState.class);
		} catch (JsonException e) {
			log.warn("Cannot load started benchmark list for cleanup", e);
			return dummyFailTaskStateCollection;
		}
	}

	private boolean checkDataAnswer(QueryAnswer qAnswer) {
		if (!qAnswer.getStatus().isOk()) {
			log.warn("Cannot refresh failed event list: {}", qAnswer.getStatus().getDescription());
			return false;
		}

		if (!qAnswer.isCarryingData()) {
			// should never get here, but just in case something changes in the future...
			log.warn("Loaded failed event list carries no data: {}", qAnswer.getStatus().getDescription());
			return false;
		}

		return true;
	}
}
