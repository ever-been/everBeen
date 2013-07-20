package cz.cuni.mff.d3s.been.core.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.persistence.Entity;

/**
 * A wrapper that enables persistence of {@link TaskContextDescriptor}
 *
 * @author darklight
 */
class PersistentContextDescriptor extends Entity {
	private TaskContextDescriptor contextDescriptor;
    private BpkIdentifier bpkId;

	/**
	 * Set the task context descriptor
	 *
	 * @param contextDescriptor {@link TaskContextDescriptor} to set
	 */
	public void setContextDescriptor(TaskContextDescriptor contextDescriptor) {
		this.contextDescriptor = contextDescriptor;
	}

	/**
	 * Get the task context descriptor
	 *
	 * @return The {@link TaskContextDescriptor}
	 */
	public TaskContextDescriptor getContextDescriptor() {
		return contextDescriptor;
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
