package cz.cuni.mff.d3s.been.task.msg;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.core.task.TaskType;
import cz.cuni.mff.d3s.been.task.action.ResubmitBenchmarkAction;
import cz.cuni.mff.d3s.been.task.action.ScheduleTaskAction;
import cz.cuni.mff.d3s.been.task.action.TaskAction;
import cz.cuni.mff.d3s.been.task.action.TaskContextCheckerAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin Sixta
 */
final class TaskChangedMessage extends AbstractEntryTaskMessage {

	private static final Logger log = LoggerFactory.getLogger(TaskChangedMessage.class);

	public TaskChangedMessage(TaskEntry entry) {
		super(entry);
	}

	@Override
	public TaskAction createAction(ClusterContext ctx) {
		TaskState state = this.getEntry().getState();

		if (state == TaskState.SUBMITTED || state == TaskState.WAITING)
			return new ScheduleTaskAction(ctx, getEntry());

		if (this.getEntry().getTaskDescriptor().getType() == TaskType.BENCHMARK) {
			if (state == TaskState.ABORTED) {
				// a benchmark generator task has failed
				log.info("BENCHMARK GENERATOR TASK ID {} FAILED", this.getEntry().getId());
				return new ResubmitBenchmarkAction(ctx, getEntry());
			}
		}

		if (state == TaskState.FINISHED || state == TaskState.ABORTED) {
			return new TaskContextCheckerAction(ctx, getEntry());
		}

		return null;
	}
}
