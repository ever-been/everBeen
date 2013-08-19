package cz.cuni.mff.d3s.been.task.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hazelcast.query.Predicate;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.query.XPathPredicate;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;

/**
 * Finds a free Host Runtime for a task based on an xpath selector
 * 
 * @author Martin Sixta
 */
final class XPathRuntimeSelection implements IRuntimeSelection {
	private ClusterContext clusterCtx;
	private final TaskEntry entry;

	public XPathRuntimeSelection(ClusterContext clusterCtx, final TaskEntry entry) {
		this.clusterCtx = clusterCtx;
		this.entry = entry;
	}

	@Override
	public String select() throws NoRuntimeFoundException {
		TaskDescriptor td = entry.getTaskDescriptor();

		String xpath;
		if (td.isSetHostRuntimes() && td.getHostRuntimes().isSetXpath()) {
			xpath = td.getHostRuntimes().getXpath();
		} else {
			xpath = "/";
		}

		TaskExclusivity exclusivity = td.getExclusive();
		String contextId = entry.getTaskContextId();
		Predicate<?, ?> predicate = new XPathPredicate(contextId, xpath, exclusivity);

		List<RuntimeInfo> runtimes = new ArrayList<>(clusterCtx.getRuntimes().getRuntimeMap().values(predicate));

		if (runtimes.size() == 0) {
			throw new NoRuntimeFoundException("Cannot find suitable Host Runtime");
		}

		Collections.shuffle(runtimes);
		Collections.sort(runtimes, new RuntimesComparable());
		return (runtimes.get(0).getId());

	}
}
