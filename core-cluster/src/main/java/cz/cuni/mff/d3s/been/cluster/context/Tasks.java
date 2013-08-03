package cz.cuni.mff.d3s.been.cluster.context;

import static cz.cuni.mff.d3s.been.core.task.TaskState.*;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * 
 * Utility class for BEEN tasks stored in Hazelcast map.
 * 
 * 
 * @author Martin Sixta
 */
public class Tasks {

	private static final Logger log = LoggerFactory.getLogger(Tasks.class);

	private ClusterContext clusterCtx;

	Tasks(ClusterContext clusterCtx) {
		// package private visibility prevents out-of-package instantiation
		this.clusterCtx = clusterCtx;
	}

	/**
	 * Returns Tasks map.
	 * 
	 * @return tasks map
	 */
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
	 * Stores task entry in the cluster. Task's id is used as the key.
	 * 
	 * @param taskEntry
	 *          entry to be stored
	 * @return old value of the entry
	 */
	public TaskEntry putTask(TaskEntry taskEntry) {
		return putTask(taskEntry, 1, TimeUnit.DAYS);
	}

	/**
	 * Stores task entry in the cluster with time-to-live. After ttl expires the
	 * entry is evicted from the map. Task's id is used as the key.
	 * 
	 * WARNING: don't use unless you know what are you doing
	 * 
	 * 
	 * @param entry
	 *          entry to be stored
	 * @param ttl
	 *          time-to-live of the entry
	 * @param timeUnit
	 *          time unit of ttl
	 * 
	 * @return old value of the entry
	 */
	public TaskEntry putTask(TaskEntry entry, long ttl, TimeUnit timeUnit) {
		return getTasksMap().put(entry.getId(), entry, ttl, timeUnit);
	}

	/**
	 * Submits a task described by the taskDescriptor to Task Manager to be
	 * scheduled.
	 * 
	 * @param taskEntry
	 *          TaskEntry of a task
	 * 
	 * @return id of the submitted task
	 */
	public String submit(TaskEntry taskEntry) {
		// TODO
		TaskEntries.setState(taskEntry, TaskState.SUBMITTED, "Submitted by ...");

		getTasksMap().put(taskEntry.getId(), taskEntry);

		return taskEntry.getId();

	}

	/**
	 * Returns configuration of tasks map.
	 * 
	 * @return Tasks map configuration
	 */
	public MapConfig getTasksMapConfig() {
		return clusterCtx.getConfig().findMatchingMapConfig(Names.TASKS_MAP_NAME);
	}

	/**
	 * 
	 * Warning: lock, transaction
	 * 
	 * @param entry
	 * @return
	 */
	public boolean isClusterEqual(TaskEntry entry) {
		TaskEntry copy = getTask(entry.getId());
		return entry.equals(copy);
	}

	/**
	 * 
	 * @param entry
	 */
	public void assertClusterEqual(TaskEntry entry) {
		if (!isClusterEqual(entry)) {
			throw new IllegalStateException(String.format("Entry '%s' has changed!", entry.getId()));
		}
	}

	/**
	 * Removes the task with the specified ID from Hazelcast map of tasks. The
	 * task must be in a final state (finished, aborted).
	 * 
	 * @param taskId
	 *          ID of the task to remove
	 */
	public void remove(String taskId) {
		TaskEntry taskEntry = getTask(taskId);

		if (taskEntry == null) {
			log.warn("Tried to remove task {} which does not exist.", taskId);
			return;
		}

		TaskState state = taskEntry.getState();
		if ((state == ABORTED) || (state == FINISHED)) {
			log.info("Removing task {} from map.", taskId);
			getTasksMap().remove(taskId);
		} else {
			throw new IllegalStateException(String.format("Trying to remove task %s, but it's in state %s.", taskId, state));
		}
	}

	/**
	 * Issues a "kill task" message and sends it to the global topic. This message
	 * will be delivered to the appropriate host runtime, which will try to kill
	 * the specified task.
	 * 
	 * @param taskId
	 *          ID of the task to kill
	 */
	public void kill(final String taskId) {
		TaskEntry taskEntry = getTask(taskId);
		TaskState state = taskEntry.getState();

		// this is tricky, the task has not been scheduled as of time of getTask() but ..
		if (state == WAITING) {
			final IMap<String, TaskEntry> tasksMap = clusterCtx.getTasks().getTasksMap();

			// ... we need to lock it ...
			try {
				tasksMap.lock(taskId);
				taskEntry = getTask(taskId);
				state = taskEntry.getState();

				// ... and check again ...
				if (state == WAITING) {
					TaskEntries.setState(taskEntry, ABORTED, "Killed by user");
					putTask(taskEntry);
				}

			} finally {
				tasksMap.unlock(taskId);
			}
		}

		if ((state == ABORTED) || (state == FINISHED)) {
			throw new IllegalStateException(String.format("Trying to kill task %s, but it's in state %s.", taskId, state));
		}

		String receiverId = taskEntry.getRuntimeId();
		KillTaskMessage killMessage = new KillTaskMessage(receiverId, "killed by user", taskId);
		clusterCtx.getTopics().publishInGlobalTopic(killMessage);
	}
}
