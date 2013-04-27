package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kuba Brecka
 */
public class TaskContextCheckerAction implements TaskAction {
	private static final Logger log = LoggerFactory.getLogger(TaskContextCheckerAction.class);

	private final ClusterContext ctx;
	private final TaskEntry entry;

	public TaskContextCheckerAction(ClusterContext ctx, TaskEntry entry) {
		this.ctx = ctx;
		this.entry = entry;
	}

	@Override
	public void execute() throws TaskActionException {
		String taskContextId = entry.getTaskContextId();
		TaskContextEntry taskContextEntry = ctx.getTaskContextsUtils().getTaskContext(taskContextId);

		boolean allTasksFinished = true;
		for (String taskId : taskContextEntry.getContainedTask()) {
			TaskEntry taskEntry = ctx.getTasksUtils().getTask(taskId);

			if (taskEntry.getState() != TaskState.FINISHED) {
				allTasksFinished = false;
				break;
			}
		}

		if (! allTasksFinished) return;

		ctx.getTaskContextsUtils().cleanupTaskContext(taskContextEntry);
	}
}
