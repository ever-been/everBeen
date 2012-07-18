/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */
package cz.cuni.mff.been.hostruntime;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogLevel;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.logging.OutputHandle;
import cz.cuni.mff.been.softwarerepository.PackageType;
import cz.cuni.mff.been.taskmanager.HostRuntimeRegistrationListener;
import cz.cuni.mff.been.taskmanager.ServiceEntry;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;

/**
 * Interface of the Host Runtime's port provided for the tasks.
 * 
 * @author Antonin Tomecek
 * @author David Majda
 */
public interface TasksPortInterface extends Remote {
	/**
	 * Returns the task directory, which would specified task have, if it was run
	 * on this Host Runtime.
	 * 
	 * @param contextID context ID
	 * @param taskID task ID
	 * @return task directory of specified task
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call
	 */
	String getTaskDirectory(String contextID, String taskID)
		throws RemoteException;
	
	/**
	 * Returns the working directory, which would specified task have, if it was
	 * run on this Host Runtime.
	 * 
	 * @param contextID context ID
	 * @param taskID task ID
	 * @return working directory of specified task
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call
	 */
	String getWorkingDirectory(String contextID, String taskID)
		throws RemoteException;
	
	/**
	 * Returns the temporary directory, which would specified task have, if it was
	 * run on this Host Runtime.
	 * 
	 * @param contextID context ID
	 * @param taskID task ID
	 * @return temporary directory of specified task
	 * @throws RemoteException if something failes during the execution of
	 *          the remote method call
	 */
	String getTemporaryDirectory(String contextID, String taskID)
		throws RemoteException;

	/**
	 * Retrieves the task properties.
	 * 
	 * @return task properties
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call
	 */
	Properties getTaskProperties() throws RemoteException;

	/**
	 * Retrieves the task properties with objects (implementing
	 * <code>Serializable</code>) as their values.
	 * 
	 * @return task properties with objects as their values
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call
	 */
	Map<String, Serializable> getTaskPropertyObjects() throws RemoteException;
	
	/**
	 * Returns the Task Manager RMI reference
	 *  
	 * @return The task manager interface
	 */
	TaskManagerInterface getTaskManager() throws RemoteException;
	
	/**
	 * Run new task specified by its task descriptor.
	 * 
	 * @param taskDescriptor task descriptor of the new task
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	void runTask(TaskDescriptor taskDescriptor) throws RemoteException;
	
	/**
	 * Run more new tasks specified by their task descriptors.
	 * 
	 * @param taskDescriptors task descriptors of the new tasks
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	void runTasks(TaskDescriptor[] taskDescriptors) throws RemoteException;
	
	/**
	 * Creates new context.
	 * 
	 * @param id context ID.
	 * @param name context name
	 * @param description contaxt description
	 * @param data additional context data
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	void newContext(String id, String name, String description,
		Serializable data) throws RemoteException;
	
	/**
	 * Ends active context.
	 * 
	 * @param id context ID
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	void endContext(String id) throws RemoteException;

	/**
	 * Registers new listener of the Task Manager events.
	 * 
	 * @param listener RMI reference to the listener
	 * @throws IllegalArgumentException if the listener was already registered
	 * @throws NullPointerException if the listener is <code>null</code>
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call.
	 */
	void registerEventListener(HostRuntimeRegistrationListener listener) throws RemoteException;

	/**
	 * Unregisters existing listener of the Task Manager events.
	 * 
	 * @param listener RMI reference to the listener
	 * @throws IllegalArgumentException if the listener was not registered
	 * @throws NullPointerException if the listener is <code>null</code>
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call.
	 */
	void unregisterEventListener(HostRuntimeRegistrationListener listener) throws RemoteException;
	
	/**
	 * Returns list of Host Runtimes registered at the Task Manager.
	 * 
	 * @return list of registered Host Runtimes
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call.
	 */
	String[] getRegisteredHostRuntimes() throws RemoteException;

	/**
	 * Sends a log message to the log storage.
	 * 
	 * @param level log level of the message
	 * @param timestamp timestamp of the log event
	 * @param message the log message; can be multiline; cannot be null or contain
	 *         "\t\t\t".
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	void log(LogLevel level, Date timestamp, String message)
		throws RemoteException;

	/**
	 * Gets all log messages produced by a task.
	 * 
	 * @param context task's context; cannot be <code>null</code> or an empty
	 *         string
	 * @param taskID task's ID; cannot be <code>null</code> or an empty string
	 * @return logs of the task
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call
	 * @throws LogStorageException if an error occured while retrieving the logs
	 * @throws IllegalArgumentException if the the context name or task's ID are
	 *          empty  strings or if the task wasn't run
	 * @throws NullPointerException if any argument is <tt>null</tt>
	 */
	LogRecord[] getLogsForTask(String context, String taskID)
		throws RemoteException, LogStorageException, IllegalArgumentException,
		NullPointerException;

	/**
	 * Creates a handle for receiving the standard output of a task. 
	 * 
	 * @param context task's context; cannot be <code>null</code> or an empty
	 *         string
	 * @param taskID task's ID; cannot be <code>null</code> or an empty string
	 * @return handle for retrievind the standard output
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call
	 * @throws LogStorageException if an error occured while creating the handle
	 * @throws IllegalArgumentException if the the context name or task's ID are
	 *          empty or if the task or it's context wasn't found
	 * @throws NullPointerException if any argument is <tt>null</tt>
	 */
	OutputHandle getStandardOutput(String context, String taskID)
		throws RemoteException, LogStorageException, IllegalArgumentException,
		NullPointerException;

	/**
	 * Creates a handle for receiving the error output of a task. 
	 * 
	 * @param context task's context; cannot be <code>null</code> or an empty string
	 * @param taskID task's ID; cannot be <code>null</code> or an empty string
	 * @return handle for retrievind the error output
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call.
	 * @throws LogStorageException if an error occured while creating the handle
	 * @throws IllegalArgumentException if the the context name or task's ID are
	 *          empty or if the task or it's context wasn't found
	 * @throws NullPointerException if any argument is <tt>null</tt>
	 */
	OutputHandle getErrorOutput(String context, String taskID)
		throws RemoteException, LogStorageException, IllegalArgumentException,
		NullPointerException;

	/**
	 * Signal that a task reached a checkpoint.
	 *  
	 * @param name checkpoint name
	 * @param value checkpoint value
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	void checkPointReached(String name, Serializable value) throws RemoteException;
	
	/**
	 * Waits until a task reaches a checkpoint of specified type and returns its
	 * value.
	 * 
	 * @param contextID context ID; if <code>null</code> then the context of the
	 *         calling task will be used.
	 * @param taskID task ID
	 * @param name checkpoint name
	 * @param timeout number of miliseconds to wait for a checkpoint;
	 *         <code>0</code> means that the call will not block and returns
	 *         immediately
	 *         
	 * @return Value of the checkpoint or <code>null</code> if there are any
	 *          problems obtaining the value.
	 *          
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	Serializable checkPointWait(String contextID, String taskID, String name, long timeout)
		throws RemoteException;
						
	/**
	 * Register a service at the Task Manager.
	 * 
	 * @param service service descriptor
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	void serviceRegister(ServiceEntry service) throws RemoteException;
	
	/**
	 * Unregisters all services matching the service template form the Task Manager.
	 * 
	 * @param serviceTemplate template of services to remove
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	void serviceUnregister(ServiceEntry serviceTemplate) throws RemoteException;
		
	/**
	 * Returns RMI reference of a service with specified service name and
	 * interface name.
	 * 
	 * @param serviceName service name
	 * @param interfaceName service interface name
	 * @return RMI reference of a service with specified service name and
	 *          interface name
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	Remote serviceFind(String serviceName, String interfaceName)
		throws RemoteException;
	
	/**
	 * Returns descriptors of all services registered at the Task Manager.
	 * 
	 * @return descriptors of all services registered at the Task Manage
	 * @throws RemoteException if something fails during the execution of
	 *          the remote method call.
	 */
	ServiceEntry[] serviceFindAll() throws RemoteException;
	
	/**
	 * Returns the task descriptor.
	 * 
	 * @return task descriptor.
	 * 
	 * @throws RemoteException If RMI error occured.
	 */
	TaskDescriptor getTaskDescriptor() throws RemoteException;

	/**
	 *  Queries task manager for status of a task
	 * @param taskId		task identifier
	 * @param contextId		context identifier
	 * @return	status of given task
	 * @throws RemoteException	if something fails during the execution of
	 *          the remote method call.
	 * @throws IllegalArgumentException	when task does not exist in task manager
	 */
	TaskEntry getTaskById(String taskId, String contextId) throws RemoteException, IllegalArgumentException;
        
        
	/** 
	 * Tells to hostruntime's package cache manager to extract package of given type 
	 * with given name and version to a specified path
	 * @param packageName package name
	 * @param packageVersion package version
	 * @param path path to extract to
	 * @param packageType type of requested package
	 */
	public void extractPackage(String packageName, String packageVersion, String path, PackageType packageType) throws RemoteException;

}

