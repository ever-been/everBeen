/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.clinterface.ref;

import java.rmi.Remote;
import java.rmi.RemoteException;

import cz.cuni.mff.been.common.ComponentInitializationException;

/**
 * Class encapsulating reference to the BEEN service.
 * 
 * @author David Majda
 * @author Andrej Podzimek
 */
public class ServiceReference< T extends Remote > extends RMIReference< T > {
	
	/** Reference to the Task Manager. */
	private final TaskManagerReference taskManagerReference;
	
	/** Service name. */
	private final String serviceName;
	
	/** Service interface. */
	private final String serviceInterface;
	
	/** Service human-readable name. */
	private final String serviceHumanName;
	
	/**
	 * Initializes a new service reference with the supplied service lookup data.
	 * 
	 * @param taskManagerReference A reference to the Task Manager.
	 * @param serviceName Name of the service.
	 * @param serviceInterface Name of the (sub)interface.
	 * @param serviceHumanName Human-readable name of the service.
	 */
	public ServiceReference(
		TaskManagerReference taskManagerReference,
		String serviceName,
		String serviceInterface,
		String serviceHumanName
	) {
		this.serviceName = serviceName;
		this.serviceInterface = serviceInterface;
		this.serviceHumanName = serviceHumanName;
		this.taskManagerReference = taskManagerReference;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public T acquire() throws ComponentInitializationException {
		Remote result;
		
		try {
			result = taskManagerReference.get().serviceFind( serviceName, serviceInterface );
			if ( null == result ) {
				throw new ComponentInitializationException(
					"Task Manager can't find a running " + serviceHumanName + '.'
				);
			}
			try {
				return (T) result;
			} catch ( ClassCastException exception ) {
				throw new ComponentInitializationException(
					"Service reference to '" + serviceName + "' points at an instance of " +
					result.getClass().getSimpleName() + ". Type mismatch.",
					exception
				);
			}
		} catch ( RemoteException exception ) {
			throw new ComponentInitializationException(
				"CLI service could not obtain a reference to the " + serviceHumanName + '.',
				exception
			);
		}
	}
}
