package cz.cuni.mff.d3s.been.core;

import static cz.cuni.mff.d3s.been.core.Names.TASKS_MAP_NAME;

import java.util.Collection;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

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

		return clusterCtx.getMap(TASKS_MAP_NAME);
	}

	public Collection<TaskEntry> getTasks() {
		return getTasksMap().values();
	}

	public Collection<TaskEntry> getTasks(TaskState state) {
		SqlPredicate predicate = new SqlPredicate("state = '" + state.toString() + "'");
		return getTasksMap().values(predicate);
	}

	public TaskEntry getTask(String key) {
		return getTasksMap().get(key);
	}

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

}