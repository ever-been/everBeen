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

import cz.cuni.mff.been.task.TaskException;

/**
 * Interface for representation of Task used by the Host Runtime and the Task
 * Manager.
 * 
 * @author Antonin Tomecek
 * @author David Majda
 */
public interface TaskInterface extends Remote {
	/**
	 * Returns the task identifier of the task.
	 * 
	 * @return task identifier of the task
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	String getTaskID() throws RemoteException;

	/**
	 * Returns the context identifier of the task.
	 * 
	 * @return context identifier of the task
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	String getContextID() throws RemoteException;
	
	/**
	 * Determines if this task has a detailed load flag specified.
	 * 
	 * @return <code>true</code> if this task has a detailed load flag specified;
	 *          <code>false</code> otherwise.
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	boolean isDetailedLoad() throws RemoteException;

	/**
	 * Returns the task directory of the task.
	 * 
	 * @return task directory of the task.
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	String getTaskDirectory() throws RemoteException;
	
	/**
	 * Returns the working directory of the task.
	 * 
	 * @return working directory of the task.
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	String getWorkingDirectory() throws RemoteException;
	
	/**
	 * Returns the temporary directory of the task.
	 * 
	 * @return temporary directory of the task.
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	String getTemporaryDirectory() throws RemoteException;
	
	/**
	 * Destroys all data associated with this task. Can be called only on
	 * non-running tasks.
	 * 
	 * @throws IllegalStateException if the task is running
	 * @throws TaskException if destroying of the task fails
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	void destroy() throws TaskException, RemoteException;
	
	/**
	 * Kills the task (destroys its process). It is not an error to kill already
	 * stopped task, as it is not possible to get the task state and kill it
	 * atomically. 
	 * 
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	void kill() throws RemoteException;

	/**
	 * Tests if this task is running.
	 * 
	 * @return <code>true</code> if this task is running;
	 *          <code>false</code> otherwise.
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	boolean isRunning() throws RemoteException;
	
	/**
	 * Waits until this task has terminated. Returns immediately if the task is
	 * already not running.
	 * 
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	void waitFor() throws RemoteException;
	
	/**
	 * Returns the exit value of this task. Can be called only on non-running
	 * tasks.
	 * 
	 * @return exit value of this task
	 * @throws IllegalStateException if the task is running
	 * @throws RemoteException if something failed during the execution of
	 * 	        the remote method call
	 */
	int getExitValue() throws RemoteException;
}
