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
package cz.cuni.mff.been.idng;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import cz.cuni.mff.been.common.RMI;

/**
 * This class implements a unique identifier generator that can be used by new parts of the
 * BEEN framework.
 * 
 * @author Andrej Podzimek
 */
public class IDGeneratorImplementation
extends UnicastRemoteObject
implements Remote, IDGeneratorInterface {

	private static final long	serialVersionUID	= 3855906780343184004L;
	
	private static final String IDGEN_URL = "/been/idgen";

	/**
	 * The default mandatory constructor.
	 * 
	 * @throws RemoteException When something goes really wrong.
	 */
	protected IDGeneratorImplementation() throws RemoteException {
		try {
			Naming.rebind( RMI.URL_PREFIX + IDGEN_URL, this );
		} catch ( MalformedURLException exception ) {
			exception.printStackTrace();
			System.err.println( "This is a weird and fatal error." );
			System.exit( -1 );
		}
	}

	/**
	 * A counter singleton provided only for the sake of synchronization.
	 */
	private static final class Counter {
		
		/** Current state of the counter. */
		private long counter;
		
		
		/** Returns a new and unique counter value. */
		synchronized long increment() {
			return counter++;
		}
	}
	
	/** The singleton instance of a counter (and a synchronization primitive). */
	private static final Counter counter;
	
	static {
		counter = new Counter();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public < T extends UID > T getUniqueIdentifier( Class< T > clazz )
	throws IDInstantiationException {
		try {
			return clazz.getConstructor( Long.class ).newInstance( counter.increment() );
		} catch ( Exception exception ) {
			exception.printStackTrace();
			throw new IDInstantiationException( "Instantiation error.", exception );
		}
	}
}
