package cz.cuni.mff.d3s.been.cluster.context;

import java.util.Collection;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * @author Martin Sixta
 * 
 *         FIXME see TaskEntries class and TaskUtils! Should be merged FIXME
 *         COMMENTS! COMMENTS! COMMENTS! :)
 */
public class TaskUtils {

	private ClusterContext clusterCtx;

	TaskUtils(ClusterContext clusterCtx) {
		// package private visibility prevents out-of-package instantiation
		this.clusterCtx = clusterCtx;
	}

	public IMap<String, TaskEntry> getTasksMap() {

		return clusterCtx.getMap(Names.TASKS_MAP_NAME);
	}

	public Collection<TaskEntry> getTasks() {
		return getTasksMap().values();
	}

	public Collection<TaskEntry> getTasks(TaskState state) {
		SqlPredicate predicate = new SqlPredicate("state = '" + state.toString() + "'");
		return getTasksMap().values(predicate);
	}

	/**
	 * Loads task entry from cluster.
	 * 
	 * @param id
	 *          id of task which should be loaded
	 * @return loaded task or null if task does not exists
	 */
	public TaskEntry getTask(String id) {
		return getTasksMap().get(id);
	}

	/**
	 * Stores task entry in cluster. Entry is stored in map in cluster, where the
	 * task's id is used as key.
	 * 
	 * @param taskEntry
	 *          entry to be stored
	 * @return previous value associated with id of taskEntry
	 */
	public TaskEntry putTask(TaskEntry taskEntry) {
		return getTasksMap().put(taskEntry.getId(), taskEntry);
	}

	public String submit(TaskDescriptor taskDescriptor) {
		// create task entry
		TaskEntry taskEntry = TaskEntries.create(taskDescriptor);

		// TODO
		TaskEntries.setState(taskEntry, TaskState.SUBMITTED, "Submitted by ...");

		getTasksMap().put(taskEntry.getId(), taskEntry);

		return taskEntry.getId();

	}

	public MapConfig getTasksMapConfig() {
		return clusterCtx.getConfig().findMatchingMapConfig("BEEN_MAP_TASKS");
	}

	public boolean isClusterEqual(TaskEntry entry) {

		TaskEntry taskEntryCopy = getTasksMap().get(entry.getId());

		if (taskEntryCopy == null || taskEntryCopy.equals(entry)) {
			return false;
		} else {
			return true;
		}
	}

	public void assertClusterEqual(TaskEntry entry) {
		if (!isClusterEqual(entry)) {
			throw new IllegalStateException(String.format("Entry '%s' has changed!", entry.getId()));
		}
	}

	public TaskEntry assertClusterEqualCopy(TaskEntry entry) {
		TaskEntry copy = getTask(entry.getId());

		if (entry.equals(copy)) {
			return copy;
		} else {
			throw new IllegalStateException(String.format("Entry '%s' has changed!", entry.getId()));
		}
	}

	public void assertEqual(TaskEntry entry, TaskEntry copy) {
		if (!entry.equals(copy)) {
			throw new IllegalStateException(String.format("Entry '%s' has changed!", entry.getId()));
		}
	}

	public void setStateAndPut(TaskEntry entry, TaskState newState,
			String reasonFormat, Object... reasonArgs) throws IllegalArgumentException {
		TaskEntries.setState(entry, newState, reasonFormat, reasonArgs);
		putTask(entry);
	}

}
