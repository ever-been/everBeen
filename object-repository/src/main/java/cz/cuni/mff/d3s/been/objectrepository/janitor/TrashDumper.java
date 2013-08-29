package cz.cuni.mff.d3s.been.objectrepository.janitor;

import cz.cuni.mff.d3s.been.persistence.QueryBuilder;
import cz.cuni.mff.d3s.been.storage.Storage;

import static cz.cuni.mff.d3s.been.objectrepository.janitor.CleanupEntities.*;

/**
 * Assembly and execution of cleanup queries takes place here
 *
 * @author darklight
 */
class TrashDumper {

	private final Storage storage;

	TrashDumper(Storage storage) {
		this.storage = storage;
	}

	/**
	 * Clean up after a failed task context.
	 *
	 * Deletes following persistent objects associated with given task context an all of its constituting tasks:
	 *
	 * <ul>
	 *     <li>logs</li>
	 *     <li>configuration</li>
	 *     <li>results</li>
	 *     <li>evaluations</li>
	 *     <li>outcome</li>
	 * </ul>
	 *
	 * After this, literally no trace of the context are left in persistence
	 *
	 * @param contextId Context to purge
	 */
	public void cleanupAfterFailedContext(String contextId) {
		boolean canForget = true;
		canForget &= cleanupContextLogs(contextId);
		canForget &= cleanupContextConfiguration(contextId);
		canForget &= cleanupContextResults(contextId);
		canForget &= cleanupContextEval(contextId);
		if(canForget) {
			cleanupContextOutcome(contextId);
		}
	}

	/**
	 * Clean up after a successfully finished task context.
	 *
	 * Deletes following objects associated with given task context and all of its constituting tasks:
	 *
	 * <ul>
	 *     <li>logs</li>
	 *     <li>outcome</li>
	 * </ul>
	 *
	 * After this operation, only produced results are left in persistence
	 *
	 * @param contextId Context to clean
	 */
	public void cleanupAfterFinishedContext(String contextId) {
		if (cleanupContextLogs(contextId)) {
			cleanupContextOutcome(contextId);
		}
	}

	/**
	 * Clean up after a failed task.
	 *
	 * Deletes following objects associated with given task:
	 *
	 * <ul>
	 *     <li>logs</li>
	 *     <li>configuration</li>
	 *     <li>results</li>
	 *     <li>evaluations</li>
	 *     <li>outcome</li>
	 * </ul>
	 *
	 * No trace is left over of given task after this operation is completed
	 *
	 * @param taskId Task to purge
	 */
	public void cleanupAfterFailedTask(String taskId) {
		boolean canForget = true;
		canForget &= cleanupTaskLogs(taskId);
		canForget &= cleanupTaskConfiguration(taskId);
		canForget &= cleanupTaskResults(taskId);
		canForget &= cleanupTaskEval(taskId);
		if (canForget) {
			cleanupTaskOutcome(taskId);
		}
	}

	/**
	 * Clean up after a successful task.
	 *
	 * Deletes following objects associated with given task:
	 *
	 * <ul>
	 *     <li>logs</li>
	 *     <li>outcome</li>
	 * </ul>
	 *
	 * @param taskId Task to clean
	 */
	public void cleanupAfterFinishedTask(String taskId) {
		if (cleanupTaskLogs(taskId)) {
			cleanupTaskOutcome(taskId);
		}
	}

	private boolean cleanupTaskLogs(String taskId) {
		return storage.query(new QueryBuilder().on(LOG.getId()).with("taskId", taskId).delete()).getStatus().isOk();
	}

	private boolean cleanupTaskOutcome(String taskId) {
		return storage.query(new QueryBuilder().on(OUTCOME.getId()).with("taskId", taskId).delete()).getStatus().isOk();
	}

	private boolean cleanupTaskConfiguration(String taskId) {
		return storage.query(new QueryBuilder().on(CONFIGURATION.getId()).with("taskId", taskId).delete()).getStatus().isOk();
	}

	private boolean cleanupTaskResults(String taskId) {
		return storage.query(new QueryBuilder().on(RESULT.getId()).with("taskId", taskId).delete()).getStatus().isOk();
	}

	private boolean cleanupTaskEval(String taskId) {
		return storage.query(new QueryBuilder().on(EVALUATION.getId()).with("taskId", taskId).delete()).getStatus().isOk();
	}

	private boolean cleanupContextLogs(String contextId) {
		return storage.query(new QueryBuilder().on(LOG.getId()).with("contextId", contextId).delete()).getStatus().isOk();
	}

	private boolean cleanupContextOutcome(String contextId) {
		return storage.query(new QueryBuilder().on(OUTCOME.getId()).with("contextId", contextId).delete()).getStatus().isOk();
	}

	private boolean cleanupContextConfiguration(String contextId) {
		return storage.query(new QueryBuilder().on(CONFIGURATION.getId()).with("contextId", contextId).delete()).getStatus().isOk();
	}

	private boolean cleanupContextResults(String contextId) {
		return storage.query(new QueryBuilder().on(RESULT.getId()).with("contextId", contextId).delete()).getStatus().isOk();
	}

	private boolean cleanupContextEval(String contextId) {
		return storage.query(new QueryBuilder().on(EVALUATION.getId()).with("contextId", contextId).delete()).getStatus().isOk();
	}
}
