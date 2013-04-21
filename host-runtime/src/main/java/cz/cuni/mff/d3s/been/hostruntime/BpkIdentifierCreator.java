package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

/**
 * Created with IntelliJ IDEA. User: donarus Date: 4/21/13 Time: 5:17 PM To
 * change this template use File | Settings | File Templates.
 */
public class BpkIdentifierCreator {

	/**
	 * Creates BpkIdentifier from values in a TaskDescriptor.
	 * 
	 * @param td
	 *          where to take values from
	 * @return BpkIdentifier corresponding to the TaskDescriptor
	 */
	public static BpkIdentifier createBpkIdentifier(TaskDescriptor td) {

		// FIXME - use BpkIdentifier as wrapper for groupId, bpkId and version directly in TaskDescriptor
		BpkIdentifier bpkIdentifier = new BpkIdentifier();
		bpkIdentifier.setGroupId(td.getGroupId());
		bpkIdentifier.setBpkId(td.getBpkId());
		bpkIdentifier.setVersion(td.getVersion());

		return bpkIdentifier;
	}

}
