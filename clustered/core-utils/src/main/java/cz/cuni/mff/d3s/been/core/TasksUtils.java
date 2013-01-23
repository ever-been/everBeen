package cz.cuni.mff.d3s.been.core;

import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

import java.util.Collection;

import static cz.cuni.mff.d3s.been.core.Names.*;

/**
 * @author Martin Sixta
 */
public class TasksUtils {

	public static IMap<String, TaskEntry> getTasksMap() {
		return MapUtils.getMap(TASKS_MAP_NAME);
	}


	public static Collection<TaskEntry> getTasks() {
		return getTasksMap().values();
	}

	public static Collection<TaskEntry> getTasks(TaskState state) {
		SqlPredicate predicate = new SqlPredicate("state = '" + state.toString() + "'");
		return getTasksMap().values(predicate);
	}

	public static TaskEntry getTask(String key) {
		return getTasksMap().get(key);
	}

	public static void putTask(TaskEntry taskEntry) {
		getTasksMap().put(taskEntry.getId(), taskEntry);
	}

	public static String submit(TaskDescriptor taskDescriptor) {
		// create task entry
		TaskEntry taskEntry = TaskEntries.create(taskDescriptor);

		// TODO
		TaskEntries.setState(taskEntry, TaskState.SUBMITTED, "Submitted by ...");

		getTasksMap().put(taskEntry.getId(), taskEntry);

		return taskEntry.getId();

	}


}
