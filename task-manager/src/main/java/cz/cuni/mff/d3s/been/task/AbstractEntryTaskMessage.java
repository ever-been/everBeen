package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

abstract class AbstractEntryTaskMessage implements TaskMessage {

	private TaskEntry entry;

	public AbstractEntryTaskMessage(TaskEntry entry) {
		this.entry = entry;
	}

	public TaskEntry getEntry() {
		return entry;
	}
}
