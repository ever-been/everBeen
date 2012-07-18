/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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
package cz.cuni.mff.been.task;

import java.rmi.Remote;
import java.rmi.RemoteException;

import cz.cuni.mff.been.webinterface.services.ServiceInfo.Status;

/**
 * Interface used to remotely control a service task.
 * 
 * @author Jaroslav Urban
 */
public interface ServiceControlInterface extends Remote {

	/**
	 * Stops the service
	 * 
	 * @throws RemoteException
	 * @throws InvalidServiceStateException if the service is in such state
	 * 	that doesn't allow restarting. For example if it's just starting up. 
	 * @throws TaskException if starting of the service fails
	 */
	void stopService() throws RemoteException, InvalidServiceStateException, TaskException;
	
	/**
	 * Determines the service's status
	 * 
	 * @return service's status
	 * @throws RemoteException
	 */
	Status getStatus() throws RemoteException;
	
	/**
	 * Does nothing, but it can be used to test whether a remote reference 
	 * is valid (that is whether it still references a valid remote object).
	 * 
	 * @throws RemoteException
	 */
	void ping() throws RemoteException;
}
