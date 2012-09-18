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
package cz.cuni.mff.been.taskmanager;

import java.io.Serializable;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import cz.cuni.mff.been.jaxb.td.LoadMonitoring;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.logging.OutputHandle;
import cz.cuni.mff.been.taskmanager.data.ContextEntry;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.taskmanager.tasktree.IllegalAddressException;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeAddress;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeInput;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeQuery;

/**
 * Main interface of the Task Manager (used to control of it).
 * 
 * @author Antonin Tomecek
 */
public interface TaskManagerInterface extends Remote {

	/** ID of system context. */
	static final String SYSTEM_CONTEXT_ID = "system";
	/** Name of system context. */
	static final String SYSTEM_CONTEXT_NAME = "System context";
	/** Description of system context. */
	static final String SYSTEM_CONTEXT_DESCRIPTION = "Context for running system components of BEEN.";

	/** URL for control RMI interface. */
	static final String URL = "/been/taskmanager";

	/** Name of configuration file for Task Manager. */
	static final String CONFIGURATION_FILE = "configuration.xml";

	/** Name of log file for Task Manager (failure recovery). */
	static final String LOG_FILE = "log.xml";

	/** Default value of the size limit of the Host Runtime's package cache. */
	static final long DEFAULT_MAX_PACKAGE_CACHE_SIZE = 1024 * 1024 * 1024; // 1
	// GB

	/**
	 * Default value of the number of closed contexts, for which the Host Runtime
	 * should keep data on the disk.
	 */
	static final int DEFAULT_KEPT_CLOSED_CONTEXT_COUNT = 3;

	/**
	 * Infinite time (especially used for methods with timeout).
	 */
	static final long INFINITE_TIME = -1;
	/**
	 * "Fake" task name for the Task Manager. It's used for storing the Task
	 * Manager's logs and output in the log storage.
	 */
	static final String TASKMANAGER_TASKNAME = "taskmanager";

	/** Default number of load units per task. */
	static final int DEFAULT_LOAD_UNITS = new LoadMonitoring().getLoadUnits(); // Read
	// default
	// value
	// from
	// schema.

	/**
	 * End working of Task Manager.
	 */
	void stopTaskManager() throws RemoteException;

	/**
	 * Task tree input interface getter. Useful for task generators and other
	 * animals who modify the tree.
	 * 
	 * @return A reference to the task tree associated with this task manager.
	 */
	TaskTreeInput getTaskTreeInput() throws RemoteException;

	/**
	 * A read-only query interface that relays queries to the task tree.
	 * 
	 * @return A read-only tree query interface associated with this task manager.
	 */
	TaskTreeQuery getTaskTreeQuery() throws RemoteException;

	/**
	 * Creates a new Task ID to be used when creating new task descriptors.
	 * 
	 * @return new ID that is reasonably unique within whole BEEN framework
	 */
	String getUniqueTaskID() throws RemoteException;

	/**
	 * Run (schedule) one new task specified by its task descriptor.
	 * 
	 * @param taskDescriptor
	 *          Task descriptor of new task to run.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	void runTask(TaskDescriptor taskDescriptor) throws RemoteException;

	/**
	 * Create new context (non-self-cleaning).
	 * 
	 * @param name
	 *          Human readable name of context.
	 * @param description
	 *          Human readable description of context.
	 * @param magicObject
	 *          Some magic object (Serializable and Cloneable).
	 * @return ID of context.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	String newContext(String name, String description, Serializable magicObject) throws RemoteException;

	/**
	 * Create new context (non-self-cleaning).
	 * 
	 * @param id
	 *          ID of context.
	 * @param name
	 *          Human readable name of context.
	 * @param description
	 *          Human readable description of context.
	 * @param magicObject
	 *          Some magic object (Serializable and Cloneable).
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	void newContext(String id, String name, String description,
			Serializable magicObject) throws RemoteException;

	/**
	 * Create new context.
	 * 
	 * @param id
	 *          ID of context.
	 * @param name
	 *          Human readable name of context.
	 * @param description
	 *          Human readable description of context.
	 * @param magicObject
	 *          Some magic object (Serializable and Cloneable).
	 * @param selfCleaning
	 *          Whether context should limit count of finished tasks in it
	 *          automatically
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	void newContext(String id, String name, String description,
			Serializable magicObject, boolean selfCleaning) throws RemoteException;

	/**
	 * Close context.
	 * 
	 * @param id
	 *          ID of context.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	void closeContext(String id) throws RemoteException;

	/**
	 * Deletes a context. Kills all it's tasks and removes it from the log
	 * storage.
	 * 
	 * @param id
	 *          id of the context
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	void killAndDeleteContext(String id) throws RemoteException;

	/**
	 * Return informations about all tasks known inside Task Manager.
	 * 
	 * @return Array containing TaskEntry for each task.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	TaskEntry[] getTasks() throws RemoteException;

	/**
	 * Return informations about all tasks known inside Task Manager as member of
	 * specified context.
	 * 
	 * @param contextId
	 *          ID of requested context.
	 * @return Array conraining TaskEntry for each convenient task.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	TaskEntry[] getTasksInContext(String contextId) throws RemoteException;

	/**
	 * Return informations about all tasks known inside Task Manager as scheduled
	 * on specified HostRuntime.
	 * 
	 * @param hostName
	 *          URI of requested Host Runtime.
	 * @return Array containing TaskEntry for each convenient task.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	TaskEntry[] getTasksOnHost(String hostName) throws RemoteException;

	/**
	 * Return informations about all contexts known inside Task Manager.
	 * 
	 * @return Array containing ContextEntry for each context.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	ContextEntry[] getContexts() throws RemoteException;

	/**
	 * Return informations about one task specified by its ID.
	 * 
	 * @param taskId
	 *          ID of requested task.
	 * @return TaskEntry filled in by informations about requested task (null if
	 *         requested task not found).
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	TaskEntry getTaskById(String taskId, String contextId) throws RemoteException;

	/**
	 * Return informations about one context specified by its ID.
	 * 
	 * @param contextId
	 *          ID of requested context.
	 * @return ContextEntry filled in by informations about requested context
	 *         (null if requested task not found).
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	ContextEntry getContextById(String contextId) throws RemoteException;

	/**
	 * Kill task specified by its ID.
	 * 
	 * @param taskId
	 *          ID of task.
	 * @param contextId
	 *          ID of context.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	void killTaskById(String taskId, String contextId) throws RemoteException;

	/**
	 * Kill a task specified by its tree address.
	 * 
	 * @param address
	 *          Address of the task in the semantic tree.
	 * @throws RemoteException
	 *           When it rains.
	 * @throws IllegalAddressException
	 *           When the address does not exist or leads to a node.
	 */
	void killTaskByAddress(TaskTreeAddress address) throws RemoteException, IllegalAddressException;

	/**
	 * Kill all tasks within specified context and that context.
	 * 
	 * @param contextId
	 *          ID of context.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	void killContextById(String contextId) throws RemoteException;

	/**
	 * Kill a whole task tree node recursively by its tree address.
	 * 
	 * @param address
	 *          Address of the node in the semantic tree.
	 * @throws RemoteException
	 *           When it rains.
	 * @throws IllegalAddressException
	 *           When the address does not exist.
	 */
	void killNodeByAddress(TaskTreeAddress address) throws RemoteException, IllegalAddressException;

	/**
	 * Kill and delete a whole task tree node by its tree address.
	 * 
	 * @param address
	 *          Address of the node in the semantic tree
	 * @throws IllegalAddressException
	 *           When the address does not exist.
	 * @throws RemoteException
	 *           When it rains.
	 */
	void deleteNodeByAddress(TaskTreeAddress address) throws RemoteException, IllegalAddressException;

	/**
	 * Kill and delete a task specified by its tree address.
	 * 
	 * @param address
	 *          Address of the task in the semantic tree.
	 * @throws RemoteException
	 *           When it rains.
	 * @throws IllegalAddressException
	 *           When he address does not exist.
	 */
	void deleteTaskByAddress(TaskTreeAddress address) throws RemoteException, IllegalAddressException;

	/**
	 * Run (schedule) one or more new tasks specified by their task descriptors
	 * (XML form).
	 * 
	 * @param taskDescriptorPaths
	 *          Array containing paths to the XML representation of Task
	 *          Descriptors.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	void runTask(String... taskDescriptorPaths) throws RemoteException;

	/**
	 * Run (schedule) a collection of tasks specified by their XML task
	 * descriptors.
	 * 
	 * @param taskDescriptorPaths
	 *          {@link Collection} containing paths to XML task descriptors
	 * 
	 * @throws RemoteException
	 *           On failure while scheduling the tasks.
	 */
	void runTasks(Collection<String> taskDescriptorPaths) throws RemoteException;

	/**
	 * Run (schedule) one or more new tasks specified by their task descriptors.
	 * 
	 * @param taskDescriptors
	 *          Task descriptors of new tasks to run.
	 * @throws RemoteException
	 *           If something failed during this operation.
	 */
	void runTask(TaskDescriptor... taskDescriptors) throws RemoteException;

	/**
	 * Do check point lookup. Tasks should not call this. Returns last value of
	 * reached checkpoints matching specified taskId, contextId and name. All
	 * values must be non-null and must match.
	 * 
	 * Calling of this method is blocking. If timeout is set to zero, then return
	 * immediately.
	 * 
	 * @param name
	 *          Name of checkpoint.
	 * @param taskId
	 *          ID of task which reached checkpoint.
	 * @param contextId
	 *          ID of context in which checkpoint was reached.
	 * @param timeout
	 *          Maximum time to wait in milliseconds.
	 * 
	 * @return Value of specified checkpoint (can be <code>null</code>).
	 * @throws NullPointerException
	 *           If some input parameter is null.
	 * @throws IllegalArgumentException
	 *           If checkpoint not found.
	 * @throws TaskManagerException
	 *           If Required checkPoint can not be reached anyway
	 * @throws RemoteException
	 *           If something failed during the execution of the remote method
	 *           call.
	 */
	Serializable checkPointLook(String name, String taskId, String contextId,
			long timeout) throws TaskManagerException, RemoteException;

	/**
	 * Do check point lookup. Tasks should not call this. Returns array containing
	 * all reached checkpoints matching specified checkpointTemplate. All filled
	 * in values must match. Values set to null are arbitrary. Calling of this
	 * method is blocking. If timeout is set to zero, then return immediately.
	 * 
	 * @param checkpointTemplate
	 *          Prepared template for checkpoint match.
	 * @param timeout
	 *          Maximum time to wait in milliseconds.
	 * @return Array containing all checkpoint matching specified
	 *         checkpointTemplate.
	 * @throws TaskManagerException
	 *           If Required checkPoint can not be reached anyway
	 * @throws RemoteException
	 *           If something failed during the execution of the remote method
	 *           call.
	 */
	CheckPoint[] checkPointLook(CheckPoint checkpointTemplate, long timeout) throws TaskManagerException, RemoteException;

	/**
	 * Used for looking in service entries. Tasks should not call this. Returns
	 * array containing all service entries matching specified template (using
	 * regular expressions).
	 * 
	 * @param serviceTemplate
	 *          Object looked describing services (using regular expressions).
	 * @throws RemoteException
	 *           If something failed during the execution of the remote method
	 *           call.
	 */
	ServiceEntry[] serviceLook(ServiceEntry serviceTemplate) throws RemoteException;

	/**
	 * Used for finding some registered remote interface. Tasks should not call
	 * this. Given names are compared for exact match (doesn't use regular
	 * expressions).
	 * 
	 * @param serviceName
	 *          Name of service.
	 * @param interfaceName
	 *          Name of service's interface.
	 * @return Remote representation of one from all matching interfaces or null
	 *         if none.
	 * @throws RemoteException
	 *           If something failed during the execution of the remote method
	 *           call.
	 */
	Remote serviceFind(String serviceName, String interfaceName) throws RemoteException;

	/**
	 * Used for finding some registered remote interface. Tasks should not call
	 * this. Given names are compared for exact match (doesn't use regular
	 * expressions).
	 * 
	 * @param serviceName
	 *          Name of service.
	 * @param interfaceName
	 *          Name of service's interface.
	 * @return URI representation of one from all matching interfaces or null if
	 *         none.
	 * @throws RemoteException
	 *           If something failed during the execution of the remote method
	 *           call.
	 */

	URI serviceFindURI(String serviceName, String interfaceName) throws RemoteException;

	/**
	 * Gets all log messages produced by a task.
	 * 
	 * @param context
	 *          task's context; cannot be null or an empty string.
	 * @param taskID
	 *          task's ID; cannot be null or an empty string.
	 * @return logs of the task.
	 * @throws RemoteException
	 *           If something failed during the execution of the remote method
	 *           call.
	 * @throws LogStorageException
	 *           If an error occured while retrieving the logs.
	 * @throws IllegalArgumentException
	 *           If the the context name or task's ID are empty strings or if the
	 *           task wasn't run.
	 * @throws NullPointerException
	 *           If any argument is <tt>null</tt>.
	 */
	LogRecord[] getLogsForTask(String context, String taskID) throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException;

	/**
	 * Gets all log messages produced by a task.
	 * 
	 * @param address
	 *          Address of the task in the semantic tree.
	 * @return Log records for the task.
	 * @throws RemoteException
	 *           When it rains.
	 * @throws LogStorageException
	 *           If an error occured while retrieving the logs.
	 * @throws IllegalAddressException
	 *           When the address does not exist or points to a node.
	 */
	LogRecord[] getLogsForTask(TaskTreeAddress address) throws RemoteException, LogStorageException, IllegalAddressException;

	/**
	 * Gets task's log messages which belong to an interval. The interval is
	 * specified by log message indexes. To get the total number of log messages
	 * of a task, use <code>getLogCountForTask</code>. The task must've been
	 * registered in the log storage with <code>addTask</code> before.
	 * 
	 * @param context
	 *          task's context; cannot be null or an empty string.
	 * @param taskID
	 *          task's ID; cannot be null or an empty string.
	 * @param first
	 *          the index of the first log message; it specifies the beginning of
	 *          the interval of log messages that will be returned. The index
	 *          begins from 0.
	 * @param last
	 *          the index of the last log message; it specifies the end of the
	 *          interval of log messages that will be returned. The index begins
	 *          from 0.
	 * @return logs of the task.
	 * @throws RemoteException
	 * @throws LogStorageException
	 *           if an error occured while retrieving the logs.
	 * @throws IllegalArgumentException
	 *           if the the context name or task's ID are empty strings or if the
	 *           task wasn't registered by <code>addTask()</code> yet.
	 * @throws NullPointerException
	 *           if any argument is <tt>null</tt>.
	 * @see #getLogCountForTask(String, String)
	 */
	LogRecord[] getLogsForTask(String context, String taskID, long first,
			long last) throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException;

	/**
	 * Gets all log messages produced by a task.
	 * 
	 * @param address
	 *          Address of the task in the semantic tree.
	 * @param first
	 *          Index of the first log message.
	 * @param last
	 *          Index of the last log message.
	 * @return Log records for the task.
	 * @throws RemoteException
	 *           When it rains.
	 * @throws LogStorageException
	 *           If an error occured while retrieving the logs.
	 * @throws IllegalAddressException
	 *           When the address does not exist or points to a node.
	 */
	LogRecord[] getLogsForTask(TaskTreeAddress address, long first, long last) throws RemoteException, LogStorageException, IllegalAddressException;

	/**
	 * Returns the number of log messages stored for a task. The task must've been
	 * registered in the log storage with <code>addTask</code> before.
	 * 
	 * @param context
	 *          task's context; cannot be null or an empty string.
	 * @param taskID
	 *          task's ID; cannot be null or an empty string.
	 * @return number of log messages.
	 * @throws RemoteException
	 * @throws LogStorageException
	 *           if an error occured while counting the log messages.
	 * @throws IllegalArgumentException
	 *           if the the context name or task's ID are empty strings or if the
	 *           task wasn't registered by <code>addTask()</code> yet.
	 * @throws NullPointerException
	 *           if any argument is <tt>null</tt>.
	 */
	long getLogCountForTask(String context, String taskID) throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException;

	/**
	 * Returns the number of log messages stored for a task. The task must have
	 * been registered in the log storage with <code>addTask</code> before.
	 * 
	 * @param address
	 *          Address of the task in the semantic tree.
	 * @return Number of log messages.
	 * @throws RemoteException
	 *           When it rains.
	 * @throws LogStorageException
	 *           If an error occured while retrieving the logs.
	 * @throws IllegalAddressException
	 *           When the address does not exist or points to a node.
	 */
	long getLogCountForTask(TaskTreeAddress address) throws RemoteException, LogStorageException, IllegalAddressException;

	/**
	 * Checks whether the task was already registered in the log storage.
	 * 
	 * @param context
	 *          context of the task; cannot be null or an empty string.
	 * @param taskID
	 *          task's ID; cannot be null or an empty string.
	 * @return true if the task was already registered, false otherwise.
	 * @throws RemoteException
	 *           If something failed during the execution of the remote method
	 *           call.
	 * @throws LogStorageException
	 *           If an error occured checking the registration.
	 * @throws IllegalArgumentException
	 *           If the name of the context or the task's ID are empty or if the
	 *           context wasn't found.
	 * @throws NullPointerException
	 *           If the name of the context or the task's ID is <tt>null</tt>.
	 */
	boolean isTaskRegistered(String context, String taskID) throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException;

	/**
	 * Checks whether the task was already registered in the log storage.
	 * 
	 * @param address
	 *          Address of the task in the semantic tree.
	 * @return true if the task was already registered, false otherwise.
	 * @throws RemoteException
	 *           When it rains.
	 * @throws LogStorageException
	 *           If an error occured while retrieving the logs.
	 * @throws IllegalAddressException
	 *           When the address does not exist or points to a node.
	 */
	boolean isTaskRegistered(TaskTreeAddress address) throws RemoteException, LogStorageException, IllegalAddressException;

	/**
	 * Checks whether the context was already registered in the log storage.
	 * 
	 * @param name
	 *          context name; cannot be null or an empty string.
	 * @throws RemoteException
	 *           If something failed during the execution of the remote method
	 *           call.
	 * @throws LogStorageException
	 *           If an error occured while checking the registration.
	 * @throws IllegalArgumentException
	 *           If the name of the context is empty.
	 * @throws NullPointerException
	 *           If the name of the context is <tt>null</tt>.
	 * @return true If the context was already registered, false otherwise.
	 */
	boolean isContextRegistered(String name) throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException;

	/**
	 * Creates a handle for receiving the standard output of a task.
	 * 
	 * @param context
	 *          task's context; cannot be null or an empty string.
	 * @param taskID
	 *          task's ID; cannot be null or an empty string.
	 * @return handle for retrievind the standard output.
	 * @throws RemoteException
	 *           If something failed during the execution of the remote method
	 *           call.
	 * @throws LogStorageException
	 *           If an error occured while creating the handle.
	 * @throws IllegalArgumentException
	 *           If the the context name or task's ID are empty or if the task or
	 *           it's context wasn't found.
	 * @throws NullPointerException
	 *           If any argument is <tt>null</tt>.
	 */
	OutputHandle getStandardOutput(String context, String taskID) throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException;

	/**
	 * Creates a handle for receiving the standard output of a task.
	 * 
	 * @param address
	 *          Address of the task in the semantic tree.
	 * @return A handle for retrievind the standard output.
	 * @throws RemoteException
	 *           When it rains.
	 * @throws LogStorageException
	 *           If an error occured while retrieving the logs.
	 * @throws IllegalAddressException
	 *           When the address does not exist or points to a node.
	 */
	OutputHandle getStandardOutput(TaskTreeAddress address) throws RemoteException, LogStorageException, IllegalAddressException;

	/**
	 * Creates a handle for receiving the error output of a task.
	 * 
	 * @param context
	 *          task's context; cannot be null or an empty string.
	 * @param taskID
	 *          task's ID; cannot be null or an empty string.
	 * @return handle for retrievind the error output.
	 * @throws RemoteException
	 *           If something failed during the execution of the remote method
	 *           call.
	 * @throws LogStorageException
	 *           If an error occured while creating the handle.
	 * @throws IllegalArgumentException
	 *           If the the context name or task's ID are empty or if the task or
	 *           it's context wasn't found.
	 * @throws NullPointerException
	 *           If any argument is <tt>null</tt>.
	 */
	OutputHandle getErrorOutput(String context, String taskID) throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException;

	/**
	 * Creates a handle for receiving the error output of a task.
	 * 
	 * @param address
	 *          Address of the task in the semantic tree.
	 * @return A handle for retrievind the error output.
	 * @throws RemoteException
	 *           When it rains.
	 * @throws LogStorageException
	 *           If an error occured while retrieving the logs.
	 * @throws IllegalAddressException
	 *           When the address does not exist or points to a node.
	 */
	OutputHandle getErrorOutput(TaskTreeAddress address) throws RemoteException, LogStorageException, IllegalAddressException;

	/**
	 * Returns the size limit of the Host Runtime's package cache.
	 * 
	 * @return size limit of the Host Runtime's package cache.
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	long getMaxPackageCacheSize() throws RemoteException;

	/**
	 * Sets the size limit of the Host Runtime's package cache. The setting will
	 * be applied after downloading next package to the cache.
	 * 
	 * @param maxPackageCacheSize
	 *          the size limit of the Host Runtime's package cache
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	void setMaxPackageCacheSize(long maxPackageCacheSize) throws RemoteException;

	/**
	 * Returns the number of closed contexts, for which the Host Runtime should
	 * keep data on the disk
	 * 
	 * @return number of closed contexts, for which the Host Runtime should keep
	 *         data on the disk
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	int getKeptClosedContextCount() throws RemoteException;

	/**
	 * Sets the number of closed contexts, for which the Host Runtime should keep
	 * data on the disk. The setting will be applied after next context close
	 * operation.
	 * 
	 * @param keptClosedContextCount
	 *          number of closed contexts, for which the Host Runtime should keep
	 *          data on the disk
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	void setKeptClosedContextCount(int keptClosedContextCount) throws RemoteException;

	/**
	 * Registers new Host Runtime as available for running tasks and notifies
	 * registered listeners about this event.
	 * 
	 * It is not an error to register same Host Runtime twice, as the Host Runtime
	 * has no way of knowing if the host was alreday registered.
	 * 
	 * @param hostname
	 *          host where the new Host Runtime is running
	 * @throws NullPointerException
	 *           if the hostname is null
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	void registerHostRuntime(String hostname) throws RemoteException;

	/**
	 * Unregisters existing Host Runtime as available for running tasks and
	 * notifies registered listeners about this event. It is an error to
	 * unregister the same Host Runtime twice.
	 * 
	 * @param hostName
	 *          host where the Host Runtime is running
	 * @throws IllegalArgumentException
	 *           if the Host Runtime was not registered
	 * @throws NullPointerException
	 *           if the hostname is null
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	void unregisterHostRuntime(String hostName) throws RemoteException;

	/**
	 * Registers new listener of the Task Manager events.
	 * 
	 * @see HostRuntimeRegistrationListener
	 * @param listener
	 *          RMI reference to the listener
	 * @throws IllegalArgumentException
	 *           if the listener was already registered
	 * @throws NullPointerException
	 *           if the listener is <code>null</code>
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	void registerEventListener(HostRuntimeRegistrationListener listener) throws RemoteException;

	/**
	 * Registers new listener of the Task Manager events.
	 * 
	 * @see TaskEventListener
	 * @param listener
	 *          instance to the listener
	 * @throws NullPointerException
	 *           if the listener is <code>null</code>
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	void registerEventListener(TaskEventListener listener) throws RemoteException;

	/**
	 * Unregisters existing listener of the Task Manager events.
	 * 
	 * @param listener
	 *          RMI reference to the listener
	 * @throws IllegalArgumentException
	 *           if the listener was not registered
	 * @throws NullPointerException
	 *           if the listener is <code>null</code>
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	void unregisterEventListener(HostRuntimeRegistrationListener listener) throws RemoteException;

	/**
	 * Unregisters existing listener from the Task Manager.
	 * 
	 * @see TaskEventListener
	 * @param listener
	 *          instance
	 * @throws NullPointerException
	 *           if the listener is <code>null</code>
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	void unregisterEventListener(TaskEventListener listener) throws RemoteException;

	/**
	 * Returns list of registered Host Runtimes.
	 * 
	 * @return list of registered Host Runtimes
	 * @throws RemoteException
	 *           if something failed during the execution of the remote method
	 *           call.
	 */
	String[] getRegisteredHostRuntimes() throws RemoteException;
}
