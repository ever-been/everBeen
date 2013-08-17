package cz.cuni.mff.d3s.been.core.task;

import java.io.Serializable;

/**
 * @author donarus
 */
public class NamedTaskContextDescriptor extends NamedDescriptor<TaskContextDescriptor> implements Serializable {

	public NamedTaskContextDescriptor(
			String name,
			String groupId,
			String bpkId,
			String bpkVersion,
			TaskContextDescriptor descriptor) {
		super(name, groupId, bpkId, bpkVersion, descriptor);
	}
}
