/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.clinterface;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is a RMI interface provided by the command line service.
 *
 * @author Andrej Podzimek
 */
public interface CommandLineInterface extends Remote {
	
	/**
	 * Connection counter getter.
	 * 
	 * @return The number of connections accepted since {@link CommandLineThreads} was started.
	 * @throws RemoteException When something really bad happens.
	 */
	public Long getConnectionCounter() throws RemoteException;
	
	/**
	 * Flushes the pool of module instances for the given module name, so that they can be
	 * garbage-collected. This method may never be needed. ;-)
	 * 
	 * @param name Name of the module for which all existing instances should be discarded.
	 * @throws RemoteException When something really bad happens.
	 */
	public void flushPool( String name ) throws RemoteException;
	
	/**
	 * Flushes all module instance pools. This method may never be needed. ;-) Could be used
	 * for cleanup after a long uptime and extremely heavy loads.
	 * 
	 * @throws RemoteException When something really bad happens.
	 */
	public void flushAllPools() throws RemoteException;
	
	/**
	 * Switches CLI's output (to bcmd) to the default mode. Only exception messages are sent back.
	 * 
	 * @throws RemoteException When something really bad happens.
	 */
	public void switchToNormalOutput() throws RemoteException;
	
	/**
	 * Switches CLI's output (to bcmd) to the extended mode. Both exception messages and full
	 * exception stack traces are sent back.
	 * 
	 * @throws RemoteException When something really bad happens.
	 */
	public void switchToDebugOutput() throws RemoteException;
	
	/**
	 * This causes the service to stop listening on the well-known port. It may or may not
	 * affect currently running connections. This method should only be called when the command
	 * line service is stopped. It is the user's responsibility to make sure that no command
	 * line connections are running.
	 * 
	 * @throws IOException When the socket cannot be closed. This is usually a fatal error...
	 * @throws RemoteException When something really bad happens.
	 */
	public void shutdown() throws IOException, RemoteException;
}
