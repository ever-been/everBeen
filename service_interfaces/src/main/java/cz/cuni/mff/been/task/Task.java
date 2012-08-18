package cz.cuni.mff.been.task;

import java.io.Serializable;
import java.util.Properties;

import org.apache.log4j.Logger;

import cz.cuni.mff.been.hostruntime.TasksPortInterface;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogLevel;
import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;

/**
 * BEEN task interface.
 * 
 * @author darklight
 * 
 */
public interface Task {
	/**
	 * Default log level.
	 */
	public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.INFO;

	// ----------------------- CONSTANTS -------------------------------
	/** Property name: directory with task package's content */
	public static final String PROP_DIR_TASK = "hostruntime.directory.task";
	/**
	 * Property name: working directory of a task, it IS NOT deleted immediately
	 * after task's end
	 */
	public static final String PROP_DIR_WORK = "hostruntime.directory.working";
	/**
	 * Property name: temporary directory of a task, it IS deleted immediately
	 * after task's end
	 */
	public static final String PROP_DIR_TEMP = "hostruntime.directory.temporary";
	/** Logging format */
	public static final String STDOUT_LOG_FORMAT = "%d %p [%c{1}]  %m";
	/** Task exit code: success */
	public static final int EXIT_CODE_SUCCESS = 0;
	/** Task exit code: error */
	public static final int EXIT_CODE_ERROR = 1;
	/** Name of the checkpoint used to signal task start. */
	public static final String CHECKPOINT_NAME_STARTED = "task started";
	/** Name of the checkpoint used to signal task finish. */
	public static final String CHECKPOINT_NAME_FINISHED = "task finished";

	/** Name of directory where to store pluggable modules */
	public static final String PLUGGABLE_MODULES_DIR = "pluggablemodules";

	/**
	 * Returns the tasksPort
	 * 
	 * @return the tasksPort
	 */
	public TasksPortInterface getTasksPort();

	/**
	 * Returns the directory where task can store its results (permanently)
	 * 
	 * @return directory name
	 */
	public String getWorkingDirectory();

	/**
	 * Returns the temporary working directory for the task
	 * 
	 * @return directory name
	 */
	public String getTempDirectory();

	/**
	 * Returns the directory with the task package's content
	 * 
	 * @return directory name
	 */
	public String getTaskDirectory();

	/**
	 * Sets the log level for logging to stdout
	 * 
	 * @param level
	 */
	public void setLogLevel(LogLevel level);

	/**
	 * @return log level for logging to stdout
	 */
	public LogLevel getLogLevel();

	/**
	 * Logs a message on the DEBUG debug level
	 * 
	 * @param message
	 */
	public void logDebug(String message);

	/**
	 * Logs a message on the ERROR debug level
	 * 
	 * @param message
	 */
	public void logError(String message);

	/**
	 * Logs a message on the FATAL debug level
	 * 
	 * @param message
	 */
	public void logFatal(String message);

	/**
	 * Logs a message on the INFO debug level
	 * 
	 * @param message
	 */
	public void logInfo(String message);

	/**
	 * Logs a message on the TRACE debug level
	 * 
	 * @param message
	 */
	public void logTrace(String message);

	/**
	 * Logs a message on the WARN debug level
	 * 
	 * @param message
	 */
	public void logWarning(String message);

	/**
	 * Retrieve logger that is used by the task.
	 * 
	 * @return logger used by the task.
	 */
	public Logger getLogger();

	/**
	 * Signals that a checkpoint was reached by the task.
	 * 
	 * @param name
	 *            name of the checkpoint
	 * @param value
	 *            value of the checkpoint
	 * @throws TaskException
	 *             if anything goes wrong.
	 */
	public void checkPointReached(String name, Serializable value)
			throws TaskException;

	/**
	 * Waits until a task reaches a checkpoint of specified type and returns its
	 * value.
	 * 
	 * @param contextID
	 *            context ID, if null then the context of the calling task will
	 *            be used.
	 * @param taskID
	 *            task ID
	 * @param name
	 *            checkpoint name
	 * @param timeout
	 *            number of milliseconds to wait for a checkpoint;
	 *            <code>0</code> means that the call will not block and returns
	 *            immediately
	 * @return value of the checkpoint.
	 * @throws TaskException
	 *             if anything goes wrong.
	 */
	public Serializable checkPointWait(
			String contextID,
			String taskID,
			String name,
			long timeout) throws TaskException;

	/**
	 * Test if given property is null or not. This can be used to test presence
	 * of a property, however cannot be used in all cases since null may be
	 * valid property value.
	 * 
	 * @param name
	 *            Name of the property to test.
	 * 
	 * @return true if property is null, false if property is not null.
	 */
	public boolean isPropertyNull(String name);

	/**
	 * Get all properties of the task.
	 * 
	 * @return all properties of the task.
	 */
	public Properties getTaskProperties();

	/**
	 * Returns the value of a task's property
	 * 
	 * @param name
	 *            name of the task's property
	 * @return value of the task's property or <code>null</code> if the
	 *         specified property is not found
	 */
	public String getTaskProperty(String name);

	/**
	 * Get value of the Task's property.
	 * 
	 * @param name
	 *            Name of the property to retrieve.
	 * @param defaultValue
	 *            Default value which will be used when property is not set (if
	 *            it is null).
	 * 
	 * @return Value of the property of default value.
	 */
	public String getTaskProperty(String name, String defaultValue);

	/**
	 * Returns the value of a task property that can have boolean values, e.g.
	 * "yes", "true" etc.
	 * 
	 * @param name
	 *            name of the task property.
	 * @return true if the value of the task property is was "yes" or "true".
	 */
	public boolean getBooleanTaskProperty(String name);

	/**
	 * @param name
	 *            name of the task property.
	 * @return the object value of the task property.
	 */
	public Serializable getTaskPropertyObject(String name);

	/**
	 * @return the task descriptor of this task.
	 */
	public TaskDescriptor getTaskDescriptor();

	/**
	 * 
	 * @return true if the task is running in Windows.
	 */
	public boolean isRunningInWindows();

	/**
	 * 
	 * @return true if the task is running in Linux.
	 */
	public boolean isRunningInLinux();

	/**
	 * Shortcut method for getting pluggable modules easily.
	 * 
	 * @param name
	 *            name of pluggable module
	 * @param version
	 *            version of pluggable module
	 * @return pluggable module of given name and version
	 * @throws PluggableModuleException
	 */
	public PluggableModule getPluggableModule(String name, String version)
			throws TaskException, PluggableModuleException;

	/**
	 * Shortcut method for getting pluggable modules easily. No need to typecast
	 * result when using this method.
	 * 
	 * @param name
	 *            name of pluggable module
	 * @param version
	 *            version of pluggable module
	 * @return pluggable module of given name and version
	 * @throws PluggableModuleException
	 */
	public <T> T
			getPluggableModule(Class<T> clazz, String name, String version)
					throws TaskException, PluggableModuleException;

	/**
	 * Returns instance of pluggable module manager. Manager won't get created
	 * until first call of this routine (lazy creating), otherwise some tasks
	 * will not be able to start at all.
	 * 
	 * @return pluggable module manager associated with task
	 */
	public PluggableModuleManager getPluggableModuleManager()
			throws TaskException;

}
