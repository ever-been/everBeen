/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Jaroslav Urban
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
package cz.cuni.mff.been.task;

import java.io.File;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import cz.cuni.mff.been.hostruntime.TasksPortInterface;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.HostRuntimeAppender;
import cz.cuni.mff.been.logging.LogLevel;
import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManagerImpl;



/**
 * Class representing a Task, which is the entity executed by BEEN
 * 
 * @author Michal Tomcanyi
 */
public abstract class AbstractTask implements Task {

	/** Task monitoring and logging facility */
	private TasksPortInterface tasksPort = null;
	/** Task descriptor of this task. */
	private TaskDescriptor taskDescriptor = null;
	/**
	 * Directory specified by Host Runtime which contains the task package's
	 * content
	 */
	private final String taskDirectory = System.getProperty(PROP_DIR_TASK);
	/**
	 * Working directory specified by Host Runtime for this task, it IS NOT
	 * deleted immediately after task's end
	 */
	private final String workingDirectory = System
			.getProperty(PROP_DIR_WORK);
	/**
	 * Temporary directory for this task specified by Host Runtime, it IS
	 * deleted immediately after task's end
	 */
	private final String tempDirectory = System.getProperty(PROP_DIR_TEMP);

	/** Task properties that can be used instead of command line arguments */
	private Properties taskProperties;
	/** Task properties with objects as values. */
	private Map<String, Serializable> taskPropertyObjects;

	private final Logger logger = Logger.getLogger("task."
			+ this.getClass().getCanonicalName());

	/**
	 * Pluggable module manager of task. Can be used for loading pluggable
	 * modules
	 */
	private PluggableModuleManager pluggableModuleManager;

	protected AbstractTask() throws TaskInitializationException {
		CurrentTaskSingleton.setTaskHandle(this);

		// obtain monitor for the task
		try {
			tasksPort = (TasksPortInterface) Naming.lookup(System
					.getProperty("hostruntime.tasksport.uri"));
			taskProperties = tasksPort.getTaskProperties();
			taskPropertyObjects = tasksPort.getTaskPropertyObjects();
			taskDescriptor = tasksPort.getTaskDescriptor();
		} catch (Exception e) {
			throw new TaskInitializationException(e);
		}

		// check if directories were correctly set
		if (tempDirectory == null) {
			throw new TaskInitializationException(
					"Temporary directory not set (check property "
							+ PROP_DIR_TEMP + ")");
		}

		if (workingDirectory == null) {
			throw new TaskInitializationException(
					"Working directory not set (check property "
							+ PROP_DIR_WORK + ")");
		}
		if (taskDirectory == null) {
			throw new TaskInitializationException(
					"Task directory not set (check property "
							+ PROP_DIR_TASK + ")");
		}

		// set-up logging
		Logger taskRootLogger = Logger.getLogger("task");
		// set additivity to false, we don't want to log to stdout (the root
		// logger has an appender
		// logging to stdout, which was assigned to it by the BeenLogger class)
		taskRootLogger.setAdditivity(false);
		taskRootLogger.addAppender(new HostRuntimeAppender(this));
		setLogLevel(DEFAULT_LOG_LEVEL);

		for (Object key : taskProperties.keySet()) {
			logDebug("Task property " + (String) key + "="
					+ taskProperties.getProperty((String) key));
		}

	}

	/**
	 * Returns the tasksPort
	 * 
	 * @return the tasksPort
	 */
	@Override
	public TasksPortInterface getTasksPort() {
		return tasksPort;
	}

	/**
	 * Returns the directory where task can store its results (permanently)
	 * 
	 * @return directory name
	 */
	@Override
	public String getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * Returns the temporary working directory for the task
	 * 
	 * @return directory name
	 */
	@Override
	public String getTempDirectory() {
		return tempDirectory;
	}

	/**
	 * Returns the directory with the task package's content
	 * 
	 * @return directory name
	 */
	@Override
	public String getTaskDirectory() {
		return taskDirectory;
	}

	/**
	 * Sets the log level for logging to stdout
	 * 
	 * @param level
	 */
	@Override
	public void setLogLevel(LogLevel level) {
		logger.setLevel(level.toLog4jLevel());
	}

	/**
	 * @return log level for logging to stdout
	 */
	@Override
	public LogLevel getLogLevel() {
		return LogLevel.getInstance(logger.getLevel());
	}

	/**
	 * Logs a message on the DEBUG debug level
	 * 
	 * @param message
	 */
	@Override
	public void logDebug(String message) {
		logger.debug(message);
	}

	/**
	 * Logs a message on the ERROR debug level
	 * 
	 * @param message
	 */
	@Override
	public void logError(String message) {
		logger.error(message);
	}

	/**
	 * Logs a message on the FATAL debug level
	 * 
	 * @param message
	 */
	@Override
	public void logFatal(String message) {
		logger.fatal(message);
	}

	/**
	 * Logs a message on the INFO debug level
	 * 
	 * @param message
	 */
	@Override
	public void logInfo(String message) {
		logger.info(message);
	}

	/**
	 * Logs a message on the TRACE debug level
	 * 
	 * @param message
	 */
	@Override
	public void logTrace(String message) {
		logger.trace(message);
	}

	/**
	 * Logs a message on the WARN debug level
	 * 
	 * @param message
	 */
	@Override
	public void logWarning(String message) {
		logger.warn(message);
	}

	/**
	 * Retrieve logger that is used by the task.
	 * 
	 * @return logger used by the task.
	 */
	@Override
	public Logger getLogger() {

		return logger;
	}

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
	@Override
	public void checkPointReached(String name, Serializable value)
			throws TaskException {
		try {
			tasksPort.checkPointReached(name, value);
		} catch (RemoteException e) {
			throw new TaskException(e);
		}
	}

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
	@Override
	public Serializable checkPointWait(
			String contextID,
			String taskID,
			String name,
			long timeout) throws TaskException {
		try {
			return tasksPort.checkPointWait(contextID, taskID, name, timeout);
		} catch (RemoteException e) {
			throw new TaskException(e);
		}
	}

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
	@Override
	public boolean isPropertyNull(String name) {

		return taskProperties.getProperty(name) == null;
	}

	/**
	 * Get all properties of the task.
	 * 
	 * @return all properties of the task.
	 */
	@Override
	public Properties getTaskProperties() {

		return taskProperties;
	}

	/**
	 * Returns the value of a task's property
	 * 
	 * @param name
	 *            name of the task's property
	 * @return value of the task's property or <code>null</code> if the
	 *         specified property is not found
	 */
	@Override
	public String getTaskProperty(String name) {
		return taskProperties.getProperty(name);
	}

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
	@Override
	public String getTaskProperty(String name, String defaultValue) {

		String value = getTaskProperty(name);

		if (value == null) {
			value = defaultValue;
		}

		return value;
	}

	/**
	 * Returns the value of a task property that can have boolean values, e.g.
	 * "yes", "true" etc.
	 * 
	 * @param name
	 *            name of the task property.
	 * @return true if the value of the task property is was "yes" or "true".
	 */
	@Override
	public boolean getBooleanTaskProperty(String name) {
		String value = getTaskProperty(name);
		return (!(value == null) && ((value.equals("yes")) || (value
				.equals("true"))));
	}

	/**
	 * @param name
	 *            name of the task property.
	 * @return the object value of the task property.
	 */
	@Override
	public Serializable getTaskPropertyObject(String name) {
		return taskPropertyObjects.get(name);
	}

	/**
	 * Exits the task which successfully finished.
	 */
	public static void exitSuccess() {
		System.exit(EXIT_CODE_SUCCESS);
	}

	/**
	 * Exits the task that finished with an error.
	 * 
	 */
	public static void exitError() {
		System.exit(EXIT_CODE_ERROR);
	}

	/**
	 * @return the task descriptor of this task.
	 */
	@Override
	public TaskDescriptor getTaskDescriptor() {
		return this.taskDescriptor;
	}

	/**
	 * 
	 * @return true if the task is running in Windows.
	 */
	@Override
	public boolean isRunningInWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("windows") != -1;
	}

	/**
	 * 
	 * @return true if the task is running in Linux.
	 */
	@Override
	public boolean isRunningInLinux() {
		return System.getProperty("os.name").toLowerCase().indexOf("linux") != -1;
	}

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
	@Override
	public PluggableModule getPluggableModule(String name, String version)
			throws TaskException, PluggableModuleException {
		return getPluggableModuleManager().getModule(
				new PluggableModuleDescriptor(name, version));
	}

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
	@Override
	public <T> T
			getPluggableModule(Class<T> clazz, String name, String version)
					throws TaskException, PluggableModuleException {
		return clazz.cast(getPluggableModule(name, version));
	}

	/**
	 * Returns instance of pluggable module manager. Manager won't get created
	 * until first call of this routine (lazy creating), otherwise some tasks
	 * will not be able to start at all.
	 * 
	 * @return pluggable module manager associated with task
	 */
	@Override
	public PluggableModuleManager getPluggableModuleManager()
			throws TaskException {
		if (pluggableModuleManager == null)
			initializePluggableModuleManager();
		return pluggableModuleManager;
	}

	/**
	 * Initializes pluggable module manager.
	 */
	private void initializePluggableModuleManager() throws TaskException {
		try {

			String modulesDir = taskDirectory + File.separator
					+ PLUGGABLE_MODULES_DIR;
			pluggableModuleManager = new PluggableModuleManagerImpl(
					tasksPort,
					modulesDir);
		} catch (Exception e) {
			logInfo(e.toString());
			e.printStackTrace();
			throw new TaskException(
					"Error initializing pluggable module manager.");
		}
	}
}
