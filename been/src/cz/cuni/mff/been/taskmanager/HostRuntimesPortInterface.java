/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager;

import java.io.Serializable;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import cz.cuni.mff.been.logging.LogLevel;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.taskmanager.data.TaskState;

/**
 * Interface of Task Manager's port provided for Host Runtimes.
 * 
 * @author Antonin Tomecek
 */
public interface HostRuntimesPortInterface extends Remote {

	/**
	 * Used by Host Runtime for forwarding of check points reached by
	 * tasks.
	 * 
	 * @param taskId TID of task.
	 * @param type Type of reached state.
	 * @param value Value with which this state was reached.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 * 
	 * _@deprecated Use checkPointReached(CheckPoint checkPoint) method
	 * 	instead.
	 */
	void checkPointReached(String taskId, String contextId,
			String type, String value, String hostName,
			Serializable magicObject)
	throws RemoteException;
	
	/**
	 * Used by Host Runtime for forwarding of checkpoint reached by task.
	 * 
	 * @param checkPoint CheckPoint representation of reached checkpoint.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	void checkPointReached(CheckPoint checkPoint) throws RemoteException;
	
	/**
	 * Used by Host Runtime for forwarding of lookup request from task.
	 * Returns array containing all reached checkpoints matching specified
	 * checkpointTemplate. All filled in values must match. Values set to
	 * null are arbitrary.
	 * Calling of this method is blocking.
	 * 
	 * @param checkpointTemplate Prepared template for checkpoint match.
	 * @param timeout Maximum time to wait in milliseconds.
	 * @return Array containing all checkpoint matching specified
	 * 	checkpointTemplate.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	CheckPoint[] checkPointLook(CheckPoint checkpointTemplate, long timeout)
	throws TaskManagerException, RemoteException;
	
	/**
	 * Used by Host Runtime for forwarding of log messages from tasks.
	 * 
	 * @param contextId Id of the task's context.
	 * @param taskId TID of task.
 	 * @param level log level of this log message 
	 * @param timestamp timestamp of this log message
	 * @param message Message to log.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	void log(String contextId, String taskId, LogLevel level, Date timestamp,
			String message)
	throws RemoteException;
	
	/**
	 * Used by Host Runtime for forwarding of task's request for new
	 * registration of service.
	 * 
	 * @param service Object describing service to register.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	void serviceRegister(ServiceEntry service)
	throws RemoteException;
	
	/**
	 * Used by Host Runtime for forwarding of task's request for
	 * deregistration of services. All entries matching specified template
	 * are removed from the registry.
	 * 
	 * @param serviceTemplate Object describing services to remove (using
	 * 	regular expressions).
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	void serviceUnregister(ServiceEntry serviceTemplate)
	throws RemoteException;
	
	/**
	 * Used by Host Runtime for forwarding of task's request for looking in
	 * service entries. Returns array containing all service entries
	 * matching specified template (using regular expressions).
	 * 
	 * @param serviceTemplate Object looked describing services (using
	 * 	regular expressions).
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	ServiceEntry[] serviceLook(ServiceEntry serviceTemplate)
	throws RemoteException;
	
	/**
	 * Used by tasks for finding some registered remote interface. Given
	 * names are compared for exact match (doesn't use regular
	 * expressions).
	 * 
	 * @param serviceName Name of service.
	 * @param interfaceName Name of service's interface.
	 * @return Remote representation of one from all matching interfaces
	 * 	or null if none.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	Remote serviceFind(String serviceName, String interfaceName)
	throws RemoteException;
	
	/**
	 * Used by tasks for finding some registered remote interface. Given
	 * names are compared for exact match (doesn't use regular
	 * expressions).
	 * 
	 * @param serviceName Name of service.
	 * @param interfaceName Name of service's interface.
	 * @return URI representation of one from all matching interfaces or
	 * 	null if none.
	 * @throws RemoteException If something failed during the execuiton of
	 * 	the remote method call.
	 */
	URI serviceFindURI(String serviceName, String interfaceName)
	throws RemoteException;

	/**
	 * Notification from Host Runtime, that specified task was restarted.
	 * 
	 * @param taskId ID of task.
	 * @param contextId ID of context.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	void taskRestarted(String taskId, String contextId)
	throws RemoteException;

	/**
	 * Used by Host Runtime for notify of task's reached state.
	 * 
	 * @param taskId TID of task.
	 * @param state state of task.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	void taskReachedEnd(String taskId, String contextId, TaskState state)
	throws RemoteException;
	
	/**
	 * Stores additional standard output of the task in the log storage. This 
	 * method can be called many times to store the output of the task in smaller
	 * batches.
	 * 
	 * @param context task's context; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @param output part of the task's standard output; cannot be null.
	 * @throws RemoteException If something failed during the execution of
	 * the remote method call.
	 * @throws LogStorageException If an error occured while storing the output.
	 * @throws IllegalArgumentException If the the context name or task's ID are empty 
	 * strings or if the task or it's context wasn't found.
	 * @throws NullPointerException If any argument is <tt>null</tt>.
	 */
	void addStandardOutput(String context, String taskID, String output)
		throws RemoteException, LogStorageException,
		IllegalArgumentException, NullPointerException;
	
	/**
	 * Stores additional error output of the task in the log storage. This 
	 * method can be called many times to store the output of the task in smaller
	 * batches.
	 * 
	 * @param context task's context; cannot be null or an empty string.
	 * @param taskID task's ID; cannot be null or an empty string.
	 * @param output part of the task's error output; cannot be null.
	 * @throws RemoteException If something failed during the execution of
	 * the remote method call.
	 * @throws LogStorageException If an error occured while storing the output.
	 * @throws IllegalArgumentException If the the context name or task's ID are empty 
	 * strings or if the task or it's context wasn't found.
	 * @throws NullPointerException If any argument is <tt>null</tt>.
	 */
	void addErrorOutput(String context, String taskID, String output)
		throws RemoteException, LogStorageException,
		IllegalArgumentException, NullPointerException;
	
	/**
	 * Return RMI reference to the Task Manager. 
	 * 
	 * @return RMI reference to the Task Manager
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call.
	 */
	TaskManagerInterface getTaskManager() throws RemoteException;
}
