package cz.cuni.mff.d3s.been.task.message;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * @author Martin Sixta
 */
final public class UpdatedTaskMessage extends AbstractTaskMessage {
	public UpdatedTaskMessage(TaskEntry entry) {
		super(entry);
	}
}
