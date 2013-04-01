package cz.cuni.mff.d3s.been.task.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.task.message.TaskMessage;

/**
 * @author Martin Sixta
 */
final class UpdatedTaskAction extends AbstractTaskAction {
	private static Logger log = LoggerFactory.getLogger(UpdatedTaskAction.class);

	public UpdatedTaskAction(ClusterContext ctx, TaskMessage msg) {
		super(ctx, msg);
	}

	@Override
	public void execute() {
		TaskEntry entry = msg.getEntry();

		String taskId = entry.getId();

		switch (entry.getState()) {
			case ABORTED:
				log.info("Task has been ABORTED: " + taskId);
				break;
			case SUBMITTED:
				if (!entry.getRuntimeId().equals("0")) {
					//timeout exceeded, stale task
					// what now?
				} else {
					//
				}

				break;

		}

		log.info("Entry updated " + taskId);

	}
}
