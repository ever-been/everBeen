package cz.cuni.mff.d3s.been.task;

import java.util.Collection;
import java.util.Random;

import cz.cuni.mff.d3s.been.core.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * @author Martin Sixta
 */
final class RandomRuntimeSelection implements IRuntimeSelection {

	private final Random rnd;
	private ClusterContext clusterCtx;

	public RandomRuntimeSelection(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
		rnd = new Random();
	}

	@Override
	public String select(final TaskEntry taskEntry) throws NoRuntimeFoundException {
		Collection<RuntimeInfo> infos = clusterCtx.getRuntimesUtils().getRuntimes();

		if (infos.size() == 0) {
			throw new NoRuntimeFoundException();
		}

		// Stupid Java
		RuntimeInfo[] ids = infos.toArray(new RuntimeInfo[infos.size()]);

		return (ids[rnd.nextInt(ids.length)].getId());
	}
}
