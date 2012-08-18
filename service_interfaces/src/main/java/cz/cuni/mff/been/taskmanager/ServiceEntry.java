/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager;

import java.io.Serializable;
import java.net.URI;
import java.rmi.Remote;

/**
 * Class representing one entry for service lookup.
 * 
 * @author Antonin Tomecek
 */
public class ServiceEntry implements Serializable {

	private static final long	serialVersionUID	= -5737414864895605141L;

	/* Name of registered service. */
	private String serviceName = null;
	/* Name of registered service's RMI interface. */
	private String interfaceName = null;
	/* URI of registered RMI interface. */
	private URI rmiAddress = null;
	/* Representation of remote interface. */
	private Remote remoteInterface = null;
	/* Context ID of the service */
	private String contextId = null;
	/* Task ID of the service */
	private String taskId = null;
	

	/**
	 * Creates a new (empty) ServiceEntry.
	 * This constructor is needed by Serializable.
	 */
	public ServiceEntry() {
		
	}
	
	/**
	 * Creates a new ServiceEntry instance so that it is initialized by
	 * specified values.
	 * 
	 * @param serviceName Name of service.
	 * @param interfaceName Name of service's interface.
	 * @param rmiAddress URI of RMI interface.
	 * @param remoteInterface the remote interface.
	 * @param contextId context ID of the service.
	 * @param taskId task ID of the service.
	 */
	public ServiceEntry(String serviceName,
			String interfaceName,
			URI rmiAddress, 
			Remote remoteInterface,
			String contextId,
			String taskId) {
		this.setServiceName(serviceName);
		this.setInterfaceName(interfaceName);
		this.setRmiAddress(rmiAddress);
		this.setRemoteInterface(remoteInterface);
		this.setContextId(contextId);
		this.setTaskId(taskId);
	}
	
	/**
	 * Sets name of service.
	 * 
	 * @param serviceName Name of service.
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	/**
	 * Sets name of service's interface.
	 * 
	 * @param interfaceName Name of service's interface.
	 */
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
	/**
	 * Sets URI of RMI interface.
	 * 
	 * @param rmiAddress URI of RMI interface.
	 */
	public void setRmiAddress(URI rmiAddress) {
		this.rmiAddress = rmiAddress;
	}
	
	/**
	 * Sets reference for a remote object.
	 * 
	 * @param remoteInterface Reference for a remote object.
	 */
	public void setRemoteInterface(Remote remoteInterface) {
		this.remoteInterface = remoteInterface;
	}
	
	/**
	 * Returns name of service.
	 * 
	 * @return Name of service.
	 */
	public String getServiceName() {
		return this.serviceName;
	}
	
	/**
	 * Returns name of service's interface.
	 * 
	 * @return Name of service's interface.
	 */
	public String getInterfaceName() {
		return this.interfaceName;
	}
	
	/**
	 * Returns URI of RMI interface.
	 * 
	 * @return URI of RMI interface.
	 */
	public URI getRmiAddress() {
		return this.rmiAddress;
	}
	
	/**
	 * Returns reference for a remote object.
	 * 
	 * @return Reference for a remote object.
	 */
	public Remote getRemoteInterface() {
		return this.remoteInterface;
	}
	
	/**
	 * @return the context ID of the service.
	 */
	public String getContextId() {
		return this.contextId;
	}

	/**
	 * @param contextId the context ID of the service.
	 */
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	/**
	 * @return the task ID of the service.
	 */
	public String getTaskId() {
		return this.taskId;
	}

	/**
	 * @param taskId the task ID of the service.
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @return A clon of this instance.
	 */
	@Override
	protected ServiceEntry clone() {
		return new ServiceEntry(this.serviceName, this.interfaceName,
				this.rmiAddress, this.remoteInterface, this.contextId, this.taskId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.contextId == null) ? 0 : this.contextId.hashCode());
		result = prime * result + ((this.interfaceName == null) ? 0 : this.interfaceName.hashCode());
		result = prime * result + ((this.rmiAddress == null) ? 0 : this.rmiAddress.hashCode());
		result = prime * result + ((this.serviceName == null) ? 0 : this.serviceName.hashCode());
		result = prime * result + ((this.taskId == null) ? 0 : this.taskId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ServiceEntry other = (ServiceEntry) obj;
		if (this.contextId == null) {
			if (other.contextId != null) {
				return false;
			}
		} else if (!this.contextId.equals(other.contextId)) {
			return false;
		}
		if (this.interfaceName == null) {
			if (other.interfaceName != null) {
				return false;
			}
		} else if (!this.interfaceName.equals(other.interfaceName)) {
			return false;
		}
		if (this.rmiAddress == null) {
			if (other.rmiAddress != null) {
				return false;
			}
		} else if (!this.rmiAddress.equals(other.rmiAddress)) {
			return false;
		}
		if (this.serviceName == null) {
			if (other.serviceName != null) {
				return false;
			}
		} else if (!this.serviceName.equals(other.serviceName)) {
			return false;
		}
		if (this.taskId == null) {
			if (other.taskId != null) {
				return false;
			}
		} else if (!this.taskId.equals(other.taskId)) {
			return false;
		}
		return true;
	}
}
