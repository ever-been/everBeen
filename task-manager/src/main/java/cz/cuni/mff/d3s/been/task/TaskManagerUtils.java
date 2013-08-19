package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * 
 * Miscellaneous Task Manger utility functions
 * 
 * @author Martin Sixta
 */
final class TaskManagerUtils {
	/**
	 * Decides if an entry is owned by a node.
	 * 
	 * @param entry
	 *          entry to test
	 * @param nodeId
	 *          node ID
	 * @return true if owner of the entry is a node with nodeId
	 */
	static boolean isOwner(TaskEntry entry, String nodeId) {
		if (!entry.isSetOwnerId()) {
			return false;
		} else {
			return nodeId.equals(nodeId);
		}
	}
}
