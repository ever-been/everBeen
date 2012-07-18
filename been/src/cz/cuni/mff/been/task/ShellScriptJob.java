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
package cz.cuni.mff.been.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.been.common.scripting.ScriptEnvironment;
import cz.cuni.mff.been.common.scripting.ScriptException;
import cz.cuni.mff.been.common.scripting.ScriptLauncher;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * <p>
 * Executes shell script.
 * </p>
 * 
 * @author Jan Tattermusch
 */
public class ShellScriptJob extends Job {
	private String scriptFileName = null;
	
	/**
	 * property containing the script to execute 
	 */
	public static final String SCRIPT_PROPERTY_NAME = "script";
	
	/**
	 * prefix of taskproperty variable
	 */
	public static final String TASKPROPERTY_VAR_PREFIX = "TASKPROP_";

	/**
	 * Allocates a new <code>ShellScriptJob</code> object.
	 * 
	 * @throws TaskInitializationException
	 */
	public ShellScriptJob(String scriptFileName)
			throws TaskInitializationException {
		super();
		this.scriptFileName = scriptFileName;
	}
	
	/**
	 * Allocates a new <code>ShellScriptJob</code> object.
	 * 
	 * @throws TaskInitializationException
	 */
	public ShellScriptJob()
			throws TaskInitializationException {
		super();
	}
	
	@Override
	protected void checkRequiredProperties() throws TaskException {
		if (scriptFileName == null) {
			checkRequiredProperty(SCRIPT_PROPERTY_NAME);
		}
	}

	/**
	 * Runs shell script.
	 * 
	 * While script is running, task directories' path can be obtained 
	 * from environment variables "WORKING_DIR", "TEMP_DIR" and "TASK_DIR".
	 * 
	 * Script's home directory is set to task's working dir by default.
	 */
	@Override
	protected void run() throws TaskException {
		
		ScriptLauncher scriptLauncher = new ScriptLauncher(); 
		logInfo("Running shell script.");
		ScriptEnvironment env = new ScriptEnvironment();
		env.setDirectory(new File(getWorkingDirectory()));
		putTaskProperties(env);	
		env.putEnv("WORKING_DIR", getWorkingDirectory());
		env.putEnv("TEMP_DIR", getTempDirectory());
		env.putEnv("TASK_DIR", getTaskDirectory());
		
		try {
			int result;
			
			if (scriptFileName != null) {
				File scriptFile = new File(scriptFileName);
				if (!scriptFile.exists()) {
					throw new TaskException("Script file \"" + scriptFile.getAbsolutePath() + "\" does not exist.");
				}
				result = scriptLauncher.runShellScript(scriptFile, env);

			} else {	
				String scriptString = getTaskProperty(SCRIPT_PROPERTY_NAME);
				
				String[] script = getScriptLines(scriptString);
				
				result = scriptLauncher.runShellScript(script, env);	
			}
			
			if (result != 0) {
				throw new TaskException("Error occured when shell script.");
			}
			
			logInfo("Script exited with success.");
		} catch (ScriptException e) {
			throw new TaskException("Error executing shell script.", e);
		} 
		
	}
	
	private String[] getScriptLines(String scriptString) throws TaskException {
		try {
			BufferedReader reader = new BufferedReader(new StringReader(scriptString));

			List<String> lines = new ArrayList<String>();

			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}

			String[] result = new String[lines.size()];
			lines.toArray(result);

			
			
			return result;
		} catch (IOException e) {
			throw new TaskException("Unexpected error when splitting script into lines", e);
		}
	}
	
	private void putTaskProperties(ScriptEnvironment env) {
		for (Object k : getTaskProperties().keySet()) {
			String key = (String) k;
			String value = getTaskProperty(key);
			
			if (key.matches("[A-Za-z_]+[A-Za-z0-9_]*")) {
				if (!key.equals(SCRIPT_PROPERTY_NAME)) {
					
					String varName = TASKPROPERTY_VAR_PREFIX + key;
					env.putEnv(varName, value);
					logInfo("Environment variable " + varName + " set.");
				}
			} else {
				logInfo("Task property " + key + " does not have safe name, environment variable will not be set.");
			}
		}
	}
}
