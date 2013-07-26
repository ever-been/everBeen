package cz.cuni.mff.d3s.been.persistence.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.persistence.NamedEntity;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

/**
 * Named persistent wrapper for the task descriptor
 *
 * @author darklight
 */
class NamedPersistentTaskDescriptor extends NamedEntity {

    private TaskDescriptor descriptor;
    private BpkIdentifier bpkId;

    /**
     * Get carried task descriptor
     *
     * @return The descriptor
     */
    TaskDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Set the task descriptor
     *
     * @param descriptor Descriptor to set
     */
    void setDescriptor(TaskDescriptor descriptor) {
        this.descriptor = descriptor;
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
