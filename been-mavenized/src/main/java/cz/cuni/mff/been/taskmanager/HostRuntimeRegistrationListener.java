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
package cz.cuni.mff.been.taskmanager;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Callback interface used when listening for events in the Task Manager
 * (currently only registering/unregistering of the Host Runtime). Those
 * who are interested in the events the must register using
 * <code>TaskManagerInterface.registerEventListener</code> method and later
 * unregister using <code>TaskManagerInterface.unregisterEventListener</code>. 
 * 
 * @author David Majda
 */
public interface HostRuntimeRegistrationListener extends Remote {
	/**
	 * Called when new Host Runtime is registered at the Task Manager.
	 * 
	 * @param hostname host where the new Host Runtime is running 
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call.
	 */
	void hostRuntimeRegistered(String hostname) throws RemoteException;

	/**
	 * Called when existing Host Runtime is unregistered at the Task Manager.
	 * 
	 * @param hostname host where the Host Runtime is running 
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call.
	 */
	void hostRuntimeUnregistered(String hostname) throws RemoteException;
}
