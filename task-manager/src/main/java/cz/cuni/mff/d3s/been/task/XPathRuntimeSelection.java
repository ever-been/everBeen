package cz.cuni.mff.d3s.been.task;

import java.util.Collection;
import java.util.Random;

import com.hazelcast.query.Predicate;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.query.XPathPredicate;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

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

		Predicate<?, ?> predicate = new XPathPredicate(xpath);

		Collection<RuntimeInfo> infos = clusterCtx.getRuntimesUtils().getRuntimeMap().values(predicate);

		if (infos.size() == 0) {
			throw new NoRuntimeFoundException();
		}

		// Stupid Java
		RuntimeInfo[] ids = infos.toArray(new RuntimeInfo[infos.size()]);

		return (ids[rnd.nextInt(ids.length)].getId());
	}
}
