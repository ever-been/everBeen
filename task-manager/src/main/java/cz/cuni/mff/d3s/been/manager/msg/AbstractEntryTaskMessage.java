package cz.cuni.mff.d3s.been.manager.msg;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * Abstract {@link TaskMessage} which can be used for implementing messages.
 * 
 * @author Martin Sixta
 */
public abstract class AbstractEntryTaskMessage implements TaskMessage {

	private TaskEntry entry;

	public AbstractEntryTaskMessage(TaskEntry entry) {
		this.entry = entry;
	}

	public TaskEntry getEntry() {
		return entry;
	}
}
