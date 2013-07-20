package cz.cuni.mff.d3s.been.core.task;

/**
 * Named persistent wrapper for this task descriptor
 *
 * @author darklight
 */
class NamedPersistentTaskDescriptor extends PersistentTaskDescriptor {

	/**
	 * Name with which this {@link PersistentTaskDescriptor} will be persisted
	 */
	private String name;

	/**
	 * Set the name under which to persist the descriptor
	 *
	 * @param name Persist name for this descriptor
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the name under which the descriptor will be persisted
	 *
	 * @return The persist name of this descriptor
	 */
	public String getName() {
		return name;
	}
}
