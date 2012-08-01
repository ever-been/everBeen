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
import java.rmi.server.UnicastRemoteObject;

import cz.cuni.mff.been.task.Task;

/**
 * This remote object provides access to some external features this service will expose.
 * 
 * @author Andrej Podzimek
 */
public class CommandLineImplementation extends UnicastRemoteObject implements CommandLineInterface {
	
	private static final long	serialVersionUID	= -4417933825423263099L;
	
	private final CommandLineThreads commandThread;

	/**
	 * This creates a new remote object for this service and spawns a new instance of its
	 * thread factory. Say WOW!
	 * 
	 * @throws IOException When a network error is encountered.
	 */
	public CommandLineImplementation() throws IOException {
		commandThread = new CommandLineThreads();
	}
	
	@Override
	public Long getConnectionCounter() {
		Task.getTaskHandle().logInfo( "Connection count requested." );
		return commandThread.getCounter();
	}
	
	@Override
	public void flushPool( String name ) {
		Task.getTaskHandle().logInfo( "Instance pool flush requested for: " + name );
		CommandLineModule.flushPool( name );
	}
	
	@Override
	public void flushAllPools() {
		CommandLineModule.flushAllPools();
		Task.getTaskHandle().logInfo( "All instance pools flushed." );
	}
	
	@Override
	public void shutdown() throws IOException {
		commandThread.shutdown();
	}

	@Override
	public void switchToNormalOutput() {
		commandThread.setNormalOutput();
	}

	@Override
	public void switchToDebugOutput() {
		commandThread.setDebugOutput();
	}
}
