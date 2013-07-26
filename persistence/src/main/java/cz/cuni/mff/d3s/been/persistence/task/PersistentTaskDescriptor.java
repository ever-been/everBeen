package cz.cuni.mff.d3s.been.persistence.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.persistence.TaskEntity;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

/**
 * Wrapper that enables persistence of {@link cz.cuni.mff.d3s.been.core.task.TaskDescriptor}
 *
 * @author darklight
 */
class PersistentTaskDescriptor extends TaskEntity {
	private TaskDescriptor descriptor;
    private BpkIdentifier bpkId;

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

    /**
     * Set the BPK ID
     *
     * @param bpkId BPK ID to associate with this descriptor
     */
    public void setBpkId(BpkIdentifier bpkId) {
        this.bpkId = bpkId;
    }

    /**
     * Get the BPK ID
     *
     * @return The BPK ID
     */
    public BpkIdentifier getBpkId() {
        return bpkId;
    }
}
