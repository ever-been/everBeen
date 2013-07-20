package cz.cuni.mff.d3s.been.persistence.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;

/**
 * A wrapper that enables persistence of {@link cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor}
 *
 * @author darklight
 */
class PersistentContextDescriptor extends Entity {
	private TaskContextDescriptor descriptor;
    private BpkIdentifier bpkId;

	/**
	 * Set the task context descriptor
	 *
	 * @param descriptor {@link TaskContextDescriptor} to set
	 */
	public void setDescriptor(TaskContextDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * Get the task context descriptor
	 *
	 * @return The {@link TaskContextDescriptor}
	 */
	public TaskContextDescriptor getDescriptor() {
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
