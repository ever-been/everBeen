package cz.cuni.mff.d3s.been.task.action;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.TaskContexts;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextState;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * @author Kuba Brecka
 */
public class TaskContextCheckerAction implements TaskAction {
	/** logging */
	private static final Logger log = LoggerFactory.getLogger(TaskContextCheckerAction.class);

	/** connection to the cluster */
	private final ClusterContext ctx;

	/** task entry used for the context check */
	private final TaskEntry entry;

	/** format of sql query predicate for finding all unfinished tasks */
	private static final String QUERY_FORMAT = "taskContextId = '%s' AND (state == %s)";

	/**
	 * Creates new context checker action.
	 */
	public TaskContextCheckerAction(ClusterContext ctx, TaskEntry entry) {
		this.ctx = ctx;
		this.entry = entry;
	}

	/**
	 * Checks if the finished context of the task is completely finished (all
	 * tasks from it are finished or aborted). If yes, calls the context cleanup
	 * procedure.
	 * 
	 * Serialization for multiply cluster members is ensured by cluster locks.
	 * 
	 * @throws TaskActionException
	 */
	@Override
	public void execute() throws TaskActionException {
		final String taskContextId = entry.getTaskContextId();
		final TaskContexts contexts = ctx.getTaskContexts();

		IMap<String, TaskContextEntry> contextsMap = ctx.getTaskContexts().getTaskContextsMap();
		IMap<String, TaskEntry> tasksMap = ctx.getTasks().getTasksMap();

		// fetch the entry
		TaskContextEntry contextEntry = contexts.getTaskContext(taskContextId);

		// optimization: fist check, and if needed then lock and check again
		if (contextEntry.getContextState() == TaskContextState.FINISHED) {
			return;
		}

		// benchmarks context should not be destroyed at all
		if (contextEntry.isLingering()) {
			return;
		}

		try {
			contextsMap.lock(taskContextId); // LOCK BEGIN

			// must fetch again
			contextEntry = contexts.getTaskContext(taskContextId);

			if (contextEntry.getContextState() == TaskContextState.FINISHED) {
				return;
			}

			Collection<TaskEntry> values = tasksMap.values(getPredicate(taskContextId));

			boolean isFinished = (values.size() == contextEntry.getContainedTask().size());

			if (isFinished) {
				contextEntry.setContextState(TaskContextState.FINISHED);
				ctx.getTaskContexts().cleanupTaskContext(contextEntry);
			}
		} finally {
			contextsMap.unlock(taskContextId); // LOCK END
		}

	}

	/**
	 * Creates SqlPredicate for finding all unfinished tasks.
	 * 
	 * @param contextId
	 *          ID of the contexts tasks belong to
	 * 
	 * @return SqlPredicate for finding all unfinished tasks from the specified
	 *         context
	 */
	private SqlPredicate getPredicate(String contextId) {
		String sql = String.format(QUERY_FORMAT, contextId, TaskState.FINISHED);

		return new SqlPredicate(sql);
	}
}
