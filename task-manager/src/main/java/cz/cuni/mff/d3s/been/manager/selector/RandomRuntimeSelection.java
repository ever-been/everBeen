package cz.cuni.mff.d3s.been.manager.selector;

import static cz.cuni.mff.d3s.been.core.task.TaskExclusivity.EXCLUSIVE;
import static cz.cuni.mff.d3s.been.core.task.TaskExclusivity.NON_EXCLUSIVE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfos;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;

/**
 * Finds a free Host Runtime for a task.
 * 
 * This selector randomly chooses a Host Runtime.
 * 
 * @author Martin Sixta
 */
final class RandomRuntimeSelection implements IRuntimeSelection {

	private ClusterContext clusterCtx;
	private final TaskEntry entry;

	public RandomRuntimeSelection(final ClusterContext clusterCtx, final TaskEntry entry) {
		this.clusterCtx = clusterCtx;
		this.entry = entry;
	}

	@Override
	public String select() throws NoRuntimeFoundException {

		TaskExclusivity exclusivity = entry.getTaskDescriptor().getExclusive();
		String contextId = entry.getTaskContextId();

		Predicate<?, ?> predicate = new ExclusivityPredicate(exclusivity, contextId);

		List<RuntimeInfo> runtimes = new ArrayList<>(clusterCtx.getRuntimes().getRuntimeMap().values(predicate));

		if (runtimes.size() == 0) {
			throw new NoRuntimeFoundException("Cannot find suitable Host Runtime");
		}

		Collections.shuffle(runtimes);
		Collections.sort(runtimes, new RuntimesComparable());
		return (runtimes.get(0).getId());

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

			// Runtime Overload conditions
			if (RuntimeInfos.isMaxTasksReached(info)) {
				return false;
			}

			if (RuntimeInfos.isMemoryThresholdReached(info)) {
				return false;
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
