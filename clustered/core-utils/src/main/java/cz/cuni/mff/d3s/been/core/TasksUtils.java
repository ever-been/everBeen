package cz.cuni.mff.d3s.been.core;

import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

import java.util.Collection;
import java.util.UUID;

import static  cz.cuni.mff.d3s.been.core.Names.*;

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
		IMap<String, TaskEntry> map = MapUtils.getMap(TASKS_MAP_NAME);

		return map.values(predicate);
	}

	public static TaskEntry getTask(String key) {
		return getTasksMap().get(key);
	}

	public static String submit(cz.cuni.mff.d3s.been.core.td.TaskDescriptor taskDescriptor) {

		// create task entry
		String id = UUID.randomUUID().toString();
		TaskEntry taskEntry = new TaskEntry(id, taskDescriptor);

		getTasksMap().put(id, taskEntry);

		return id;

	}



}
