package cz.cuni.mff.d3s.been.debugassistant;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * Created with IntelliJ IDEA. User: Kuba Date: 25.03.13 Time: 12:12 To change
 * this template use File | Settings | File Templates.
 */
public class DebugAssistant {

	private static final Logger log = LoggerFactory.getLogger(DebugAssistant.class);

	private ClusterContext ctx;

	private String DEBUG_ASSISTANT_MAP_NAME = "BEEN_MAP_DEBUG_ASSISTANT";

	private final IMap<String, DebugListItem> debugMap;

	public DebugAssistant(final ClusterContext clusterContext) {
		this.ctx = clusterContext;
		this.debugMap = ctx.getMap(DEBUG_ASSISTANT_MAP_NAME);
	}

	public void addSuspendedTask(String taskId, int debugPort, boolean suspended) {
		String hostName = ctx.getInetSocketAddress().getHostName();
		DebugListItem item = new DebugListItem(taskId, hostName, debugPort, suspended);
		debugMap.put(taskId, item);
	}

	public void removeSuspendedTask(String taskId) {
		debugMap.remove(taskId);
	}

	public Collection<DebugListItem> listWaitingProcesses() {
		ArrayList<DebugListItem> list = new ArrayList<>();
		for (Object o : debugMap.values()) {
			list.add((DebugListItem) o);
		}
		return list;
	}

	public void setSuspended(String taskId, boolean suspended) {
		DebugListItem debugListItem = debugMap.get(taskId);
		if (debugListItem != null) {
			debugListItem.setSuspended(suspended);
			debugMap.put(taskId, debugListItem);
		}

	}
}
