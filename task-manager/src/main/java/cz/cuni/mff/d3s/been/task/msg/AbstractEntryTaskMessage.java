package cz.cuni.mff.d3s.been.task.msg;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

public abstract class AbstractEntryTaskMessage implements TaskMessage {

	private TaskEntry entry;

	public AbstractEntryTaskMessage(TaskEntry entry) {
		this.entry = entry;
	}

	public TaskEntry getEntry() {
		return entry;
	}
}
