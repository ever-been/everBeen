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
package cz.cuni.mff.been.webinterface.ref;

import java.rmi.RemoteException;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.load.LoadServerInterface;
import cz.cuni.mff.been.webinterface.Config;

import static cz.cuni.mff.been.services.Names.HOST_MANAGER_SERVICE_NAME;
import static cz.cuni.mff.been.services.Names.HOST_MANAGER_REMOTE_INTERFACE_MAIN;
import static cz.cuni.mff.been.services.Names.HOST_MANAGER_SERVICE_HUMAN_NAME;

/**
 * Class encapsulating reference to the Load Server.
 * 
 * @author David Majda
 */
public class LoadServerReference extends RMIReference<LoadServerInterface> {
	/** Reference to the Task Manager. */
	private TaskManagerReference taskManagerReference;

	/**
	 * Acquires a reference to the Load Server.
	 * 
	 * @return newly acquired reference
	 * @throws ComponentInitializationException if acquiring of the reference
	 *          failed
	 *                                           
	 * @see cz.cuni.mff.been.webinterface.ref.RMIReference#acquire()
	 */
	@Override
	public LoadServerInterface acquire()
			throws ComponentInitializationException {
		String taskManagerHostname = Config.getInstance().getTaskManagerHostname();
		try {
			HostManagerInterface hostManager
				= (HostManagerInterface) taskManagerReference.get().serviceFind(
					HOST_MANAGER_SERVICE_NAME,
					HOST_MANAGER_REMOTE_INTERFACE_MAIN
				);
			if (hostManager != null) {
				return hostManager.getLoadServer();
			} else {
				throw new ComponentInitializationException(
					"<strong>Task Manager can't find a running instance of the "
					+HOST_MANAGER_SERVICE_HUMAN_NAME
					+ ".</strong><br /><br />"
					+ "Go to the <a href=\"../../services/\">Services</a> tab and make "
					+ "sure the " + HOST_MANAGER_SERVICE_HUMAN_NAME + " is running."
				);
			}
		} catch (RemoteException e) {
			throw new ComponentInitializationException(
				"<strong>Can't connect to the RMI registry on host \""
					+ taskManagerHostname
					+ "\".</strong><br /><br />"
					+ "Make sure the RMI registry is running, is correctly "
					+ "configured, and there is no firewall in the way, which is "
					+ "blocking the connection.",
				e
			);
		}
	}

	/**
	 * Allocates a new <code>LoadServerReference</code> object.
	 * 
	 * @param taskManagerReference reference to the Task Manager
	 */
	public LoadServerReference(TaskManagerReference taskManagerReference) {
		this.taskManagerReference = taskManagerReference;
	}

}
