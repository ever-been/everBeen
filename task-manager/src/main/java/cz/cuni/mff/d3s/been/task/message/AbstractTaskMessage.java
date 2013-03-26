package cz.cuni.mff.d3s.been.task.message;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

abstract class AbstractTaskMessage implements TaskMessage {

	private TaskEntry entry;

	public AbstractTaskMessage(TaskEntry entry) {
		this.entry = entry;
	}

	public TaskEntry getEntry() {
		return entry;
	}
}
