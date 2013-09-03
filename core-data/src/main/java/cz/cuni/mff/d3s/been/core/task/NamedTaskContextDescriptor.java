package cz.cuni.mff.d3s.been.core.task;

import java.io.Serializable;

/**
 * Named TaskContextDescriptor.
 * 
 * @author donarus
 */
public class NamedTaskContextDescriptor extends NamedDescriptor<TaskContextDescriptor> implements Serializable {

	/**
	 * Creates new TaskNamedDescriptor
	 * 
	 * @param name
	 *          name of the named descriptor
	 * @param groupId
	 *          groupId associated with the named descriptor
	 * @param bpkId
	 *          bpkId associated with the named descriptor
	 * @param bpkVersion
	 *          version associated with the named descriptor
	 * @param descriptor
	 *          the descriptor to save
	 */
	public NamedTaskContextDescriptor(
			String name,
			String groupId,
			String bpkId,
			String bpkVersion,
			TaskContextDescriptor descriptor) {
		super(name, groupId, bpkId, bpkVersion, descriptor);
	}
}
