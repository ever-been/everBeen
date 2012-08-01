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
package cz.cuni.mff.been.resultsrepositoryng;

import java.io.File;
import java.rmi.RemoteException;

import cz.cuni.mff.been.common.Debug;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModule;
import cz.cuni.mff.been.pluggablemodule.hibernate.HibernatePluggableModule;
import cz.cuni.mff.been.resultsrepositoryng.implementation.ResultsRepositoryImplementation;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * Service wrapper for Results Repository Implementation
 *
 * @author Jan Tattermusch
 */
public class ResultsRepositoryService extends Service {

	/**
	 * Name of this service
	 */
	public static final String SERVICE_NAME = "resultsrepositoryng";
	
	/**
	 * Human readable name of this service
	 */
	public static final String SERVICE_HUMAN_NAME = "Results Repository NG";
	
	/**
	 * Name of database that will be used for storing results repository data
	 */
	public final static String RR_DATABASE_NAME = "ResultsRepositoryDB";
	
	/**
	 * Derby pluggable module reference used by RR.
	 */
	private DerbyPluggableModule derbyPluggableModule;
	
	/**
	 * Hibernate pluggable module reference used by RR.
	 */
	private HibernatePluggableModule hibernatePluggableModule;
	
	
	/**
	 * Results repository implementation itself.
	 */
	private ResultsRepositoryImplementation implementation;

	
	/**
	 * Network port on which will derby be accessible when in debug mode 
	 */
	private final int RR_DERBY_DEBUG_NETWORK_PORT = 1532;

	/**
	 * @throws TaskInitializationException
	 */
	public ResultsRepositoryService() throws TaskInitializationException {
		super();
		try {		
			derbyPluggableModule = (DerbyPluggableModule) Task.getTaskHandle().getPluggableModuleManager().getModule(new PluggableModuleDescriptor("derby", "1.0")); 		
			hibernatePluggableModule = (HibernatePluggableModule) Task.getTaskHandle().getPluggableModuleManager().getModule(new PluggableModuleDescriptor("hibernate", "1.0"));			
		} catch (PluggableModuleException e) {
			logError("Error during Results Repository startup: " + e.getMessage());
			throw new TaskInitializationException(e);
		} catch (TaskException e) {
			logError("Error during Results Repository startup: " + e.getMessage());
			throw new TaskInitializationException(e);
		} catch (Exception e) {
			e.printStackTrace();
			logError("Error during Results Repository startup: " + e.getMessage());
			throw new TaskInitializationException(e);
		}
		
		/* Register implemented interface. */
		//addRemoteInterface(RMI_MAIN_IFACE, implementation);
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
			if (Debug.isDebugModeOn()) {
				derbyPluggableModule.startEngine(getDerbyHomeDir(), true, RR_DERBY_DEBUG_NETWORK_PORT);
			} else {
				derbyPluggableModule.startEngine(getDerbyHomeDir(), false);
			}
			implementation = new ResultsRepositoryImplementation(
					hibernatePluggableModule, 
					RR_DATABASE_NAME,
					Task.getTaskHandle().getWorkingDirectory() + File.separator + "filestore");
			
			/* Register implemented interface. */
			addRemoteInterface(RMI_MAIN_IFACE, implementation);
			
		} catch (PluggableModuleException e) {
			Task.getTaskHandle().logError("Couldn't initialize RR because error occured: "+e.getMessage());
			throw new TaskException("Error starting Results Repository.", e);
		} catch (ResultsRepositoryException e) {
			Task.getTaskHandle().logError("Couldn't initialize RR because error occured: "+e.getMessage());
			throw new TaskException("Error starting Results Repository.", e);
		} catch (RemoteException e) {
			Task.getTaskHandle().logError("Couldn't initialize RR because error occured: "+e.getMessage());
			throw new TaskException("Error starting Results Repository.", e);
		} 
 	}

	/**
	 * @see cz.cuni.mff.been.services.Service#stop()
	 */
	@Override
	protected void stop() throws TaskException {
		implementation.destroy();
		removeRemoteInterface(RMI_MAIN_IFACE);
		
		try {
			derbyPluggableModule.stopEngine();
		} catch (PluggableModuleException e) {
			Task.getTaskHandle().logError("Couldn't stop RR because error occured: "+e.getMessage());
			throw new TaskException("Error stopping Results Repository.", e);
		}
		
		
	}

	/**
	 * Returns derby home directory to use
	 * @return derby home directory
	 */
	private String getDerbyHomeDir() {
		return Task.getTaskHandle().getWorkingDirectory();
	}

}
