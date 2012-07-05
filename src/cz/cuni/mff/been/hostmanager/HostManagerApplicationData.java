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

import cz.cuni.mff.been.common.id.IDManager;
import cz.cuni.mff.been.common.id.IDManagerInterface;
import cz.cuni.mff.been.common.id.OID;
import cz.cuni.mff.been.hostmanager.database.HostDatabaseEngine;
import cz.cuni.mff.been.hostmanager.load.LoadServerImplementation;

/**
 * Store data that are provided by the service to the different parts of the Host Manager.
 *
 * @author Branislav Repcek
 */
public class HostManagerApplicationData {

	/**
	 * Logger.
	 */
	private HostManagerLogger logger;
	
	/**
	 * Configuration.
	 */
	private HostManagerOptionsInterface configuration;
	
	/**
	 * Database engine.
	 */
	private HostDatabaseEngine database;
	
	/**
	 * Working directory.
	 */
	private String workingDirectory;
	
	/**
	 * Id manager used in the Load Server and Host Manager.
	 */
	private IDManagerInterface idManager;
	
	/**
	 * Host Manager.
	 */
	private HostManagerImplementation hmi;
	
	/**
	 * Load Server.
	 */
	private LoadServerImplementation lsi;
	
	/**
	 * Create new application settings.
	 * 
	 * @param workingDirectory Working directory path (absolute).
	 * @param logger Logger.
	 * @param configuration Configuration storage.
	 * @param database Database engine.
	 */
	public HostManagerApplicationData(String workingDirectory, HostManagerLogger logger, 
			HostManagerOptionsInterface configuration, HostDatabaseEngine database) {
		
		this.workingDirectory = workingDirectory;
		this.logger = logger;
		this.configuration = configuration;
		this.database = database;
		this.idManager = IDManager.getInstance();
	}

	/**
	 * @return Configuration.
	 */
	public HostManagerOptionsInterface getConfiguration() {
		
		return configuration;
	}

	/**
	 * @return Logger.
	 */
	public HostManagerLogger getLogger() {
		
		return logger;
	}

	/**
	 * @return Absolute path to the workingDirectory.
	 */
	public String getWorkingDirectory() {
		
		return workingDirectory;
	}

	/**
	 * @return Database engine.
	 */
	public HostDatabaseEngine getDatabase() {
		
		return database;
	}
	
	/**
	 * @return Id manager.
	 */
	public IDManagerInterface getIDManager() {
		
		return idManager;
	}
	
	/**
	 * Get next ID of the given type.
	 * 
	 * @param <T> Type of the ID to retrieve.
	 * 
	 * @param clazz Class of the ID to retrieve.
	 *  
	 * @return New Id of the given type.
	 */
	public synchronized < T extends OID > T getNextID(Class< T > clazz) {
		
		return idManager.getNext(clazz);
	}

	/**
	 * @return Host Manager..
	 */
	public HostManagerImplementation getHMI() {
		
		return hmi;
	}

	/**
	 * @param hmi Host Manager.
	 */
	public void setHMI(HostManagerImplementation hmi) {
		
		this.hmi = hmi;
	}

	/**
	 * @return Load Server.
	 */
	public LoadServerImplementation getLSI() {
		
		return lsi;
	}

	/**
	 * @param lsi Load Server.
	 */
	public void setLSI(LoadServerImplementation lsi) {
		
		this.lsi = lsi;
	}
	
	
}
