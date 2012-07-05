/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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
package cz.cuni.mff.been.debugassistant;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * Remote interface of Debug Assistant Service.
 * @author Jan Tattermusch
 *
 */
public interface DebugAssistantInterface extends Remote {
	
    /**
     * Registers a new suspended task 
     * @param task task to register
     * @throws RemoteException
     */
	void registerSuspendedTask(SuspendedTask task) throws RemoteException;
	
	/**
	 * Retrieves list of suspended tasks.
	 * @return list of registered suspended tasks.
	 * @throws RemoteException
	 */
	List<SuspendedTask> getSuspendedTasks() throws RemoteException;
	
	/**
	 * Lets a suspended task run (connects to it's debugging port 
	 * and resumes it).
	 * @param id identifier of suspended task
	 * @throws RemoteException
	 * @throws DebugAssistantException
	 */
	void runSuspendedTask(UUID id) throws RemoteException, DebugAssistantException;
	
	/**
	 * Unregisters registered task
	 * @param id identifier of a task to unregister
	 * @throws RemoteException
	 * @throws DebugAssistantException
	 */
	void unregisterTask(UUID id) throws RemoteException, DebugAssistantException;

}
