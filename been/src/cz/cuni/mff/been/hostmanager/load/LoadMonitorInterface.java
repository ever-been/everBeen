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

import java.rmi.Remote;
import java.rmi.RemoteException;

import cz.cuni.mff.been.hostmanager.IllegalOperationException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;

/**
 * <p>Basic interface for the Load Monitor. Load Monitor is running on every host and collects data
 * from native library and sends results to the Load Server. Load Monitor can function in two modes -
 * detailed mode and brief mode. These modes are mutually exclusive.</p>
 * <p>In brief mode load samples are collected in sparse intervals and are immediately sent to the 
 * Load Server. Sampling interval in brief mode should be in tens of seconds (default value is 10 
 * seconds). This interval is dense enough to detect computer crash and still does not generate 
 * gigabytes of data for each computer.</p>
 * <p>In detailed mode samples can be taken more often. This is possible since detailed mode data
 * is saved to the disk.To provide user with some data about host even in detailed mode, some samples
 * are sent to the Load Server. Which samples are sent is determined from the sampling intervals in
 * brief and detailed modes. Every n-th sample is sent, where n is integer such that
 * <br>
 * n * detailedInterval &lt; briefInterval &lt; (n + 1) * detailedInterval
 * <br>
 * Data from detailed mode can later be queried using getDetailedModeData(). Detailed mode data will
 * not be stored in Load Server, but in Results Repository instead.
 *
 * @author Branislav Repcek
 */
public interface LoadMonitorInterface extends Remote {

	/**
	 * Name under which Load Monitor registers itself on each host.
	 */
	String RMI_NAME = "been/loadmonitor";
	
	/**
	 * Default sampling interval for brief mode in milliseconds.
	 */
	int DEFAULT_BRIEF_INTERVAL = 10000;
	
	/**
	 * Default sampling interval for detailed mode in milliseconds.
	 */
	int DEFAULT_DETAILED_INTERVAL = 1000;
	
	/**
	 * Describes mode in which Load Monitor is taking samples.
	 *
	 * @author Branislav Repcek
	 */
	public enum LoadMonitorMode {
		
		/**
		 * No samples are taken (== Load Monitoring is disabled).
		 */
		MODE_NONE,
		
		/**
		 * Load Monitor is in brief mode.
		 */
		MODE_BRIEF,
		
		/**
		 * Load monitor is in detailed mode.
		 */
		MODE_DETAILED
	}
	
	/**
	 * Set name of the host on which Load Server is running.
	 * 
	 * @param lsHostName Name of the host on which Load Server is running.
	 * @param loadServer Reference to the target Load Server.
	 * @param defaultDetailedModeInterval Default interval for detailed mode in milliseconds. Value
	 *        has to be at least 1. 
	 * @param briefModeInterval Interval between samples in brief mode. Measured in milliseconds,
	 *        minimum is 1 ms. 
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws LoadMonitorException If host cannot be resolved or lookup failed.
	 * @throws InvalidArgumentException If hostname is invalid or if some interval is too small.
	 */
	void initialize(String lsHostName, LoadServerInterface loadServer, 
			long defaultDetailedModeInterval, long briefModeInterval) 
			throws RemoteException, InvalidArgumentException, LoadMonitorException;
	
	/**
	 * Terminate Load Monitor. Monitor will stop taking samples.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	void terminate() throws RemoteException;
	
	/**
	 * Begin measuring in brief mode. Brief mode is automatically enabled when Load Monitor starts up.
	 * Brief mode cannot be started if monitor is already in brief or detailed mode.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If monitor is already in brief or detailed mode.
	 * @throws LoadMonitorException If monitor was unable to schedule new thread.
	 */
	void startBriefMode() throws RemoteException, LoadMonitorException, IllegalOperationException;
	
	/**
	 * Stop measuring samples in brief mode.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If monitor is not in brief mode.
	 */
	void stopBriefMode() throws RemoteException, IllegalOperationException;
	
	/**
	 * Start measuring in detailed mode. All samples taken in detailed mode are stored on the current 
	 * host and only some of them are sent to the Load Server instead of the brief mode samples.
	 * Note that combination of the context id and task id is unique identifier. Default interval
	 * is used when taking samples.
	 * 
	 * @param contextId Id of the context in which task that requests detailed mode is running.
	 * @param taskId Id of the task that requests detailed mode.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If monitor is already in detailed mode or if id is already in use.
	 * @throws LoadMonitorException If monitor was unable to schedule new thread or if there was an
	 *         error updating database files after monitor has been started.
	 */
	void startDetailedMode(String contextId, String taskId) 
		throws RemoteException, IllegalOperationException, LoadMonitorException;
	
	/**
	 * Start measuring in detailed mode. All samples taken in detailed mode are stored on the current 
	 * host and only some of them are sent to the Load Server instead of the brief mode samples.
	 * Note that combination of the context id and task id is unique identifier.
	 * 
	 * @param contextId Id of the context in which task that requests detailed mode is running.
	 * @param taskId Id of the task that requests detailed mode.
	 * @param interval Interval in which samples will be measured.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If monitor is already in detailed mode or if id is already in use.
	 * @throws LoadMonitorException If monitor was unable to schedule new thread or if there was an
	 *         error updating database files after monitor has been started.
	 * @throws InvalidArgumentException If interval is too small (less than 1 ms).
	 */
	void startDetailedMode(String contextId, String taskId, long interval) 
		throws RemoteException, IllegalOperationException, LoadMonitorException, InvalidArgumentException;
	
	/**
	 * Stop measuring in detailed mode. If brief mode was running when detailed mode has been enabled,
	 * brief mode is enabled again. Otherwise, no load measurements are taken.
	 * To request data collected in detailed mode, call <tt>getDetailedModeData()</tt>.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If monitor is not in detailed mode.
	 * @throws LoadMonitorException If brief mode cannot be switched back on.
	 */
	void stopDetailedMode() 
		throws RemoteException, IllegalOperationException, LoadMonitorException;
	
	/**
	 * Get interval in which load samples are taken in brief mode.
	 * 
	 * @return Sampling interval in milliseconds.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	long getBriefInterval() throws RemoteException;
	
	/**
	 * Set interval in which load samples are taken in brief mode. If this interval is set too low
	 * (samples are taken too often), it can severely affect performance. If you need to take samples
	 * often consider switching to the brief mode. New interval will take effect immediately.
	 * 
	 * @param interval Interval between two samples in brief mode in milliseconds. Note that this 
	 *        interval is not exact and samples may drift (they are usually taken a little later when
	 *        system is under heavy load). Interval cannot be smaller than or equal to zero.
	 *
	 * @throws RemoteException If RMI error occurred.
	 * @throws InvalidArgumentException If interval is zero or negative.
	 * @throws LoadMonitorException If there was an error while saving configuration.
	 */
	void setBriefInterval(long interval) 
		throws RemoteException, InvalidArgumentException, LoadMonitorException;
	
	/**
	 * Get sampling interval in detailed mode.
	 * 
	 * @return Detailed mode sampling interval in milliseconds.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	long getDefaultDetailedInterval() throws RemoteException;
	
	/**
	 * Set sampling interval for detailed mode. New interval will be used next time Load Monitor is
	 * switched to the detailed mode.
	 * 
	 * @param interval Sampling interval in milliseconds. Interval has to be greater than zero.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InvalidArgumentException If interval is negative or zero.
	 * @throws LoadMonitorException If there was an error while saving configuration.
	 */
	void setDefaultDetailedInterval(long interval) 
		throws RemoteException, InvalidArgumentException, LoadMonitorException;
	
	/**
	 * @return Mode in which Load Monitor is currently working.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	LoadMonitorMode getMode() throws RemoteException;
	
	/**
	 * Remove data cached when in detailed mode.
	 * 
	 * @param contextId Id of the context in which task that requested detailed mode is running.
	 * @param taskId Id of the task that requested detailed mode.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If monitor is already in detailed mode.
	 * @throws InvalidArgumentException If some parameter is empty string or <tt>null</tt>.
	 * @throws ValueNotFoundException If no data file exists for given IDs.
	 */
	void clearDetailedModeData(String contextId, String taskId) 
		throws RemoteException, IllegalOperationException, InvalidArgumentException, ValueNotFoundException;

	/**
	 * Remove all data from detailed mode data cache.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If monitor is currently in detailed mode.
	 */
	void clearDetailedModeData() throws RemoteException, IllegalOperationException;
	
	/**
	 * Remove all detailed mode data belonging to the given context. That is, all data generated
	 * by the tasks from given context will be removed.
	 * 
	 * @param contextId Id of the context.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If monitor is currently in detailed mode.
	 * @throws ValueNotFoundException If no data for given context exist.
	 */
	void clearDetailedModeData(String contextId)
		throws RemoteException, IllegalOperationException, ValueNotFoundException;
	
	/**
	 * Get handle of the load data file with given IDs.
	 * 
	 * @param contextId Id of the context in which task that requested detailed mode is running.
	 * @param taskId Id of the task that requested detailed mode.
	 * 
	 * @return Handle to the load file, File can be opened only from the local computer. If no file
	 *         has been found for given context and task, <tt>null</tt> is returned.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InvalidArgumentException If some id is invalid (empty string or <tt>null</tt>).
	 * @throws ValueNotFoundException If no data has been found for given combination of the 
	 *         context ID and task ID.
	 */
	LoadFile getDetailedModeLoadData(String contextId, String taskId)
		throws RemoteException, InvalidArgumentException, ValueNotFoundException;
	
	/**
	 * Test if detailed load data are present for given combination of the task ID and context ID.
	 * 
	 * @param contextId Id of the context in which the task has been run.
	 * @param taskId Id of the task.
	 * 
	 * @return <tt>true</tt> if detailed load data are present for given combination of the context
	 *         id and task id, <tt>false</tt> otherwise. 
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InvalidArgumentException If some id is <tt>null</tt> or empty string.
	 */
	boolean hasDetailedModeData(String contextId, String taskId)
		throws RemoteException, InvalidArgumentException;
	
	/**
	 * Use this method to determine if current operating system has native Load Monitor library.
	 * If no native library is found (or loading failed) for the current OS, Load Monitor will 
	 * function in very limited mode (samples will contain only timestamps). In such case, there's 
	 * no point in switching monitor to the detailed mode (however, it is still supported) since
	 * no useful data will be collected.
	 * 
	 * @return <tt>true</tt> if monitor has native library for current OS, <tt>false</tt> otherwise.
	 * 
	 * @throws RemoteException
	 */
	boolean hasNativeSupport() throws RemoteException;
	
	/**
	 * Retrieve description of current hardware.
	 *  
	 * @return Description of the hardware.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	HardwareDescription getHardwareDescription() throws RemoteException;
	
	/**
	 * Empty method to test if host is online.
	 * 
	 * @return Current time.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	long ping() throws RemoteException;
}
