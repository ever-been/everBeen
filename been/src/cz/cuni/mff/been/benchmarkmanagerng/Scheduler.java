/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
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
package cz.cuni.mff.been.benchmarkmanagerng;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;

import cz.cuni.mff.been.task.Task;

/**
 * @author Jiri Tauber
 *
 */
public class Scheduler extends Thread {

	/** How often does scheduler wake up to check analyses */
	public static final int SLEEP_TIME_MILIS = 60000;   // a minute by default

	private BenchmarkManagerInterface benchmarkManager;
	private Task task;

	private HashMap<String, Integer> blockedAnalyses = new HashMap<String, Integer>();

	/**
	 * 
	 */
	public Scheduler(BenchmarkManagerInterface benchmarkManager) {
		super();
		this.benchmarkManager = benchmarkManager;
		this.task = Task.getTaskHandle();
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		Collection<Analysis> analyses;
		while( true ){
			if( interrupted() ){
				logWarning("Scheduler has been interrupted");
				break;
			}
			try {
				sleep(SLEEP_TIME_MILIS);
			} catch (InterruptedException e1) {
				interrupt(); // set the interrupted flag - it has been cleared;
				continue;
			}

			try {
				analyses = benchmarkManager.getAnalyses();
			} catch (BenchmarkManagerException e) {
				dumpException("Couldn't load analyses because error occured: ",e);
				continue; // i.e. go to sleep
			} catch (RemoteException e) {
				assert false : "RemoteException occured in local call: "+e.toString();
				continue;
			}
			Integer blockCount;
			for (Analysis analysis : analyses) {
				if( analysis.shouldBeScheduled() ){
					try {
						benchmarkManager.runAnalysis(analysis.getName(), false);
					} catch (Exception e) {
						blockCount = blockedAnalyses.get(analysis.getName());
						if( blockCount == null ){
							blockCount = 0;
							logWarning("Couldn't run analysis '"+analysis.getName()
									+"' because error occured: "+e.getMessage());
						}
						blockCount++;
						blockedAnalyses.put(analysis.getName(), blockCount);
					}
				}
			}
		}
	}


	/**
	 * @param message
	 * @param e
	 */
	private void dumpException(String message, Throwable e) {
		logError(message+" because error occured: "+e.getMessage());
		System.err.println(message+" because error occured: "+e.getMessage());
		e.printStackTrace();
		System.err.println("----------------------------------------");
	}


	/**
	 * Logs a warning message.
	 * It uses task logging facility to write the message if possible,
	 * otherwise it prints it to the standard output.
	 * 
	 * @param message message text
	 */
	private void logWarning(String message){
		if (task != null){
			task.logWarning(message);
		} else {
			System.out.println("WARNING: "+message);
		}
	}


	/**
	 * Logs an error message.
	 * It uses task logging facility to write the message if possible,
	 * otherwise it prints it to the error output.
	 * 
	 * @param message message text
	 */
	private void logError(String message) {
		if (task != null) {
			task.logError(message);
		} else {
			System.err.println(message);
		}
	}


}
