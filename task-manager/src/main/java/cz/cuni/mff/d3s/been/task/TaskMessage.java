package cz.cuni.mff.d3s.been.task;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * @author Martin Sixta
 */
public abstract class TaskMessage implements Serializable {
	protected final TaskEntry entry;

	public TaskMessage(TaskEntry entry) {
		this.entry = entry;
	}

	public TaskEntry getEntry() {
		return entry;
	}
}

class NewTaskMessage extends TaskMessage {

	public NewTaskMessage(TaskEntry entry) {
		super(entry);
	}
}

class UpdatedTaskMessage extends TaskMessage {
	public UpdatedTaskMessage(TaskEntry entry) {
		super(entry);
	}
}
