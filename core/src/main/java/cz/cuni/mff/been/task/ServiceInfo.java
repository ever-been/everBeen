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
package cz.cuni.mff.been.task;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.TreeMap;

import cz.cuni.mff.been.taskmanager.TaskManagerException;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;

/**
 * Little helper class that contains information about a service. 
 * 
 * @author David Majda
 */
public class ServiceInfo {
	/**
	 * Run status of a service
	 * 
	 * @author Jaroslav Urban
	 * @author David Majda
	 */
	public enum Status {
		STARTING("starting"),
		RUNNING("running"),
		STOPPING("stopping"),
		RESTARTING("restarting");
		
		private static final TreeMap< String, Status >	reverseMap;
		
		static {
			reverseMap = new TreeMap< String, Status >();
			
			for ( Status status : Status.values() ) {
				reverseMap.put(  status.name, status );
			}
		}
		
		/** Name of the status, displayed in the user interface. */
		private final String name;
		
		/**
		 * Allocates a new <code>Status</code> object.
		 * 
		 * @param name name of the status
		 */
		private Status(String name) {
			this.name = name;
		}
		
		/**
		 * Returns a string representation of the object.
		 * 
		 * @return a string representation of the object
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return name;
		}
		
		public static Status fromString(String s) {
			Status	result;
			
			if ( ( result = reverseMap.get( s ) ) == null ) {
				throw new IllegalArgumentException("Invalid Status \"" + s + "\".");
			} else {
				return result;
			}
		}
	}
	
	/** Suffix used to generate TID of the service from its name. */
	private static final String TID_SUFFIX = "-tid";
	
	/** Service name. */
	private String name;
	/** Human-readable service name, displayed in the user interface. */ 
	private String humanName;
	/** Service TID. */
	private String tid;
	/** Host, where this service runs. */
	private String host;
	/** Service status. */
	private Status status;
	
	/** @return service name */
	public String getName() {
		return name;
	}

	/** @return human-readable service name */
	public String getHumanName() {
		return humanName;
	}
	
	/** @return service TID */
	public String getTid() {
		return tid;
	}

	/** @return host, where this service runs */
	public String getHost() {
		return host;
	}
	
	/** @return service status */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * Allocates a new <code>ServiceInfo</code> object.
	 * 
	 * @param name service name
	 * @param humanName human-readable service name
	 */
	public ServiceInfo(String name, String humanName) {
		super();
		this.name = name;
		this.humanName = humanName;
		this.tid = name + TID_SUFFIX;
	}

	/**
	 * Fills in the host, whee service runs, and its status. The information
	 * is obtained from the naming service.
	 * 
	 * If the information can't be obtainted, host, status, or both are set to
	 * <code>null</code>.  
	 * 
	 * @param taskManager Task Manager interface used to obtain status information
	 * @throws RemoteException when something in RMI goes bad
	 */
	public void fillHostAndStatus(TaskManagerInterface taskManager) throws RemoteException {
		/* Initialize filled attributes to values indicating failure. */
		host = "";
		status = null;

		/* Try to get the service host using the Naming Service. */
		URI uri;
		try {
			uri = taskManager.serviceFindURI(name, Service.RMI_CONTROL_IFACE);
		} catch (RemoteException e) {
			return;
		}
		
		if (uri != null) {
			host = uri.getHost();
		} else {
			return;
		}
				
		/* Try to connect to service's control interface. If we can do this,
		 * try to determine the service state using checkpoints registered by the
		 * Task Manager. */
		try {
			ServiceControlInterface controlInterface
				= (ServiceControlInterface) taskManager.serviceFind(name, Service.RMI_CONTROL_IFACE);
			if (controlInterface != null) {
				status = Status.fromString((String) taskManager.checkPointLook(
					Service.STATUS_CHECKPOINT,
					tid,
					TaskManagerInterface.SYSTEM_CONTEXT_ID,
					0
				));
			}
		} catch (RemoteException e) {
			/* Do nothing, status is set to null allready. */
		} catch (TaskManagerException e) {
			/* Do nothing, status is set to null allready. */
		}
	}
}
