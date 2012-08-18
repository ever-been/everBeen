package cz.cuni.mff.been.taskmanager.data;

import java.io.Serializable;
import java.util.Properties;
import java.util.regex.Pattern;

import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.jaxb.td.TaskExclusivity;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeAddress;

/**
 * Task entry in task manager.
 * 
 * @author darklight
 * 
 */
public interface TaskEntry extends Cloneable, Serializable {

	/**
	 * Regular expression for match <code>taskId</code>.
	 */
	public static final Pattern REGEXP_TASK_ID = Pattern
			.compile("^[a-zA-Z_0-9-]+$");

	/**
	 * Regular expression for match <code>contextId</code>.
	 */
	public static final Pattern REGEXP_CONTEXT_ID = ContextEntry.REGEXP_CONTEXT_ID;

	/**
	 * Regular expression for match <code>packageName</code>.
	 */
	public static final Pattern REGEXP_PACKAGE_NAME = Pattern
			.compile("^[a-zA-Z_0-9.+-]+$");

	/**
	 * Regular expression for match <code>hostName</code>.
	 */
	public static final Pattern REGEXP_HOST_NAME = HostRuntimeEntry.REGEXP_HOST_NAME;

	/**
	 * Regular expression for match <code>directoryPath</code>.
	 */
	public static final Pattern REGEXP_DIRECTORY_PATH = Pattern.compile("^.+$");

	/**
	 * Set name of host with <code>Host Runtime</code> running this task.
	 * 
	 * @param hostName
	 *            Name of host with <code>Host Runtime</code> running this task
	 *            (or <code>null</code> for unlink).
	 * @throws IllegalArgumentException
	 *             If <code>hostName</code> is not valid.
	 */
	public void setHostName(String hostName);

	/**
	 * Set path to task's directory <code>Task</code>.
	 * 
	 * @param directoryPathTask
	 *            Path to task's directory <code>Task</code>.
	 * @throws IllegalArgumentException
	 *             If <code>directoryPathTask</code> is not valid.
	 */
	public void setDirectoryPathTask(String directoryPathTask);

	/**
	 * Set path to task's directory <code>Working</code>.
	 * 
	 * @param directoryPathWorking
	 *            Path to task's directory <code>Working</code>.
	 * @throws IllegalArgumentException
	 *             If <code>directoryPathWorking</code> is not valid.
	 */
	public void setDirectoryPathWorking(String directoryPathWorking);

	/**
	 * Set path to task's directory <code>Temporary</code>.
	 * 
	 * @param directoryPathTemporary
	 *            Path to task's directory <code>Temporary</code>.
	 * @throws IllegalArgumentException
	 *             If <code>directoryPathTemporary</code> is not valid.
	 */
	public void setDirectoryPathTemporary(String directoryPathTemporary);

	/**
	 * Set <code>true</code> if this task is service, otherwise
	 * <code>false</code>.
	 * 
	 * @param serviceFlag
	 *            <code>true</code> if this task is service, otherwise
	 *            <code>false</code>.
	 */
	public void setServiceFlag(boolean serviceFlag);

	/**
	 * Get time of creation of this object (according with system creating this
	 * entry) in milliseconds. (Difference between current time and midnight,
	 * January 1, 1970 UTC.
	 * 
	 * @return Time of creation of this object.
	 */
	public long getCurrentTime();

	/**
	 * Get ID of this task.
	 * 
	 * @return ID of this task.
	 */
	public String getTaskId();

	/**
	 * Get ID of context containing this task.
	 * 
	 * @return ID of context containing this task.
	 */
	public String getContextId();

	/**
	 * Get name of BEEN's package containing this task.
	 * 
	 * @return Name of BEEN's package containing this task.
	 */
	public String getPackageName();

	/**
	 * Get human readable name of this task.
	 * 
	 * @return Human readable name of this task.
	 */
	public String getTaskName();

	/**
	 * Get human readable description of this task.
	 * 
	 * @return Human readable description of this task.
	 */
	public String getTaskDescription();

	/**
	 * Task tree address getter.
	 * 
	 * @return Address of the represented task in the task tree.
	 */
	public TaskTreeAddress getTreeAddress();

	/**
	 * Task tree path getter.
	 * 
	 * @return Path of the represented task in the task tree.
	 */
	public String getTreePath();

	/**
	 * Task descriptor getter.
	 * 
	 * @return The task descriptor used to spawn this task. (For XML output
	 *         purposes.)
	 */
	public TaskDescriptor getOriginalTaskDescriptor();

	/**
	 * Task descriptor getter.
	 * 
	 * @return The task descriptor used after modification (with resolved RSL
	 *         expresssions).
	 */
	public TaskDescriptor getModifiedTaskDescriptor();

	/**
	 * Get name of host with <code>Host Runtime</code> running this task.
	 * 
	 * @return Name of host with <code>Host Runtime</code> running this task.
	 */
	public String getHostName();

	/**
	 * Get path to task's directory <code>Task</code>.
	 * 
	 * @return Path to task's directory <code>Task</code>.
	 */
	public String getDirectoryPathTask();

	/**
	 * Get path to task's directory <code>Working</code>.
	 * 
	 * @return Path to task's directory <code>Working</code>.
	 */
	public String getDirectoryPathWorking();

	/**
	 * Get path to task's directory <code>Temporary</code>.
	 * 
	 * @return Path to task's directory <code>Temporary</code>.
	 */
	public String getDirectoryPathTemporary();

	/**
	 * Get properties of task.
	 * 
	 * @return Properties of task.
	 */
	public Properties getTaskProperties();

	/**
	 * Get exclusivity of this task (non-exclusive, context-exclusive,
	 * exclusive).
	 * 
	 * @return Exclusivity of this task (non-exclusive, context-exclusive,
	 *         exclusive).
	 */
	public TaskExclusivity getExclusivity();

	/**
	 * Get <code>true</code> if this task is service, otherwise (if job)
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> if this task is service, otherwise (if job)
	 *         <code>false</code>.
	 */
	public boolean getServiceFlag();

	/**
	 * Get state of this task (submitted, scheduled, running, waiting,
	 * finished).
	 * 
	 * @return State of this task (submitted, scheduled, running, waiting,
	 *         finished).
	 */
	public TaskState getState();

	/**
	 * Get time (in milliseconds) when task was submitted.
	 * 
	 * @return Time (in milliseconds) when task was submitted.
	 */
	public long getTimeSubmitted();

	/**
	 * Get time (in milliseconds) when task was scheduled or zero if not
	 * scheduled yet.
	 * 
	 * @return Time (in milliseconds) when task was scheduled or zero if not
	 *         scheduled yet.
	 */
	public long getTimeScheduled();

	/**
	 * Get time (in milliseconds) when task was started or zero if not started
	 * yet.
	 * 
	 * @return Time (in milliseconds) when task was started of zero if not
	 *         started yet.
	 */
	public long getTimeStarted();

	/**
	 * Get time (in milliseconds) when task was finished or zero if not finished
	 * yet.
	 * 
	 * @return Time (in milliseconds) when task was finished or zero if not
	 *         finished yet.
	 */
	public long getTimeFinished();

	/**
	 * Get how many times this task was restarted.
	 * 
	 * @return How many times this task was restarted.
	 */
	public int getRestartCount();

	/**
	 * Get how many restarts for this task are allowed (before failed). Zero if
	 * not restricted.
	 * 
	 * @return How many restarts for this task are allowed (before failed). Zero
	 *         if not restricted.
	 */
	public int getRestartMax();

	/**
	 * Get how long (in milliseconds) this task can run (from
	 * <code>started</code> to <code>finished</code> state). Zero if not
	 * restricted.
	 * 
	 * @return How long (in milliseconds) this task can run (from
	 *         <code>started</code> to <code>finished</code> state). Zero if not
	 *         restricted.
	 */
	public long getTimeoutRun();
}
