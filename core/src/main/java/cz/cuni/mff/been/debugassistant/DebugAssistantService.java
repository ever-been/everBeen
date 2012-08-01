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

import java.rmi.RemoteException;

import cz.cuni.mff.been.debugassistant.implementation.DebugAssistantImplementation;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * Debug assistant service which helps to debug tasks with ease.
 *
 * @author Jan Tattermusch
 */
public class DebugAssistantService extends Service {

	private DebugAssistantImplementation implementation;
	/**
	 * Name of this service
	 */
	public static final String SERVICE_NAME = "debugassistant";
	
	/**
	 * Human readable name of this service
	 */
	public static final String SERVICE_HUMAN_NAME = "Debug Assistant";

	/**
	 * @throws TaskInitializationException
	 */
	public DebugAssistantService() throws TaskInitializationException {
		super();
		
	}

	/**
	 * @see cz.cuni.mff.been.services.Service#getName()
	 */
	@Override
	public String getName() {
		return SERVICE_NAME;
	}

	/**
	 * @see cz.cuni.mff.been.services.Service#start()
	 */
	@Override
	protected void start() throws TaskException {
		
		try {
			implementation = new DebugAssistantImplementation();
		} catch (RemoteException e) {
			throw new TaskException("Error constructing Debug assistant instance.", e);
		}
		/* Register implemented interface. */
		addRemoteInterface(RMI_MAIN_IFACE, implementation);
 	}

	/**
	 * @see cz.cuni.mff.been.services.Service#stop()
	 */
	@Override
	protected void stop() throws TaskException {
		removeRemoteInterface(RMI_MAIN_IFACE);
		implementation = null;
	}

}
