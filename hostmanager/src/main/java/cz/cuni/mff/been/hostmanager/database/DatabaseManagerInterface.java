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

package cz.cuni.mff.been.hostmanager.database;

import java.rmi.Remote;
import java.rmi.RemoteException;

import cz.cuni.mff.been.hostmanager.HostManagerException;

/**
 * Interface to the Host Manager Database Manager. It is used to upload new files to the host 
 * database by the detectors. This interface is used internally by the detector tasks and you should 
 * not use this by yourself.
 *
 * @author Branislav Repcek
 */
public interface DatabaseManagerInterface extends Remote {

	/**
	 * Upload data for new host. This method is used by the detector task to upload host's data when
	 * data collection is finished.
	 * 
	 * @param hostName Name of the host as reported by the detector.
	 * @param hostKey Key assigned to the host by the Host Manager when executing task.
	 * @param data Data collected on the host.
	 * @param outEncoding Desired encoding of the output file. This should be the same one as encoding
	 *        used by the detector library or there may be errors when parsing resulting file if it
	 *        contains some special characters.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostManagerException If there was an error while updating database of parsing received data.
	 */
	void uploadHostData(String hostName, String hostKey, String data, String outEncoding) 
		throws RemoteException, HostManagerException;
}
