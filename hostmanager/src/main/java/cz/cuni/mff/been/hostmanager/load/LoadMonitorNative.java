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

package cz.cuni.mff.been.hostmanager.load;

import cz.cuni.mff.been.common.util.TimeUtils;

/**
 * Adapter for the native library for Load Monitor.
 *
 * @author Branislav Repcek
 */
public class LoadMonitorNative {

	/**
	 * Initialise native library.
	 * 
	 * @return <tt>true</tt> on success, <tt>false</tt> otherwise.
	 */
	private native boolean nativeInitialize();
	
	/**
	 * Terminate native library.
	 * 
	 * @return <tt>true</tt> on success, <tt>false</tt> otherwise.
	 */
	private native boolean nativeTerminate();
	
	/**
	 * Get sample.
	 * 
	 * @return Sample containing load data.
	 */
	private native LoadSample nativeGetSample();
	
	/**
	 * @return Description of the hardware.
	 */
	private native HardwareDescription nativeGetHardwareDescription();
	
	/**
	 * Flag which determines if native library is used.
	 */
	private boolean usingNative;

	/**
	 * Create Load Monitor Native Library Adapter.
	 * 
	 * @param libraryPath Full path to the directory which contains Load Monitor library.
	 */
	public LoadMonitorNative(String libraryPath) {
		// Which OS are we running on?
		String sysname = System.getProperty("os.name").toLowerCase();
		
		// Find out where we are running.
		logInfo("Library path: " + libraryPath);

		try {
			// now we try to load native libraries
			
			if (sysname.indexOf("windows") != -1) {
				// we are running on Windows.
				logInfo("Attempting to load Windows Load Monitor library.");
				
				System.load(libraryPath + "LoadMonitor.wnm");
				
				logInfo("Link successful.");
				usingNative = true;
			} else if (sysname.indexOf("linux") != -1) {
				// we are running on Linux.
				logInfo("Attempting to load Linux Load Monitor library.");
				
				System.load(libraryPath + "LoadMonitor.lnm");
				
				logInfo("Link successful.");
				usingNative = true;
			} else if (sysname.indexOf("solaris") != -1) {
				// we are running on Solaris.
				logInfo("Attempting to load Solaris Load Monitor library.");
				
				System.load(libraryPath	+ "LoadMonitor.snm");
				
				logInfo("Link successful.");
				usingNative = true;
			} else {
				// We are running on OS which does not have native detector library.
				usingNative = false;
			}
		} catch (Exception e) {
			usingNative = false;
			logError("Unable to load Load Monitor library. Reason: " + e.getMessage());
		} catch (Error e) {
			usingNative = false;
			logError("Unable to load Load Monitor library. Message: " + e.getMessage());
		}
	}
	
	/**
	 * Initialise native library.
	 * 
	 * @return <tt>true</tt> on success, <tt>false</tt> otherwise.
	 */
	public synchronized boolean initialize() {
		
		if (usingNative) {
			logInfo("Initializing Load Monitor library.");
			if (!nativeInitialize()) {
				logError("Initialization failed.");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Terminate native library (this will not unload library from the memory).
	 * 
	 * @return <tt>true</tt> on success, <tt>false</tt> otherwise.
	 */
	public synchronized boolean terminate() {
		
		if (usingNative) {
			logInfo("Terminating Load Monitor library.");
			if (!nativeTerminate()) {
				logError("Termination failed.");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Get load sample.
	 * 
	 * @return Load Sample.
	 */
	public synchronized LoadSample getSample() {
		
		if (usingNative) {
			return nativeGetSample();
		} else {
			return new LoadSample();
		}
	}
	
	/**
	 * @return Description of the hardware that is monitor by the native library.
	 */
	public synchronized HardwareDescription getHardwareDescription() {
		
		if (usingNative) {
			return nativeGetHardwareDescription();
		} else {
			return new HardwareDescription();
		}
	}

	/**
	 * @return <tt>true</tt> is native library initialisation was successful, <tt>false</tt> otherwise.
	 */
	public boolean usingNative() {
		
		return usingNative;
	}
	
	/**
	 * Log info message to the standard output.
	 * 
	 * @param s Message to log.
	 */
	private static void logInfo(String s) {
		
		if ("true".equals(System.getenv("BEEN_HOSTRUNTIME_DEBUG"))) {
			System.out.println(TimeUtils.nowFormated() + " INFO " + s);
		}
	}
	
	/**
	 * Log error message to the error output.
	 * 
	 * @param s Message to log.
	 */
	private static void logError(String s) {

		if ("true".equals(System.getenv("BEEN_HOSTRUNTIME_DEBUG"))) {
			System.err.println(TimeUtils.nowFormated() + " ERROR " + s);
		}
	}
}
