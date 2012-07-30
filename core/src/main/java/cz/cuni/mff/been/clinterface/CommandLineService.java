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

import cz.cuni.mff.been.task.Service;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * This service provides a command line listener to which a fast remote client written in native
 * code can connect.
 * 
 * @author Andrej Podzimek
 */
public class CommandLineService extends Service {
	
	/** Official name of the service. */
	public static final String SERVICE_NAME = "clinterface";
	
	/** Human-readable name of the service. */
	public static final String SERVICE_HUMAN_NAME = "Command Line Interface";
	
	/** Implementation of the RMI interface for remote service management. */
	private final CommandLineImplementation implementation;

	/**
	 * Creates a new instance of the service each time BEEN starts it.
	 * 
	 * @throws TaskInitializationException when a failure unrelated to this service occurs.
	 */
	public CommandLineService() throws TaskInitializationException {
		CommandLineImplementation implementation;
		
		try {
			implementation = new CommandLineImplementation();
		} catch ( IOException exception ) {
			logFatal(
				"Cannot create command line service implementation: " + exception.getMessage()
			);
			implementation = null;
			System.exit( -1 );
		}
		
		this.implementation = implementation;
		addRemoteInterface( RMI_MAIN_IFACE, implementation );
	}

	@Override
	public String getName() {
		return SERVICE_NAME;
	}

	@Override
	protected void start() throws TaskException {
	}

	@Override
	protected void stop() throws TaskException {
		removeRemoteInterface( RMI_MAIN_IFACE );
		try {
			implementation.shutdown();																// TODO: This is a DIRTY exit.
		} catch ( IOException exception ) {
			logError( "Failed to shutdown() the network socket." );
		}
	}
}
