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

import java.rmi.Remote;
import java.rmi.RemoteException;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.webinterface.Config;

/**
 * Class encapsulating reference to the BEEN service.
 * 
 * @author David Majda
 */
public class ServiceReference<T extends Remote> extends RMIReference<T> {
	/** Reference to the Task Manager. */
	private TaskManagerReference taskManagerReference;
	/** Service name. */
	private String serviceName;
	/** Service interface. */
	private String serviceInterface;
	/** Service human-readable name. */
	private String serviceHumanName;
	
	/** @return reference to the Task Manager */
	public TaskManagerReference getTaskManagerReference() {
		return taskManagerReference;
	}

	/** @return service name */
	public String getServiceName() {
		return serviceName;
	}

	/** @return service interface */
	public String getServiceInterface() {
		return serviceInterface;
	}

	/** @return service human-readable name */
	public String getServiceHumanName() {
		return serviceHumanName;
	}
	
	/**
	 * Acquires a reference to the BEEN service.
	 * 
	 * @return newly acquired reference
	 * @throws ComponentInitializationException if acquiring of the reference
	 *                                           failed
	 *                                           
	 * @see cz.cuni.mff.been.webinterface.ref.RMIReference#acquire()
	 */
	@SuppressWarnings("unchecked") /* Needed to make the "(T)" typecast work. */ 
	@Override
	public T acquire() throws ComponentInitializationException {
		T result;
		try {
			result = (T) taskManagerReference.get().serviceFind(
				serviceName, serviceInterface);
			if (result == null) {
				throw new ComponentInitializationException(
					"<strong>Task Manager can't find a running instance of the "
					+ serviceHumanName 
					+ ".</strong><br /><br />"
					+ "Go to the <a href=\"../../services/\">Services</a> tab and make "
					+ "sure the " + serviceHumanName + " is running."
				);
			}
			return result;
		} catch (RemoteException e) {
			throw new ComponentInitializationException(
				"<strong>Can't connect to the RMI registry on host \""
					+ Config.getInstance().getTaskManagerHostname()
					+ "\".</strong><br /><br />"
					+ "Make sure the RMI registry is running, is correctly "
					+ "configured, and there is no firewall in the way, which is "
					+ "blocking the connection.",
				e
			);
		}
	}

	/**
	 * Allocates a new <code>ServiceReference</code> object.
	 * 
	 * @param taskManagerReference reference to the Task Manager
	 * @param serviceName service name
	 * @param serviceInterface service interface
	 * @param serviceHumanName service human-readable name
	 */
	public ServiceReference(TaskManagerReference taskManagerReference,
			String serviceName, String serviceInterface, String serviceHumanName) {
		this.serviceName = serviceName;
		this.serviceInterface = serviceInterface;
		this.serviceHumanName = serviceHumanName;
		this.taskManagerReference = taskManagerReference;
	}
	
}
