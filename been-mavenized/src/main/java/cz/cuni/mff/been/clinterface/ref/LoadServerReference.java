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

import java.rmi.RemoteException;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.load.LoadServerInterface;

/**
 * Class encapsulating reference to the Load Server.
 * 
 * @author David Majda
 * @author Andrej Podzimek
 */
public class LoadServerReference extends RMIReference< LoadServerInterface > {
	
	/** A reference to the Host Manager. */
	private final ServiceReference< HostManagerInterface > hostManagerReference;

	@Override
	public LoadServerInterface acquire() throws ComponentInitializationException {
		try {
			return hostManagerReference.get().getLoadServer();
		} catch ( RemoteException exception ) {
			throw new ComponentInitializationException(
				"CLI service could not obtain a reference to the Load Server.",
				exception
			);
		}
	}

	/**
	 * Initializes a Load Server reference with the supplied Host Manager reference.
	 * 
	 * @param hostManagerReference A reference to the Host Manager.
	 */
	public LoadServerReference( ServiceReference< HostManagerInterface > hostManagerReference ) {
		this.hostManagerReference = hostManagerReference;
	}
}
