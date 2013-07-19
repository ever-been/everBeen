package cz.cuni.mff.d3s.been.core.task;

import cz.cuni.mff.d3s.been.core.persistence.Entity;

/**
 * Wrapper that enables persistence of {@link TaskDescriptor}
 *
 * @author darklight
 */
class PersistentTaskDescriptor extends Entity {
	private TaskDescriptor descriptor;

	/**
	 * Set the task descriptor
	 *
	 * @param descriptor {@link TaskDescriptor} to set
	 */
	public void setDescriptor(TaskDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * Get the task descriptor
	 *
	 * @return The {@link TaskDescriptor}
	 */
	public TaskDescriptor getDescriptor() {
		return descriptor;
	}
}
