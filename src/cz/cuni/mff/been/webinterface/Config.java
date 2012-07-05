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
package cz.cuni.mff.been.webinterface;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Class used to configuration settings management. The configuration is built
 * around Java's <code>Preferences></code> class. 
 * 
 * @author David Majda
 */
public class Config {
	private static final String 
		TASK_MANAGER_HOSTNAME_KEY = "taskManagerHostname";
	private static final String 
		TASK_MANAGER_HOSTNAME_DEFAULT = "localhost";
    
	private static final String 
		SHOW_DEBUG_OPTIONS_KEY = "showDebugOptions";
	private static final boolean 
		SHOW_DEBUG_OPTIONS_DEFAULT = false;

	/** Class instance (singleton pattern). */
	private static Config instance;
	/** Internal preferences object. */
	private Preferences preferences = Preferences.userNodeForPackage(getClass());
	   
	/** @return returns the Task Manager hostname */
	public String getTaskManagerHostname() {
		return preferences.get(
			TASK_MANAGER_HOSTNAME_KEY,
			TASK_MANAGER_HOSTNAME_DEFAULT
		);
	}
    
	/**
	 * Sets the Task Manager hostname.
	 * 
	 * @param value new value of the Task Manager hostname
	 * @throws BackingStoreException if this operation cannot be completed due to
	 *          a failure in the preferences backing store, or inability to
	 *          communicate with it.
	 */
	public void setTaskManagerHostname(String value) throws BackingStoreException {
		preferences.put(TASK_MANAGER_HOSTNAME_KEY, value);
		preferences.flush();
	}
    
	/** @return returns the flag whether to show debug options */
	public boolean getShowDebugOptions() {
		return preferences.getBoolean(
			SHOW_DEBUG_OPTIONS_KEY,
			SHOW_DEBUG_OPTIONS_DEFAULT
		);
	}
    
	/**
	 * Sets the flag whether to show debug options.
	 * 
	 * @param value new value of the flag whether to show debug options
	 * @throws BackingStoreException if this operation cannot be completed due to
	 *          a failure in the preferences backing store, or inability to
	 *          communicate with it.
	 */
	public void setShowDebugOptions(boolean value) throws BackingStoreException {
		preferences.putBoolean(SHOW_DEBUG_OPTIONS_KEY, value);
		preferences.flush();
	}

	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}
	
	/**
	 * Allocates a new <code>Config</code> object. Constructor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private Config() {
		super();
	}		
}
