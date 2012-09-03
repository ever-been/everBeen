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
package cz.cuni.mff.been.softwarerepository;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * The Software Repository service class.
 * 
 * @author David Majda
 */
public class SoftwareRepositoryService extends Service {
	private static final String TEMP_DIR_PROPERTY = "temp.dir";
	private static final String DATA_DIR_PROPERTY = "data.dir";
	private static final String BEEN_HOME = "BEEN_HOME";
	
	/** Name of the service. */
	public static final String SERVICE_NAME = "softwarerepository";
	/** Human-readable name of the service (displayed in the web interface). */
	public static final String SERVICE_HUMAN_NAME = "Software Repository";

	private String dataDirectory;
	private String tempDirectory;
	private SoftwareRepositoryImplementation implementation; 

	/**
	 * Allocates a new <code>SoftwareRepositoryService</code> object.
	 * 
	 * @throws TaskInitializationException if something fails during the service
	 *          creation
	 */
	public SoftwareRepositoryService() throws TaskInitializationException {
		/* Determine and check data and temp directories. */
		getAndCheckDirectories();
		
		/* Create the implementation. */
		try {
			implementation = SoftwareRepositoryImplementation.getInstance();
		} catch (RemoteException e) {
			throw new TaskInitializationException(e);
		}
		
		/* Register implemented interface. */
		addRemoteInterface(RMI_MAIN_IFACE, implementation);
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
			tryCopyExampleData();
		} catch (IOException e) {
			throw new TaskException(e);
		}
		
		try {
			implementation.initialize(dataDirectory, tempDirectory);
		} catch (IOException e) {
			throw new TaskException(e);
		} catch (ClassNotFoundException e) {
			throw new TaskException(e);
		}
	}

	/**
	 * @see cz.cuni.mff.been.services.Service#stop()
	 */
	@Override
	protected void stop() throws TaskException {
	}

	private void checkDirectory(String directoryName) {
		File directory = new File(directoryName);
		if (!directory.exists()) {
			logFatal("Error: \"" + directory + "\" does not exist.");
			exitError();
		}
		if (!directory.isDirectory()) {
			logFatal("Error: \"" + directory + "\" is not directory.");
			exitError();
		}
		if (!directory.canWrite()) {
			logFatal("Error: \"" + directory + "\" is not writable.");
			exitError();
		}
	}
	
	private void getAndCheckDirectories() {
		if (getTaskProperty(DATA_DIR_PROPERTY) != null) {
			String beenHome = System.getenv(BEEN_HOME);
			if (beenHome != null) {
				dataDirectory = beenHome + File.separatorChar + getTaskProperty(DATA_DIR_PROPERTY);
			} else {
				logFatal("Environment variable BEEN_HOME not defined.");
				exitError();
			}
		} else {
			dataDirectory = getWorkingDirectory();
		}

		if (getTaskProperty(TEMP_DIR_PROPERTY) != null) {
			String beenHome = System.getenv(BEEN_HOME);
			if (beenHome != null) {
				tempDirectory = beenHome + File.separatorChar + getTaskProperty(TEMP_DIR_PROPERTY);
			} else {
				logFatal("Environment variable BEEN_HOME not defined.");
				exitError();
			}
		} else {
			tempDirectory = getTempDirectory(); 
		}

		checkDirectory(dataDirectory);
		checkDirectory(tempDirectory);
	}
}
