package cz.cuni.mff.d3s.been.manager.msg;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * Abstract {@link TaskMessage} which can be used for implementing messages.
 * 
 * @author Martin Sixta
 */
abstract class AbstractEntryTaskMessage implements TaskMessage {

	private TaskEntry entry;

	/**
	 * Creates new AbstractEntryTaskMessage
	 * 
	 * @param entry
	 *          targeted entry
	 */
	protected AbstractEntryTaskMessage(TaskEntry entry) {
		this.entry = entry;
	}

	/**
	 * Returns the associated entry.
	 * 
	 * @return the associated entry
	 */
	public TaskEntry getEntry() {
		return entry;
	}
}
