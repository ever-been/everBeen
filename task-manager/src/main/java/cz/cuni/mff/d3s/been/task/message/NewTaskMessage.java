package cz.cuni.mff.d3s.been.task.message;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * @author Martin Sixta
 */
public final class NewTaskMessage extends AbstractTaskMessage {

	public NewTaskMessage(TaskEntry entry) {
		super(entry);
	}
}
