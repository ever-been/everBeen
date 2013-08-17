package cz.cuni.mff.d3s.been.core.task;

import java.io.Serializable;

/**
 * @author donarus
 */
public class NamedTaskDescriptor extends NamedDescriptor<TaskDescriptor> implements Serializable {

	public NamedTaskDescriptor(String name, String groupId, String bpkId, String bpkVersion, TaskDescriptor descriptor) {
		super(name, groupId, bpkId, bpkVersion, descriptor);
	}

}
