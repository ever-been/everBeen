package cz.cuni.mff.d3s.been.task.action;

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
import cz.cuni.mff.d3s.been.task.NoRuntimeFoundException;
import cz.cuni.mff.d3s.been.task.RuntimesComparable;

/**
 * @author Martin Sixta
 */
final class XPathRuntimeSelection implements IRuntimeSelection {
	private ClusterContext clusterCtx;

	public XPathRuntimeSelection(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
	}

	@Override
	public String select(TaskEntry taskEntry) throws NoRuntimeFoundException {
		TaskDescriptor td = taskEntry.getTaskDescriptor();

		String xpath;
		if (td.isSetHostRuntimes() && td.getHostRuntimes().isSetXpath()) {
			xpath = td.getHostRuntimes().getXpath();
		} else {
			xpath = "/";
		}

		TaskExclusivity exclusivity = td.getExclusive();
		String contextId = taskEntry.getTaskContextId();
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
