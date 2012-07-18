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
package cz.seznam.been.task.hintserver.deploy;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.UUID;


import cz.cuni.mff.been.common.scripting.ScriptEnvironment;
import cz.cuni.mff.been.common.scripting.ScriptException;
import cz.cuni.mff.been.common.scripting.ScriptLauncher;
import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * Task that loads binary from RR and runs a deploy script
 *
 * @author Jiri Tauber
 */
public class Deploy extends Job {

	/** Name of the property that gives the binary serial number */
	public static final String PROP_BINARY_ID = "binary";

	/** File where the binary package will be stored after download from the RR */
	private static final String FILE_BINARY = "binary.deb";  // TODO
	/** File with deploy and run script - stored in task package directory */
	private static final String FILE_SCRIPT = "script.sh";  // TODO

	//----------------------------------------------------------------------------------//
	/** RMI reference to results repository file store */
	private FileStoreClient fileStore;

	/**
	 * @throws TaskInitializationException
	 */
	public Deploy() throws TaskInitializationException {
		super();
		try {
			RRDataInterface resultsRepository = (RRDataInterface)getTasksPort().serviceFind(
					ResultsRepositoryService.SERVICE_NAME,
					ResultsRepositoryService.RMI_MAIN_IFACE);
			fileStore = resultsRepository.getFileStoreClient();
		} catch (RemoteException e) {
			throw new TaskInitializationException(e);
		}
	}

	//----------------------------------------------------------------------------------//
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#checkRequiredProperties()
	 */
	@Override
	protected void checkRequiredProperties() throws TaskException {
		StringBuilder err = new StringBuilder();

		String propName = PROP_BINARY_ID;
		String propValue = getTaskProperty(propName); 
		if (propValue == null || propValue.isEmpty()) {
			err.append("Property "+propName+" is missing;");
		}

		if (err.length() > 0){
			throw new TaskException(err.toString());
		}
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	protected void run() throws TaskException {
		downloadFile();
		String scriptFile = getTaskDirectory() + File.separator + FILE_SCRIPT;
		ScriptEnvironment env = new ScriptEnvironment();
		try {
			(new ScriptLauncher()).runShellScript(new File(scriptFile), env);
		} catch (ScriptException e) {
			throw new TaskException("Error executing script - "+e.getMessage(), e);
		}
	}


	//----------------------------------------------------------------------------------//
	/**
	 * Downloads the binary file from the Results Repository
	 * @throws TaskException
	 */
	private void downloadFile() throws TaskException {
		try {
			UUID binId = UUID.fromString(getTaskProperty(PROP_BINARY_ID));
			fileStore.downloadFile(binId, new File(FILE_BINARY));
		} catch (RemoteException e) {
			throw new TaskException("Remote error when downloading the binary file", e);
		} catch (IOException e) {
			throw new TaskException("I/O error downloading the binary file", e);
		}
	}

}
