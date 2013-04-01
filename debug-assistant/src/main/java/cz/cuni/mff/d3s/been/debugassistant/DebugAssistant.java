package cz.cuni.mff.d3s.been.debugassistant;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 25.03.13
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public class DebugAssistant {

	private static final Logger log = LoggerFactory.getLogger(DebugAssistant.class);

	private ClusterContext ctx;

	private String DEBUG_ASSISTANT_MAP_NAME = "BEEN_MAP_DEBUG_ASSISTANT";

	public DebugAssistant(final ClusterContext clusterContext) {
		this.ctx = clusterContext;
	}

	public void addSuspendedTask(String taskId, String hostName, int debugPort) {
		DebugListItem item = new DebugListItem(taskId, hostName, debugPort);
		this.ctx.getMap(DEBUG_ASSISTANT_MAP_NAME).put(taskId, item);
	}

	public void removeSuspendedTask(String taskId) {
		this.ctx.getMap(DEBUG_ASSISTANT_MAP_NAME).remove(taskId);
	}

	public Collection<DebugListItem> listWaitingProcesses() {
		ArrayList<DebugListItem> list = new ArrayList<DebugListItem>();
		for (Object o : this.ctx.getMap(DEBUG_ASSISTANT_MAP_NAME).values()) {
			list.add((DebugListItem) o);
		}
		return list;
	}
}
