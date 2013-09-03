package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

/**
 * Creates BpkIdentifier helper class.
 * 
 * @author Tadeáš Palusga
 */
final class BpkIdentifierCreator {

	/**
	 * Creates BpkIdentifier from values in a TaskDescriptor.
	 * 
	 * @param td
	 *          where to take values from
	 * @return BpkIdentifier corresponding to the TaskDescriptor
	 */
	public static BpkIdentifier createBpkIdentifier(TaskDescriptor td) {

		BpkIdentifier bpkIdentifier = new BpkIdentifier();
		bpkIdentifier.setGroupId(td.getGroupId());
		bpkIdentifier.setBpkId(td.getBpkId());
		bpkIdentifier.setVersion(td.getVersion());

		return bpkIdentifier;
	}

}
