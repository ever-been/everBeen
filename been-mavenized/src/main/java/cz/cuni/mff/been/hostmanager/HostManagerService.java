/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import cz.cuni.mff.been.hostmanager.database.HostDatabaseEngine;

import cz.cuni.mff.been.hostmanager.load.LoadServerImplementation;
import cz.cuni.mff.been.hostmanager.util.MiscUtils;
import cz.cuni.mff.been.logging.LogLevel;

import cz.cuni.mff.been.task.Service;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * Task which loads and initialises Host Manager.
 * 
 * @author Branislav Repcek 
 */
public class HostManagerService extends Service {

	/**
	 * Service name. 
	 */
	public static final String SERVICE_NAME = "hostmanager";

	/** 
	 * Human-readable name of the service (displayed in the web interface). 
	 */
	public static final String SERVICE_HUMAN_NAME = "Host Manager";
	
	/**
	 * Name of main remote interface for the Host Manager.
	 */
	public static final String REMOTE_INTERFACE_MAIN = "main";
	
	/**
	 * Name of the database interface.
	 */
	public static final String REMOTE_INTERFACE_DATABASE = "database";
	
	/**
	 * Name of the load server's interface.
	 */
	public static final String REMOTE_INTERFACE_LOAD_SERVER = "load";

	/**
	 * Application data.
	 */
	private HostManagerApplicationData appData;
	
	/**
	 * Instance of the Host Manager implementation. 
	 */
	private HostManagerImplementation hostManager;
	
	/**
	 * Instance of Load Server.
	 */
	private LoadServerImplementation loadServer;
	
	/**
	 * Create instance of HostManagerService.
	 * 
	 * @throws TaskInitializationException if error occurred when initialising task.
	 */
	public HostManagerService() throws TaskInitializationException {
		
		super();
	}
	
	/**
	 * @see cz.cuni.mff.been.task.Service#getName()
	 */
	@Override
	public String getName() {
		
		return SERVICE_NAME;
	}

	/**
	 * @see cz.cuni.mff.been.task.Service#start()
	 */
	@Override
	protected void start() throws TaskException {

		String taskDirectory = getTaskDirectory();
		String workingDirectory = getWorkingDirectory();
		String databaseDirectory = testAndCreateDirectory(workingDirectory, "hosts");

		testAndCopyFile(workingDirectory, taskDirectory, HostManagerOptions.DEFAULT_FILE_NAME);
		testAndCopyFile(workingDirectory, taskDirectory, 
				HostManagerImplementation.PROPERTY_DESCRIPTION_FILE_NAME);
		testAndCopyFile(databaseDirectory, taskDirectory, HostDatabaseEngine.ALIAS_DEFINITION_FILE);

		/*
		 * See if there's environmental variable HOSTMANAGER_DEBUG_LEVEL. If it is, we use its value
		 * to set separate log level for the logger used through the Host Manager and Load Server.
		 * If such variable does not exist we will use default log level as specified in Task class.
		 */
		String hmLogLevel = System.getenv("HOSTMANAGER_DEBUG_LEVEL");
		
		if (hmLogLevel != null) {
			// Now set log level
			try {
				this.setLogLevel(LogLevel.valueOf(hmLogLevel.toUpperCase()));
			} catch (IllegalArgumentException e) {
				// oops, use default instead
				logWarning("Invalid log level specified for Host Manager: " + hmLogLevel);
				setLogLevel(DEFAULT_LOG_LEVEL);
			}
			logInfo("Setting debug level for Host Manager to: " + getLogLevel().toString());
		}
		
		// Create logger.
		HostManagerLogger logger = new HostManagerLogger(this);
		
		try {
			tryCopyExampleData("hosts");
		} catch (IOException e) {
			logger.logFatal("Unable to copy example data.", e);
			throw new TaskException("Unable to copy example data.", e);
		}
		
		// Load configuration.
		HostManagerOptionsInterface options = null;
		
		try {
			options = new HostManagerOptions(MiscUtils.concatenatePath(workingDirectory, 
					HostManagerOptions.DEFAULT_FILE_NAME));
		} catch (InputParseException e) {
			e.printStackTrace();
			throw new TaskException("Error parsing configuration file.", e);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new TaskException("Error creating configuration manager.", e);
		}

		try {
			logger.logDebug("[Options begin]");
			logger.logDebug(HostManagerOptionsInterface.Option.ACTIVITY_MONITOR_INTERVAL.toString() + "="
					+ options.getActivityMonitorInterval());
			logger.logDebug(HostManagerOptionsInterface.Option.BRIEF_MODE_INTERVAL.toString() + "="
					+ options.getBriefModeInterval());
			logger.logDebug(HostManagerOptionsInterface.Option.DEAD_HOST_TIMEOUT + "="
					+ options.getDeadHostTimeout());
			logger.logDebug(HostManagerOptionsInterface.Option.DEFAULT_DETAILED_MODE_INTERVAL + "="
					+ options.getDefaultDetailedModeInterval());
			logger.logDebug(HostManagerOptionsInterface.Option.HOST_DETECTION_TIMEOUT + "="
					+ options.getHostDetectionTimeout());
			logger.logDebug(HostManagerOptionsInterface.Option.PENDING_HOSTS_REFRESH_INTERVAL + "="
					+ options.getPendingRefreshInterval());
			logger.logDebug("[Options end]");
		} catch (RemoteException e) {
			// ignore...
		}
		
		// Create database engine.
		HostDatabaseEngine database = null;
		
		try {
			database = new HostDatabaseEngine(databaseDirectory, logger);
		} catch (HostManagerException e) {
			logger.logFatal("Unable to create database engine.", e);
			throw new TaskException("Unable to create database engine.", e);
		} catch (FileNotFoundException e) {
			logger.logFatal("Unable to create database engine.", e);
			throw new TaskException("Unable to create database engine.", e);
		}
		
		appData = new HostManagerApplicationData(workingDirectory, logger, options, database);

		try {
			tryCopyExampleData("hosts");
		} catch (IOException e) {
			throw new TaskException("Unable to copy example data.", e);
		}
		
		try {
			hostManager = new HostManagerImplementation(appData, this);
		} catch (Exception e) {
			throw new TaskException("Unable to initialize Host Manager (" + e.getMessage() + ").", e);
		}

		appData.setHMI(hostManager);
		
		try {
			loadServer = new LoadServerImplementation(appData);
		} catch (Exception e) {
			throw new TaskException("Unable to initialize Load Server.", e);
		}
		
		appData.setLSI(loadServer);
		
		addRemoteInterface(REMOTE_INTERFACE_DATABASE, hostManager);
		addRemoteInterface(REMOTE_INTERFACE_MAIN, hostManager);
		addRemoteInterface(REMOTE_INTERFACE_LOAD_SERVER, loadServer);
		
		hostManager.initializeHostRuntimes();
	}
	
	/**
	 * @see cz.cuni.mff.been.task.Service#stop()
	 */
	@Override
	protected void stop() throws TaskException {
		
		try {
			hostManager.terminate();
		} catch (HostManagerException e) {
			throw new TaskException("Error terminating Host Manager.", e);
		}

		hostManager = null;
		
		try {
			loadServer.terminate();
		} catch (Exception e) {
			throw new TaskException("Error terminating Load Server.", e);
		}
		
		loadServer = null;
	}
	
	/**
	 * Test directory access rights and create requested directory in case it does not exist.
	 * 
	 * @param workingPath Directory parent.
	 * @param relativePath Directory path.
	 * 
	 * @return Path to the resulting directory.
	 */
	private String testAndCreateDirectory(String workingPath, String relativePath) {
		
		String directoryName = workingPath + File.separator + relativePath;
		
		File directory = new File(directoryName);

		if (!directory.isDirectory()) {
			logInfo("Creating \"" + relativePath + "\" directory.");
			
			if (!directory.mkdirs()) {
				logFatal("Unable to create required directory.");
				exitError();
			}
		} else if (!directory.canWrite()) {
			
			logFatal("Unsufficient access-rights to the \"" + relativePath + "\" directory. Need write access.");
			exitError();
		}
		
		return directoryName;
	}
	
	/**
	 * Test for existence of the target file and copy source if target is not found.
	 * 
	 * @param workingPath Target path.
	 * @param sourcePath Source directory.
	 * @param fileName Name of file to test.
	 */
	private void testAndCopyFile(String workingPath, String sourcePath, String fileName) {
		
		String targetName = workingPath + File.separator + fileName;
		
		File file = new File(targetName);
		
		if (!file.isFile()) {
			logInfo("File \"" + fileName + "\" does not exist. Copying from \"" + sourcePath + "\".");
			
			String sourceName = sourcePath + File.separator + fileName;
			
			try {
				copyFile(new File(sourceName), file);
			} catch (IOException e) {
				logFatal("Unable to copy file \"" + fileName + "\", message: " + e.getMessage());
				
				exitError();
			}
		} else if (!file.canWrite() || !file.canRead()) {
			logFatal("Insufficient access rights to the \"" + fileName + "\" file. "
					+ "Read and write permissions are needed.");
		}
	}
}
