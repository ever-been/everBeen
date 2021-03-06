package cz.cuni.mff.d3s.been.hostruntime.task;

import static cz.cuni.mff.d3s.been.core.task.TaskState.*;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.Tasks;
import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.task.PersistentTaskState;

/**
 * Utility class which encapsulate manipulation of {@link TaskEntry} of a given
 * task.
 * 
 * @author Martin Sixta
 */
public class TaskHandle {

	/** logging */
	private static final Logger log = LoggerFactory.getLogger(TaskHandle.class);

	/** the entry to take care of */
	private final TaskEntry entry;

	/** connection to the cluster */
	private final ClusterContext ctx;

	/** The map with task entries. */
	private final IMap<String, TaskEntry> map;

	/** ID of the task */
	private final String id;

	/** Tasks utility functions */
	private final Tasks tasks;

	/**
	 * Creates new handle.
	 * 
	 * @param entry
	 *          entry to manipulate upon
	 * @param ctx
	 *          connection to the cluster
	 */
	public TaskHandle(TaskEntry entry, ClusterContext ctx) {
		this.entry = entry;
		this.ctx = ctx;
		this.id = entry.getId();
		this.map = ctx.getTasks().getTasksMap();
		this.tasks = ctx.getTasks();
	}

	/**
	 * 
	 * Sets state of the entry to ACCEPTED.
	 * 
	 * The change is cluster visible.
	 * 
	 * @throws IllegalStateException
	 *           when cannot update entry
	 */
	public void setAccepted() throws IllegalStateException {
		updateEntry(ACCEPTED, "Task has been accepted on %s", entry.getRuntimeId());
	}

	/**
	 * 
	 * Sets state of the entry to RUNNING.
	 * 
	 * The change is cluster visible.
	 * 
	 * @param process
	 *          where to get runtime information to be updated in the entry
	 * 
	 * @throws IllegalStateException
	 *           when cannot update entry
	 * 
	 */
	public void setRunning(TaskProcess process) throws IllegalStateException {
		entry.setWorkingDirectory(process.getWorkingDirectory());
		setTaskEntryArgs(process.getArgs());
		updateEntry(TaskState.RUNNING, "Task is going to be run on %s", entry.getRuntimeId());
	}

	/**
	 * Sets state of the entry to FINISHED.
	 * 
	 * The change is cluster visible.
	 * 
	 * @param exitValue
	 *          the exit value of the task
	 * 
	 * @throws IllegalStateException
	 *           when cannot update entry
	 */
	public void setFinished(int exitValue) throws IllegalStateException {
		entry.setExitCode(exitValue);
		updateEntry(TaskState.FINISHED, "Task has finished with exit value %d", exitValue);
	}

	/**
	 * Sets state of the entry to ABORTED
	 * 
	 * @param message
	 *          formatted message of why the change happened
	 * 
	 * @throws IllegalStateException
	 *           when cannot update entry
	 */
	public void setAborted(String message) throws IllegalStateException {
		updateEntry(TaskState.ABORTED, "%s", message);
	}

	/**
	 * Sets state of the entry to ABORTED
	 * 
	 * @param message
	 *          formatted message of why the change happened
	 * @param exitValue
	 *          exit code of the aborted process
	 * 
	 * @throws IllegalStateException
	 *           when cannot update entry
	 * 
	 */
	public void setAborted(String message, int exitValue) throws IllegalStateException {
		entry.setExitCode(exitValue);
		updateEntry(TaskState.ABORTED, "%s", message);
	}

	/**
	 * Sets the state of the entry to SUBMITTED. This causes the task to be
	 * rescheduled.
	 * 
	 * No further manipulation of the entry is allowed after calling the function
	 * in this context of execution.
	 * 
	 * @param format
	 *          formatted message of why the change happened
	 * @param args
	 *          arguments for the formatted message
	 * 
	 * @throws IllegalStateException
	 *           when cannot update entry
	 */
	public void reSubmit(String format, Object... args) throws IllegalStateException {
		updateEntry(TaskState.SUBMITTED, format, args);
	}

	/**
	 * Returns TaskDescriptor associated with the entry.
	 * 
	 * @return TaskDescriptor associated with the entry
	 */
	public TaskDescriptor getTaskDescriptor() {
		return entry.getTaskDescriptor();
	}

	/**
	 * Returns context ID of the task.
	 * 
	 * @return context ID of the task
	 */
	public String getContextId() {
		return entry.getTaskContextId();
	}

	/**
	 * Returns ID of the task.
	 * 
	 * @return ID of the task
	 */
	public String getTaskId() {
		return entry.getId();
	}

	/**
	 * Returns current exclusivity
	 * 
	 * @return current exclusivity
	 */
	public TaskExclusivity getExclusivity() {
		return getTaskDescriptor().getExclusive();
	}

	/**
	 * Updates the entry in the cluster.
	 * 
	 * @param state
	 *          new state of the task
	 * @param format
	 *          formatted message of why the change happened
	 * @param args
	 *          arguments for the formatted message
	 * 
	 * @throws IllegalStateException
	 *           if the current entry has been concurrently modified
	 */
	private void updateEntry(TaskState state, String format, Object... args) throws IllegalStateException {

		map.lock(id);

		try {
			TaskEntry clusterEntry = map.get(id);

			if (clusterEntry == null) {
				String msg = String.format("No such task entry: %s", id);
				throw new IllegalStateException(msg);
			}

			if (!isSame(clusterEntry)) {
				String msg = String.format("Task entry '%s' concurrently modified.", id);
				throw new IllegalStateException(msg);
			}

			// change state of the entry
			TaskEntries.setState(entry, state, format, args);

			tasks.putTask(entry);
		} finally {
			map.unlock(id);
		}

		if (FINISHED.equals(state) || ABORTED.equals(state) || RUNNING.equals(state)) {
			try {
				PersistentTaskState entity = new PersistentTaskState();
				entity.setTaskState(state);
				entity.setTaskId(id);
				entity.setContextId(entry.getTaskContextId());
				entity.setBenchmarkId(entry.getBenchmarkId());
				entity.setRuntimeId(entry.getRuntimeId());

				List<StateChangeEntry> logEntries = entry.getStateChangeLog().getLogEntries();
				if (logEntries.size() > 0) {
					entity.setTimeStarted(logEntries.get(0).getTimestamp());
					entity.setTimeFinished(logEntries.get(logEntries.size() - 1).getTimestamp());
				}

				ctx.getPersistence().asyncPersist(Entities.OUTCOME_TASK.getId(), entity);
			} catch (DAOException e) {
				log.warn("Could not record finishing state of task '{}'. Task data may remain dangling.", id, e);
			}
		}

		log.debug("State of task {} has been changed to {}", id, state.toString());

	}

	/**
	 * Checks whether the current entry is still valid.
	 * 
	 * @param clusterEntry
	 *          entry to compare against
	 * @return true if the current entry has not been modified from the outside,
	 *         false otherwise
	 */
	private boolean isSame(TaskEntry clusterEntry) {
		boolean isScheduledHere = entry.getRuntimeId().equals(clusterEntry.getRuntimeId());
		boolean sameState = (entry.getState() == clusterEntry.getState());
		boolean sameContext = (entry.getTaskContextId().equals(clusterEntry.getTaskContextId()));

		return (isScheduledHere && sameState && sameContext);
	}

	/**
	 * 
	 * Adds necessary debug information to the cluster.
	 * 
	 * @param debugPort
	 *          debug port
	 * @param suspended
	 *          whether the task has been started suspended
	 */
	public void setDebug(int debugPort, boolean suspended) {
		DebugAssistant debugAssistant = new DebugAssistant(ctx);
		debugAssistant.addSuspendedTask(id, debugPort, suspended);
	}

	/**
	 * Adds command line arguments information to the entry
	 * 
	 * @param taskArguments
	 *          task command line arguments
	 */
	private void setTaskEntryArgs(Collection<String> taskArguments) {
		TaskEntry.Args args = new TaskEntry.Args();
		args.getArg().addAll(taskArguments);
		entry.setArgs(args);
	}

}
