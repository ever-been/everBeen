package cz.cuni.mff.d3s.been.persistence.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.persistence.NamedEntity;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;

/**
 * Persistent named wrapper for {@link TaskContextDescriptor}
 *
 * @author darklight
 */
class NamedPersistentContextDescriptor extends NamedEntity {

    private TaskContextDescriptor descriptor;
    private BpkIdentifier bpkId;

    /**
     * Set carried context descriptor
     *
     * @param descriptor Descriptor to set
     */
    public void setDescriptor(TaskContextDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Get carried context descriptor
     *
     * @return The context descriptor
     */
    public TaskContextDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Get the ID of the BPK to which this named descriptor belongs
     *
     * @return The BPK ID
     */
    BpkIdentifier getBpkId() {
        return bpkId;
    }

    /**
     * Set the ID of the BPK for which this named descriptor was created
     *
     * @param bpkId The BPK ID
     */
    void setBpkId(BpkIdentifier bpkId) {
        this.bpkId = bpkId;
    }
}
