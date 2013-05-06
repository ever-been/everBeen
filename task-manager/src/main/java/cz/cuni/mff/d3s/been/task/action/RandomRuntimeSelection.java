package cz.cuni.mff.d3s.been.task.action;

import static cz.cuni.mff.d3s.been.core.task.TaskExclusivity.EXCLUSIVE;
import static cz.cuni.mff.d3s.been.core.task.TaskExclusivity.NON_EXCLUSIVE;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.task.NoRuntimeFoundException;
import cz.cuni.mff.d3s.been.task.RuntimesComparable;

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

		TaskExclusivity exclusivity = taskEntry.getTaskDescriptor().getExclusive();
		String contextId = taskEntry.getTaskContextId();

		Predicate<?, ?> predicate = new ExclusivityPredicate(exclusivity, contextId);

		Collection<RuntimeInfo> runtimes = clusterCtx.getRuntimes().getRuntimeMap().values(predicate);

		if (runtimes.size() == 0) {
			throw new NoRuntimeFoundException("Cannot find suitable Host Runtime");
		}

		// Stupid Java
		RuntimeInfo[] ids = runtimes.toArray(new RuntimeInfo[runtimes.size()]);

		Arrays.sort(ids, new RuntimesComparable());

		return (ids[0].getId());

	}

	/**
	 * 
	 * Predicate for filtering RuntimeInfo based on Host Runtime Exclusivity
	 * 
	 * @author Martin Sixta
	 */
	public static final class ExclusivityPredicate implements Predicate<String, RuntimeInfo> {
		private final TaskExclusivity taskExclusivity;
		private final String contextId;

		public ExclusivityPredicate(TaskExclusivity taskExclusivity, String contextId) {
			this.taskExclusivity = taskExclusivity;
			this.contextId = contextId;
		}

		@Override
		public boolean apply(MapEntry<String, RuntimeInfo> mapEntry) {
			RuntimeInfo info = mapEntry.getValue();

			if (info.getExclusivity() == null) {
				// workaround for JAXB not setting default value on elements
				info.setExclusivity(TaskExclusivity.NON_EXCLUSIVE.toString());
			}

			TaskExclusivity runtimeExclusivity;

			try {
				runtimeExclusivity = TaskExclusivity.valueOf(info.getExclusivity());
			} catch (IllegalArgumentException e) {
				// something fishy is going on, just skip this host runtime
				runtimeExclusivity = EXCLUSIVE;
			}

			switch (runtimeExclusivity) {
				case NON_EXCLUSIVE:
					boolean isExclusive = (taskExclusivity != NON_EXCLUSIVE);
					boolean hasTasks = (info.getTaskCount() > 0);
					return !(isExclusive && hasTasks);
				case CONTEXT_EXCLUSIVE:
					return contextId.equals(info.getExclusiveId());
				case EXCLUSIVE:
					return false;
			}

			return true;
		}
	}

}