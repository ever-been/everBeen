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

import java.rmi.Remote;
import java.rmi.RemoteException;

import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.taskmanager.HostRuntimesPortInterface;

/**
 * RMI interface to the Host Runtime. Used by the Task Manager.
 * 
 * @author Antonin Tomecek
 * @author David Majda 
 */
public interface HostRuntimeInterface extends Remote {
	/* RMI URL to the Host Runtime interface. */
	String URL = "/been/hostruntime";
	
	/**
	 * Initializes interfaces to the Task Manager and set the option. Called by
	 * the Task manager every time the task is started.
	 * 
	 * @param hostRuntimesPort RMI interface to the Task Manager's port to the
	 *         Host Runtime
	 * @param maxPackageCacheSize the size limit of the Host Runtime's package
	 *         cache
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call
	 */
	void initialize(
		HostRuntimesPortInterface hostRuntimesPort,
		long maxPackageCacheSize)
		throws RemoteException;
	
	/**
	 * Ends the Host Huntime's suffering and terminates its existence, along with
	 * all tasks brave enough to be alive at this time. As its last act, the
	 * Host Runtime will also unregister itself from the Task Manager. 
	 * 
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call
	 */
	void terminate() throws RemoteException;

	/**
	 * Creates new task according to the specified task descriptor.
	 * 
	 * @param taskDescriptor task descriptor for new task
	 * @return RMI interface to the created task
	 * @throws HostRuntimeException if creating of the internal task object fails 
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call
	 * @throws IllegalStateException if called before calling
	 *          <code>initialize</code> 
	 */
	TaskInterface createTask(TaskDescriptor taskDescriptor) throws
		HostRuntimeException, RemoteException;
	
	
	/**
	 * Deletes a context.
	 * 
	 * @param contextID identifier of the context to close
	 * @throws IllegalArgumentException if no task from given context was running
	 *          on this Host Runtime or if any of the tasks in given context is
	 *          currently running
	 * @throws HostRuntimeException if deleting of the context data fails
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call
	 */
	void deleteContext(String contextID) throws HostRuntimeException,
		RemoteException;
	
	/**
	 * Sets the size limit of the Host Runtime's package cache. The setting will
	 * be applied after downloading next package to the cache.
	 * 
	 * @param maxPackageCacheSize the size limit of the Host Runtime's package
	 *         cache
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call
	 */
	void setMaxPackageCacheSize(long maxPackageCacheSize)
		throws RemoteException;
}
