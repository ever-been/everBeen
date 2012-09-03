/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Antonin Tomecek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.taskmanager.data;

import java.util.Properties;

import cz.cuni.mff.been.jaxb.td.FailurePolicy;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.jaxb.td.TaskExclusivity;
import cz.cuni.mff.been.jaxb.td.TaskProperties;
import cz.cuni.mff.been.jaxb.td.TaskProperty;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeAddress;

/**
 * Class representing one entry (task) in
 * {@link cz.cuni.mff.been.taskmanager.TaskManagerImplementation
 * <code>Task Manager</code>}.
 * 
 * @author Antonin Tomecek
 */
public class TaskEntryImplementation implements TaskEntry {

	/** Serialization ID */
	private static final long serialVersionUID = 4704555606179212402L;

	/*
	 * Current time on system creating this entry in milliseconds. Difference
	 * between current time and midnight, January 1, 1970 UTC.
	 */
	private long currentTimeMillis;

	/* ID of this task. */
	private String taskId = "";

	/* ID of context containing this task. */
	private String contextId = "";

	/* Name of BEEN's package containing this task. */
	private String packageName = "";

	/* Human readable name of this task. */
	private String taskName = "";

	/* Human readable description of this task. */
	private String taskDescription = "";

	/** Task tree address of this task. */
	private TaskTreeAddress treeAddress;

	/** Path in the tree of tasks. */
	private String treePath;

	/** Task descriptor used when this task was created. */
	private TaskDescriptor modifiedDescriptor;

	/** Task descriptor originally submitted to the Task Manager. */
	private TaskDescriptor originalDescriptor;

	/* Name of host with Host Runtime running this task or null if not set yet. */
	private String hostName = null;

	/* Path to task's directory Task. */
	private String directoryPathTask = "";

	/* Path to task's directory Working. */
	private String directoryPathWorking = "";

	/* Path to task's directory Temporary. */
	private String directoryPathTemporary = "";

	/* Properties of task. */
	private Properties taskProperties = new Properties();

	/*
	 * Exclusivity of this task (non-exclusive, context-exclusive, exclusive).
	 */
	private TaskExclusivity exclusivity = TaskExclusivity.NON_EXCLUSIVE;

	/* True if this task is service, otherwise (if job) false. */
	private boolean serviceFlag = false;

	/*
	 * State of this task (submitted, scheduled, running, waiting, finished).
	 */
	private TaskState state = TaskState.SUBMITTED;

	/* Time (in milliseconds) when task was submitted. */
	private long timeSubmitted = 0;

	/*
	 * Time (in milliseconds) when task was scheduled or zero if not scheduled
	 * yet.
	 */
	private long timeScheduled = 0;

	/*
	 * Time (in milliseconds) when task was started or zero if not started yet.
	 */
	private long timeStarted = 0;

	/*
	 * Time (in milliseconds) when task was finished or zero if not finished
	 * yet.
	 */
	private long timeFinished = 0;

	/* How many times this task was restarted. */
	private int restartCount = 0;

	/*
	 * How many restarts for this task are allowed (before failed). Zero if not
	 * restricted.
	 */
	private int restartMax = 0;

	/*
	 * How long (in milliseconds) this task can run (from started to finished
	 * state). Zero if not restricted.
	 */
	private long timeoutRun = 0;

	/**
	 * A stupid constructor for testing purposes only!
	 * 
	 * @param taskId
	 *            A dummy string to assign as task ID.
	 */
	public TaskEntryImplementation(String taskId) {
		setTaskId(taskId);
	}

	/**
	 * Creates a new (empty - default initialized) <code>TaskEntry</code>. This
	 * constructor is needed by <code>Serializable</code>.
	 */
	public TaskEntryImplementation(TaskTreeAddress treeAddress,
			TaskDescriptor modifiedTaskDescriptor,
			TaskDescriptor originalTaskDescriptor) {
		this.currentTimeMillis = System.currentTimeMillis();

		// FIXME blow this code to nine hells - these setters are overridable
		setOriginalTaskDescriptor(originalTaskDescriptor);
		setModifiedTaskDescriptor(modifiedTaskDescriptor);
		setTaskId(modifiedTaskDescriptor.getTaskId());
		setContextId(modifiedTaskDescriptor.getContextId());
		setTreeAddress(treeAddress);
		setTreePath(modifiedTaskDescriptor.getTreeAddress());
		setTaskName(modifiedTaskDescriptor.getName());
		setTaskDescription(modifiedTaskDescriptor.isSetLongDescription() ? modifiedTaskDescriptor
				.getLongDescription() : modifiedTaskDescriptor.getDescription() // OK,
																				// defaults
																				// to
																				// "".
		);
		setPackageName(modifiedTaskDescriptor.getPackage().getName()); // OK,
																		// has
																		// default.
		setExclusivity(modifiedTaskDescriptor.getExclusive()); // Always set.
		if (modifiedTaskDescriptor.isSetFailurePolicy()) {
			FailurePolicy failurePolicy = modifiedTaskDescriptor
					.getFailurePolicy();
			setRestartMax(failurePolicy.getRestartMax());
			setTimeoutRun(failurePolicy.getTimeoutRun());
		}
		if (modifiedTaskDescriptor.isSetTaskProperties()) {
			TaskProperties taskProperties = modifiedTaskDescriptor
					.getTaskProperties();
			if (taskProperties.isSetTaskProperty()) {
				Properties properties = new Properties();
				for (TaskProperty taskProperty : taskProperties
						.getTaskProperty()) {
					properties.setProperty(
							taskProperty.getKey(),
							taskProperty.isSetLongValue() ? taskProperty
									.getValue() + taskProperty.getLongValue()
									: taskProperty.getValue());
				}
				setTaskProperties(properties);
			}
		}

		// this.taskId = "";
		// this.contextId = "";
		// this.packageName = "";
		// this.taskName = "";
		// this.taskDescription = "";
		// this.hostName = null;
		// this.directoryPathTask = "";
		// this.directoryPathWorking = "";
		// this.directoryPathTemporary = "";
		// this.exclusivity = TaskExclusivity.NON_EXCLUSIVE;
		// this.serviceFlag = false;
		// this.state = TaskState.SUBMITTED;
		// this.timeSubmitted = 0;
		// this.timeStarted = 0;
		// this.timeFinished = 0;
		// this.restartCount = 0;
		// this.restartMax = 0;
		// this.timeoutRun = 0;
	}

	/**
	 * Creates a new (and initialized) <code>TaskEntry</code>.
	 * 
	 * @param taskId
	 *            ID of this task.
	 * @param contextId
	 *            ID of context containing this task.
	 * @param packageName
	 *            Name of BEEN's package containing this task.
	 * @param taskName
	 *            Human readable name of this task (or <code>null</code>).
	 * @param taskDescription
	 *            Human readable description of this task (or <code>null</code>
	 *            ).
	 * @param hostName
	 *            Name of host with <code>Host Runtime</code> running this task
	 *            or <code>null</code> if not choosen yet.
	 * @param directoryPathTask
	 *            Path to task's directory <code>Task</code>.
	 * @param directoryPathWorking
	 *            Path to task's directory <code>Working</code>.
	 * @param directoryPathTemporary
	 *            Path to task's directory <code>Temporary</code>.
	 * @param taskProperties
	 *            Properties of task.
	 * @param exclusivity
	 *            Exclusivity of this task.
	 * @param serviceFlag
	 *            <code>true</code> if this task is service, otherwise
	 *            <code>false</code>.
	 * @param restartMax
	 *            How many restarts for this task are allowed (before failed).
	 *            Zero if not restricted.
	 * @param timeoutRun
	 *            How long (in milliseconds) this task can run (from
	 *            <code>started</code> to <code>finished</code> state). Zero if
	 *            not restricted.
	 */
	public TaskEntryImplementation(String taskId, String contextId,
			String packageName, String taskName, String taskDescription,
			String hostName, String directoryPathTask,
			String directoryPathWorking, String directoryPathTemporary,
			Properties taskProperties, TaskExclusivity exclusivity,
			boolean serviceFlag, int restartMax, long timeoutRun) {
		this.currentTimeMillis = System.currentTimeMillis();

		/* Inicialization of variables with public write access. */
		this.setTaskId(taskId);
		this.setContextId(contextId);
		this.setPackageName(packageName);
		if (taskName != null) {
			this.setTaskName(taskName);
		}
		if (taskDescription != null) {
			this.setTaskDescription(taskDescription);
		}
		if (hostName != null) {
			this.setHostName(hostName);
		}
		this.setDirectoryPathTask(directoryPathTask);
		this.setDirectoryPathWorking(directoryPathWorking);
		this.setDirectoryPathTemporary(directoryPathTemporary);
		this.setTaskProperties(taskProperties);
		this.setExclusivity(exclusivity);
		this.setServiceFlag(serviceFlag);

		// /* Initialization of following (protected write access) variables
		// with
		// * default values:
		// * state,
		// * timeSubmitted,
		// * timeScheduled,
		// * timeStarted,
		// * timeFinished,
		// * restartCount.
		// */
		// this.initInternals(TaskState.SUBMITTED, 0, 0, 0, 0, 0);

		/* Inicialization of variables with public write access. */
		this.setRestartMax(restartMax);
		this.setTimeoutRun(timeoutRun);
	}

	/**
	 * Initialization of variables with protected write access.
	 * 
	 * @param state
	 *            State of this task (submitted, scheduled, running, waiting,
	 *            finished).
	 * @param timeSubmitted
	 *            Time (in milliseconds) when task was submitted.
	 * @param timeScheduled
	 *            Time (in milliseconds) when task was scheduled or zero if not
	 *            scheduled yet.
	 * @param timeStarted
	 *            Time (in milliseconds) when task was started or zero if not
	 *            started yet.
	 * @param timeFinished
	 *            Time (in milliseconds) when task was finished or zero if not
	 *            finished yet.
	 * @param restartCount
	 *            How many times this task was restarted.
	 */
	protected void initInternals(
			TaskState state,
			long timeSubmitted,
			long timeScheduled,
			long timeStarted,
			long timeFinished,
			int restartCount) {
		this.setState(state);
		this.setTimeSubmitted(timeSubmitted);
		this.setTimeScheduled(timeScheduled);
		this.setTimeStarted(timeStarted);
		this.setTimeFinished(timeFinished);
		this.setRestartCount(restartCount);
	}

	/**
	 * Set ID of this task.
	 * 
	 * @param taskId
	 *            ID of this task.
	 * @throws IllegalArgumentException
	 *             If <code>taskId</code> is not valid.
	 */
	private void setTaskId(String taskId) {
		if (REGEXP_TASK_ID.matcher(taskId).matches()) {
			this.taskId = taskId;
		} else {
			throw new IllegalArgumentException("taskId is not valid");
		}
	}

	/**
	 * Set ID of context containing this task.
	 * 
	 * @param contextId
	 *            ID of context containing this task.
	 * @throws IllegalArgumentException
	 *             If <code>contextId</code> is not valid.
	 */
	private void setContextId(String contextId) {
		if (REGEXP_CONTEXT_ID.matcher(contextId).matches()) {
			this.contextId = contextId;
		} else {
			throw new IllegalArgumentException("contextId is not valid");
		}
	}

	/**
	 * Set name of BEEN's package containing this task.
	 * 
	 * @param packageName
	 *            Name of BEEN's package containing this task.
	 * @throws IllegalArgumentException
	 *             If <code>packageName</code> is not valid.
	 */
	private void setPackageName(String packageName) {
		if (REGEXP_PACKAGE_NAME.matcher(packageName).matches()) {
			this.packageName = packageName;
		} else {
			throw new IllegalArgumentException("packageName is not valid");
		}
	}

	/**
	 * Set human readable name of this task.
	 * 
	 * @param taskName
	 *            Human readable name of this task.
	 * @throws IllegalArgumentException
	 *             If <code>taskName</code> is not valid.
	 */
	private void setTaskName(String taskName) {
		// if (Pattern.matches(REGEXP_TASK_NAME, taskName)) {
		if (taskName != null) {
			this.taskName = taskName;
		} else {
			throw new IllegalArgumentException("taskName is not valid");
		}
	}

	/**
	 * Set human readable description of this task.
	 * 
	 * @param taskDescription
	 *            Human readable description of this task.
	 * @throws IllegalArgumentException
	 *             If <code>taskDescription</code> is not valid.
	 */
	private void setTaskDescription(String taskDescription) {
		if (taskDescription != null) {
			this.taskDescription = taskDescription;
		} else {
			throw new IllegalArgumentException("taskDescription is not valid");
		}
	}

	/**
	 * Task tree address setter.
	 * 
	 * @param treeAddress
	 *            Address of the task in the visual tree.
	 */
	private void setTreeAddress(TaskTreeAddress treeAddress) {
		if (null == treeAddress) {
			throw new IllegalArgumentException("treeAddress is not valid");
		} else {
			this.treeAddress = treeAddress;
		}
	}

	/**
	 * Task tree path setter.
	 * 
	 * @param treePath
	 *            A string representation of the task's tree address.
	 */
	private void setTreePath(String treePath) {
		if (null == treePath) {
			throw new IllegalArgumentException("treePath is not valid");
		} else {
			this.treePath = treePath;
		}
	}

	/**
	 * Task descriptor setter.
	 * 
	 * @param originalDescriptor
	 *            The task descriptor used to spawn this task.
	 */
	private void setOriginalTaskDescriptor(TaskDescriptor originalDescriptor) {
		if (null == originalDescriptor) {
			throw new IllegalArgumentException(
					"originalDescriptor is not valid");
		} else {
			this.originalDescriptor = originalDescriptor;
		}
	}

	/**
	 * Task descriptor setter.
	 * 
	 * @param modifiedDescriptor
	 *            The task descriptor after preprocessing (RSL resolution).
	 */
	private void setModifiedTaskDescriptor(TaskDescriptor modifiedDescriptor) {
		if (null == modifiedDescriptor) {
			throw new IllegalArgumentException(
					"modifiedDescriptor is not valid");
		} else {
			this.modifiedDescriptor = modifiedDescriptor;
		}
	}

	@Override
	public void setHostName(String hostName) {
		if (hostName == null) {
			return; // do nothing (needed for unlink)
		}
		if (REGEXP_HOST_NAME.matcher(hostName).matches()) {
			this.hostName = hostName;
		} else {
			throw new IllegalArgumentException("hostName is not valid");
		}
	}

	@Override
	public void setDirectoryPathTask(String directoryPathTask) {
		if (REGEXP_DIRECTORY_PATH.matcher(directoryPathTask).matches()) {
			this.directoryPathTask = directoryPathTask;
		} else {
			throw new IllegalArgumentException("directoryPathTask is not valid");
		}
	}

	@Override
	public void setDirectoryPathWorking(String directoryPathWorking) {
		if (REGEXP_DIRECTORY_PATH.matcher(directoryPathWorking).matches()) {
			this.directoryPathWorking = directoryPathWorking;
		} else {
			throw new IllegalArgumentException(
					"directoryPathWorking is not valid");
		}
	}

	@Override
	public void setDirectoryPathTemporary(String directoryPathTemporary) {
		if (REGEXP_DIRECTORY_PATH.matcher(directoryPathTemporary).matches()) {
			this.directoryPathTemporary = directoryPathTemporary;
		} else {
			throw new IllegalArgumentException(
					"directoryPathTemporary is not valid");
		}
	}

	/**
	 * Set properties of task.
	 * 
	 * @param taskProperties
	 *            Properties of task.
	 */
	private void setTaskProperties(Properties taskProperties) {
		if (taskProperties != null) {
			this.taskProperties = taskProperties;
		} else {
			this.taskProperties = new Properties(); // Use empty...
		}
	}

	/**
	 * Set exclusivity of this task (non-exclusive, context-exclusive,
	 * exclusive).
	 * 
	 * @param exclusivity
	 *            Exclusivity of this task (non-exclusive, context-exclusive,
	 *            exclusive).
	 */
	private void setExclusivity(TaskExclusivity exclusivity) {
		if (null == exclusivity) {
			throw new IllegalArgumentException("exclusivity is not valid");
		} else {
			this.exclusivity = exclusivity;
		}
	}

	@Override
	public void setServiceFlag(boolean serviceFlag) {
		this.serviceFlag = serviceFlag;
	}

	/**
	 * Set state of this task (submitted, scheduled, running, waiting,
	 * finished).
	 * 
	 * @param state
	 *            State of this task (submitted, scheduled, running, waiting,
	 *            finished.
	 */
	protected void setState(TaskState state) {
		if (null == state) {
			throw new IllegalArgumentException("state is not valid");
		} else {
			this.state = state;
		}
	}

	/**
	 * Set time (in milliseconds) when task was submitted.
	 * 
	 * @param timeSubmitted
	 *            Time (in milliseconds) when task was submitted.
	 */
	protected void setTimeSubmitted(long timeSubmitted) {
		this.timeSubmitted = timeSubmitted;
	}

	/**
	 * Set time (in milliseconds) when task was scheduled or zero if not
	 * scheduled yet.
	 * 
	 * @param timeScheduled
	 *            Time (in milliseconds) when task was scheduled or zero if not
	 *            started yet.
	 * @throws IllegalArgumentException
	 *             If <code>timeScheduled</code> is less than
	 *             <code>timeSubmitted</code>.
	 */
	protected void setTimeScheduled(long timeScheduled) {
		if ((timeScheduled == 0) || (timeScheduled >= this.timeSubmitted)) {
			this.timeScheduled = timeScheduled;
		} else {
			throw new IllegalArgumentException("timeScheduled is less than "
					+ "timeSubmitted");
		}
	}

	/**
	 * Set time (in milliseconds) when task was started or zero if not started
	 * yet.
	 * 
	 * @param timeStarted
	 *            Time (in milliseconds) when task was started or zero if not
	 *            started yet.
	 * @throws IllegalArgumentException
	 *             If <code>timeStarted</code> is less than
	 *             <code>timeScheduled</code>.
	 */
	protected void setTimeStarted(long timeStarted) {
		if ((timeStarted == 0) || (timeStarted >= this.timeScheduled)) {
			this.timeStarted = timeStarted;
		} else {
			throw new IllegalArgumentException("timeStarted is less than "
					+ "timeScheduled");
		}
	}

	/**
	 * Set time (in milliseconds) when task was finished or zero if not finished
	 * yet.
	 * 
	 * @param timeFinished
	 *            Time (in milliseconds) when task was finished or zero if not
	 *            finished yet.
	 * @throws IllegalArgumentException
	 *             If <code>timeFinished</code> is less than
	 *             <code>timeSubmitted</code>.
	 */
	protected void setTimeFinished(long timeFinished) {
		if ((timeFinished == 0) || (timeFinished >= this.timeSubmitted)) {
			// we must compare with timeSubmitted because in case of failure
			// task can reach state finisched and never scheduled or started
			this.timeFinished = timeFinished;
		} else {
			throw new IllegalArgumentException("timeFinished is less than "
					+ "timeSubmitted");
		}
	}

	/**
	 * Set how many times this task was restarted.
	 * 
	 * @param restartCount
	 *            How many times this task was restarted.
	 * @throws IllegalArgumentException
	 *             If <code>restartCount</code> is less than 0.
	 */
	protected void setRestartCount(int restartCount) {
		if (restartCount >= 0) {
			this.restartCount = restartCount;
		} else {
			throw new IllegalArgumentException("restartCount is less than 0");
		}
	}

	/**
	 * Set how many restarts for this task are allowed (before failed). Zero if
	 * not restricted.
	 * 
	 * @param restartMax
	 *            How many restarts for this task are allowed (before failed).
	 *            Zero if not restricted.
	 * @throws IllegalArgumentException
	 *             If <code>restartMax</code> is less than 0.
	 */
	private void setRestartMax(int restartMax) {
		if (restartMax >= 0) {
			this.restartMax = restartMax;
		} else {
			throw new IllegalArgumentException("restartMax is less than 0");
		}
	}

	/**
	 * Set how long (in milliseconds) this task can run (from
	 * <code>started</code> to <code>finished</code> state). Zero if not
	 * restricted.
	 * 
	 * @param timeoutRun
	 *            How long (in milliseconds) this task can run (from
	 *            <code>started</code> to <code>finishec</code> state). Zero if
	 *            not restricted.
	 * @throws IllegalArgumentException
	 *             If <code>timeoutRun</code> is less than 0.
	 */
	private void setTimeoutRun(long timeoutRun) {
		if (timeoutRun >= 0) {
			this.timeoutRun = timeoutRun;
		} else {
			throw new IllegalArgumentException("timeoutRun is less than 0");
		}
	}

	@Override
	public long getCurrentTime() {
		return this.currentTimeMillis;
	}

	@Override
	public String getTaskId() {
		return this.taskId;
	}

	@Override
	public String getContextId() {
		return this.contextId;
	}

	@Override
	public String getPackageName() {
		return this.packageName;
	}

	@Override
	public String getTaskName() {
		return this.taskName;
	}

	@Override
	public String getTaskDescription() {
		return this.taskDescription;
	}

	@Override
	public TaskTreeAddress getTreeAddress() {
		return treeAddress;
	}

	@Override
	public String getTreePath() {
		return treePath;
	}

	@Override
	public TaskDescriptor getOriginalTaskDescriptor() {
		return originalDescriptor;
	}

	@Override
	public TaskDescriptor getModifiedTaskDescriptor() {
		return modifiedDescriptor;
	}

	@Override
	public String getHostName() {
		return this.hostName;
	}

	@Override
	public String getDirectoryPathTask() {
		return this.directoryPathTask;
	}

	@Override
	public String getDirectoryPathWorking() {
		return this.directoryPathWorking;
	}

	@Override
	public String getDirectoryPathTemporary() {
		return this.directoryPathTemporary;
	}

	@Override
	public Properties getTaskProperties() {
		return this.taskProperties;
	}

	@Override
	public TaskExclusivity getExclusivity() {
		return this.exclusivity;
	}

	@Override
	public boolean getServiceFlag() {
		return this.serviceFlag;
	}

	@Override
	public TaskState getState() {
		return this.state;
	}

	@Override
	public long getTimeSubmitted() {
		return this.timeSubmitted;
	}

	@Override
	public long getTimeScheduled() {
		return this.timeScheduled;
	}

	@Override
	public long getTimeStarted() {
		return this.timeStarted;
	}

	@Override
	public long getTimeFinished() {
		return this.timeFinished;
	}

	@Override
	public int getRestartCount() {
		return this.restartCount;
	}

	@Override
	public int getRestartMax() {
		return this.restartMax;
	}

	@Override
	public long getTimeoutRun() {
		return this.timeoutRun;
	}

	/**
	 * Creates and returns a copy of this object. Does not copy
	 * <code>currentTime</code> but uses real current time to initialize it as
	 * used when constructing new object (i.e. clone is not fully identical with
	 * this object!).
	 */
	@Override
	public TaskEntryImplementation clone() throws CloneNotSupportedException {
		TaskEntryImplementation taskEntry = (TaskEntryImplementation) super
				.clone();

		/* Set current time. */
		taskEntry.currentTimeMillis = System.currentTimeMillis();

		return taskEntry;
	}
}
