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

import java.io.File;

import org.python.util.PythonInterpreter;

/**
 * <p>
 * Executes Jython script using JythonInterpreter.
 * </p>
 * 
 * @author Jan Tattermusch
 */
public class JythonScriptJob extends Job {
	private String scriptFileName;

	/** Name of the context variable which will be injected in the script's
	namespace */
	public static final String JYTHON_TASK_CONTEXT_NAME = "TASK";
	
	/**
	 * property containing the script to execute 
	 */
	public static final String SCRIPT_PROPERTY_NAME = "script";
	
	/**
	 * jython cachedir 
	 */
	public static final String JYTHON_CACHEDIR_NAME = "jython_cachedir";

	/**
	 * Allocates a new <code>JythonScript</code> object.
	 * 
	 * @throws TaskInitializationException
	 */
	public JythonScriptJob(String scriptFileName)
			throws TaskInitializationException {
		super();
		this.scriptFileName = scriptFileName;
	}
	
	/**
	 * Allocates a new <code>JythonScript</code> object.
	 * 
	 * @throws TaskInitializationException
	 */
	public JythonScriptJob() throws TaskInitializationException {
		super();
	}

	@Override
	protected void checkRequiredProperties() throws TaskException {
		if (scriptFileName == null) {
			checkRequiredProperty(SCRIPT_PROPERTY_NAME);
		}
	}

	@Override
	protected void run() throws TaskException {
		String cachedirName = getTempDirectory() + File.separator + JYTHON_CACHEDIR_NAME;
		System.setProperty("python.cachedir", cachedirName);
		logInfo("Jython package cachedir set to: " + cachedirName);
		
		PythonInterpreter interpreter = new PythonInterpreter();
		
		/* create jython task context */
		JythonTaskContext ctx = new JythonTaskContext(this);
		
		interpreter.set(JYTHON_TASK_CONTEXT_NAME, ctx );
		
		if (scriptFileName == null) {
			logInfo("Executing script got from \"" + SCRIPT_PROPERTY_NAME + "\" property.");
			
			String script = getTaskProperty(SCRIPT_PROPERTY_NAME);
			interpreter.exec(script);
			
		} else {
			logInfo("Executing Jython script \"" + scriptFileName + "\".");
		
			interpreter.execfile(scriptFileName);
		}

	}
}
