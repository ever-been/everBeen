/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
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
package cz.cuni.mff.been.benchmarkmanagerng;

import java.rmi.RemoteException;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * Service wrapper for BenchmarkManagerImplementation
 *
 *  @author Jiri Tauber
 */
public class BenchmarkManagerService extends Service {

	/** Name of the service. */
	public static final String SERVICE_NAME = "benchmarkmanagerng";

	/** Name of the BenchmarkManagerCallbackInterface provided by this service */
	public static final String CALLBACK_INTERFACE = "callback";

	/** Human-readable name of the service (displayed in the web interface). */
	public static final String SERVICE_HUMAN_NAME = "Benchmark Manager NG";

	/** Derby pluggable module reference */
	private PluggableModuleManager manager;

	/** Benchmark manager itself */
	private BenchmarkManagerImplementation implementation;

	
	/**
	 * @throws TaskInitializationException
	 */
	public BenchmarkManagerService() throws TaskInitializationException {
		super();
		try {
			manager = Task.getTaskHandle().getPluggableModuleManager();
		} catch (TaskException e) {
			throw new TaskInitializationException(e);
		}

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
			implementation = new BenchmarkManagerImplementation(manager);

			/* Register implemented interface. */
			addRemoteInterface(RMI_MAIN_IFACE, implementation);
			addRemoteInterface(CALLBACK_INTERFACE, implementation);

		} catch (ComponentInitializationException e){
			Task.getTaskHandle().logError("Couldn't initialize BM because error occured: "+e.getMessage());
			throw new TaskException("Error starting Benchmark Manager", e);
		} catch (RemoteException e) {
			Task.getTaskHandle().logError("Couldn't initialize BM because error occured: "+e.getMessage());
			throw new TaskException("Error starting Benchmark Manager", e);
		} 
	}

	/**
	 * @see cz.cuni.mff.been.services.Service#stop()
	 */
	@Override
	protected void stop() throws TaskException {
		implementation.destroy();
		removeRemoteInterface(RMI_MAIN_IFACE);
		removeRemoteInterface(CALLBACK_INTERFACE);
	}

}
