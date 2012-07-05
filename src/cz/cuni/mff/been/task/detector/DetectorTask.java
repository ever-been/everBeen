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

package cz.cuni.mff.been.task.detector;

import java.net.InetAddress;

import java.rmi.RemoteException;

import cz.cuni.mff.been.hostmanager.database.DatabaseManagerInterface;

import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * Detector task detects hardware and software configuration of the computer on which it is run.
 * Configuration is then send to the Host Manager which will store it in database (after validation
 * of course).
 * Detector task contains native libraries which are written specifically for each supported operating
 * system (currently Windows, Linux and Solaris). If native library is not available for current
 * system, only limited information will be available about the host.
 *
 * @author Branislav Repcek
 */
public class DetectorTask extends Job {

	/**
	 * Host key property name.
	 */
	private static final String PROPERTY_HOST_KEY = "key";
	
	/**
	 * Object which communicates with native libraries.
	 */
	private NativeDetector detector;
	
	/**
	 * Initialize detector task.
	 * 
	 * @throws TaskInitializationException If initialization failed.
	 */
	public DetectorTask() throws TaskInitializationException {
	}

	/*
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	public void run() throws TaskException {
		
		detector = new NativeDetector();

		if (!detector.execute()) {
			logError("Error detecting host features.");
		} else {
			String key = getTaskProperty(PROPERTY_HOST_KEY);

			DatabaseManagerInterface databaseManager = null;
			try {
				databaseManager = 
					(DatabaseManagerInterface) getTasksPort().serviceFind("hostmanager", "database");
			} catch (RemoteException e) {
				throw new TaskException("Unable to find Host Database Manager.", e);
			}
				
			String hostName = null;
			
			try {
				hostName = InetAddress.getLocalHost().getCanonicalHostName();
			} catch (Exception e) {
				throw new TaskException("Unable to retrieve canonical name for localhost", e);
			}
			
			try {
				databaseManager.uploadHostData(hostName, 
				                               key, 
				                               detector.getDataString(), 
				                               detector.getEncoding());
			} catch (Exception e) {
				throw new TaskException("Unable to upload data to the Host Manager.", e);
			}
			
			logDebug("Data uploaded successfully.");
		}
	}

	/*
	 * @see cz.cuni.mff.been.task.Job#checkRequiredProperties()
	 */
	@Override
	protected void checkRequiredProperties() throws TaskException {
		
		checkRequiredProperties(new String[] {PROPERTY_HOST_KEY});
	}
}
