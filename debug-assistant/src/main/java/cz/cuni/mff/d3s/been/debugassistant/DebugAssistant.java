package cz.cuni.mff.d3s.been.debugassistant;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * Debug support for JVM-based tasks.
 * 
 * @author Kuba Břečka
 */
public class DebugAssistant {

	private static final Logger log = LoggerFactory.getLogger(DebugAssistant.class);

	private ClusterContext ctx;

	private String DEBUG_ASSISTANT_MAP_NAME = "BEEN_MAP_DEBUG_ASSISTANT";

	private final IMap<String, DebugListItem> debugMap;

	/**
	 * Creates new DebugAssistant.
	 * 
	 * @param clusterContext
	 *          connection to the cluster
	 */
	public DebugAssistant(final ClusterContext clusterContext) {
		this.ctx = clusterContext;
		this.debugMap = ctx.getMap(DEBUG_ASSISTANT_MAP_NAME);
	}

	/**
	 * Adds debugging info for a task.
	 * 
	 * @param taskId
	 *          ID of the task
	 * @param debugPort
	 *          port the task listens on
	 * @param suspended
	 *          whether the task was suspended
	 */
	public void addSuspendedTask(String taskId, int debugPort, boolean suspended) {
		String hostName = ctx.getInetSocketAddress().getHostName();
		DebugListItem item = new DebugListItem(taskId, hostName, debugPort, suspended);
		debugMap.put(taskId, item);
	}

	/**
	 * Removes debugging info of a task.
	 * 
	 * @param taskId
	 *          ID of the task to remove info for
	 */
	public void removeSuspendedTask(String taskId) {
		debugMap.remove(taskId);
	}

	/**
	 * Returns all listening task information
	 * 
	 * @return all listening task information
	 */
	public Collection<DebugListItem> listWaitingProcesses() {
		ArrayList<DebugListItem> list = new ArrayList<>();
		for (Object o : debugMap.values()) {
			list.add((DebugListItem) o);
		}
		return list;
	}

	/**
	 * Sets suspended flag of a task
	 * 
	 * @param taskId
	 *          ID of the task to set the flag for
	 * @param suspended
	 *          whether the task is suspended
	 */
	public void setSuspended(String taskId, boolean suspended) {
		DebugListItem debugListItem = debugMap.get(taskId);
		if (debugListItem != null) {
			debugListItem.setSuspended(suspended);
			debugMap.put(taskId, debugListItem);
		}

	}
}
