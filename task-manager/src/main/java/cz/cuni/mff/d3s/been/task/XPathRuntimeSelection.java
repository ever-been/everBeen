package cz.cuni.mff.d3s.been.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import com.hazelcast.query.Predicate;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.query.XPathPredicate;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;

/**
 * @author Martin Sixta
 */
final class XPathRuntimeSelection implements IRuntimeSelection {
	private final Random rnd;
	private ClusterContext clusterCtx;

	public XPathRuntimeSelection(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
		rnd = new Random();
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

		Collection<RuntimeInfo> runtimes = clusterCtx.getRuntimes().getRuntimeMap().values(
				predicate);

		if (runtimes.size() == 0) {
			throw new NoRuntimeFoundException("Cannot find suitable Host Runtime");
		}

		// Stupid Java
		RuntimeInfo[] ids = runtimes.toArray(new RuntimeInfo[runtimes.size()]);

		Arrays.sort(ids, new RuntimesComparable());

		return (ids[0].getId());

	}
}
