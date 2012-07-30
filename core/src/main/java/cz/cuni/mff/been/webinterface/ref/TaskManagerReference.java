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

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.webinterface.Config;

/**
 * Class encapsulating reference to the Task Manager.
 * 
 * @author David Majda
 */
public class TaskManagerReference extends RMIReference<TaskManagerInterface> {

	/**
	 * Acquires a reference to the Task Manager.
	 * 
	 * @return newly acquired reference
	 * @throws ComponentInitializationException if acquiring of the reference
	 *                                           failed
	 *                                           
	 * @see cz.cuni.mff.been.webinterface.ref.RMIReference#acquire()
	 */
	@Override
	public TaskManagerInterface acquire()
			throws ComponentInitializationException {
		String taskManagerHostname = Config.getInstance().getTaskManagerHostname();
		try {
			return (TaskManagerInterface) Naming.lookup(
					"//" + taskManagerHostname + ":" + RMI.REGISTRY_PORT + TaskManagerInterface.URL
			);
		} catch (MalformedURLException e) {
			throw new ComponentInitializationException(e);
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
		} catch (NotBoundException e) {
			throw new ComponentInitializationException(
				"<strong>Can't connect to the Task Manager on host \""
					+ taskManagerHostname
					+ "\".</strong><br /><br />"
					+ "Make sure the Task Manager is running on host \""
					+ taskManagerHostname
					+ "\", or correct the host in the <a href=\"../../configuration/\">"
					+ "Configuration</a> tab.",
				e
			);
		}
	}

}
