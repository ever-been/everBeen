package cz.cuni.mff.d3s.been.task.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * 
 * Factory for {@link TaskAction}s.
 * 
 * @author Martin Sixta
 */
public class Actions {

	/** Creates action which does nothing */
	public static TaskAction createNullAction() {
		return new NullAction();
	}

	/**
	 * Creates abort action.
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @param entry
	 *          {@link TaskEntry} of a task to abort
	 * @param msg
	 *          reason for the abortion of the task
	 * @return action which will abort the task
	 */
	public static TaskAction createAbortAction(final ClusterContext ctx, final TaskEntry entry, final String msg) {
		return new AbortTaskAction(ctx, entry, msg);
	}

	/**
	 * Creates actions which changes owner of the task.
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @param entry
	 *          {@link TaskEntry} of a task to change owner of
	 * @return action which will change owner of the task
	 */
	public static TaskAction createChangeOwnerTaskAction(final ClusterContext ctx, final TaskEntry entry) {
		return new ChangeOwnerTaskAction(ctx, entry);
	}

	/**
	 * Creates actions which changes owner of the task.
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @param entry
	 *          {@link TaskEntry} of a task to change owner of
	 * @return action which will change owner of the task
	 */
	public static TaskAction createScheduleTaskAction(final ClusterContext ctx, final TaskEntry entry) {
		return new ScheduleTaskAction(ctx, entry);
	}

	/**
	 * Creates actions which checks context of a task/ .
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @param entry
	 *          {@link TaskEntry} of a task to check context for
	 * @return action which will check context of the task
	 */
	public static TaskAction createTaskContextCheckerAction(final ClusterContext ctx, final TaskEntry entry) {
		return new TaskContextCheckerAction(ctx, entry);
	}

	/**
	 * Creates actions which will resubmit a benchmark task.
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @param entry
	 *          {@link TaskEntry} of a benchmark task to resubmit
	 * @return action which will resubmit the benchmark task
	 */
	public static TaskAction createResubmitBenchmarkAction(final ClusterContext ctx, final TaskEntry entry) {
		return new ResubmitBenchmarkAction(ctx, entry);
	}

	/**
	 * Creates actions which will run a task context.
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @param contextId
	 *          ID of the context to run
	 * @return action which will run the context
	 */
	public static TaskAction createRunContextAction(final ClusterContext ctx, final String contextId) {
		return new RunContextAction(ctx, contextId);

	}
}
