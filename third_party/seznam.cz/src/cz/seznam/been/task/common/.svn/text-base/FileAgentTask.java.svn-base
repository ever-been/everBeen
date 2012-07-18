/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2010 Distributed Systems Research Group,
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
package cz.seznam.been.task.common;

import java.rmi.RemoteException;

import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgent;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgentPluggableModule;
import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * Base class for jobs that need to upload files to the ResultsRepository.
 * This class loads the TileAgent pluggable module at startup.
 * 
 * @author Jiri Tauber
 */
public abstract class FileAgentTask extends Job {

	private FileAgentPluggableModule fileAgentPluggableModule;

	//-----------------------------------------------------------------------//
	public FileAgentTask() throws TaskInitializationException {
		super();
		loadFileAgent();
	}

	public FileAgentPluggableModule getFileAgent() {
		return fileAgentPluggableModule;
	}

	//-----------------------------------------------------------------------//

	/**
	 * Simple helper methid for getting the Results Repository inteface.
	 * @return The RR interface refference
	 * @throws RemoteException 
	 */
	protected RRDataInterface getRRInterface() throws RemoteException {
		RRDataInterface rr;
		rr = (RRDataInterface)Task.getTaskHandle().getTasksPort().
				serviceFind(ResultsRepositoryService.SERVICE_NAME,
							ResultsRepositoryService.RMI_MAIN_IFACE);
		return rr;
	}

	//-----------------------------------------------------------------------//

	/**
	 * Loads the file agent pluggable module
	 * 
	 * @throws TaskInitializationException
	 */
	private void loadFileAgent() throws TaskInitializationException {
		String moduleName = "fileagent";
		String moduleVersion = "1.0";
		PluggableModuleDescriptor descriptor = new PluggableModuleDescriptor(moduleName, moduleVersion);
		PluggableModule module;
		try {
			module = getTaskHandle().getPluggableModuleManager().loadModule(descriptor);
		} catch (TaskException e) {
			throw new TaskInitializationException(e);
		} catch (PluggableModuleException e) {
			throw new TaskInitializationException(e);
		}

		if (!(module instanceof FileAgent)) {
			String err = "Pluggable module "+moduleName+"-"+moduleVersion
					+" is not a File Agent module!";
			logError(err);
			throw new TaskInitializationException(err);
		}
		fileAgentPluggableModule = (FileAgentPluggableModule) module;
	}

}
