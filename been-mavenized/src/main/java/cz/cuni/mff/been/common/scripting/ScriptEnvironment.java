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
package cz.cuni.mff.been.common.scripting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class representing variable environment of a shell script.
 * It stores String-String name value pairs (variable names and their values).
 * 
 * @author Jan Tattermusch
 *
 */
public class ScriptEnvironment {
	
    /**
     * Environment variables to set
     */
	private Map<String, String> env = new HashMap<String,String> ();
	
	/**
	 * Home directory of script.
	 */
	private File directory;
	
	
	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File homeDirectory) {
		this.directory = homeDirectory;
	}

	/**
	 * Adds new environment variable
	 * @param name variable name
	 * @param value variable value
	 */
	public void putEnv(String name, String value) {
		env.put(name, value);
	}
	
	/**
	 * Retrieves value of environment variable
	 * @param name variable name
	 * @return variable value
	 */
	public String getEnv(String name) {
		return env.get(name);
	}
	
	/**
	 * 
	 * @return set of variable name for script
	 */
	public Set<String> variables() {
		return env.keySet();
	}
}
