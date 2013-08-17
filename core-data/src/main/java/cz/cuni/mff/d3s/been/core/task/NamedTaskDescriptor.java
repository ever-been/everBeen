package cz.cuni.mff.d3s.been.core.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

import java.io.Serializable;

/**
 * @author donarus
 */
public class NamedTaskDescriptor extends NamedDescriptor<TaskDescriptor> implements Serializable {

    public NamedTaskDescriptor(String name, String groupId, String bpkId, String version, TaskDescriptor descriptor) {
        super(name, groupId, bpkId, version, descriptor);
    }

}
