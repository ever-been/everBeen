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
package cz.cuni.mff.been.common;

import java.rmi.registry.Registry;

/**
 * This enum lists some important constants related to RMI. There are global static constants and
 * service-specific constants defined inside enum members.
 * 
 * @author Andrej Podzimek
 */
public enum RMI {
	;
	
	/** The port RMI registry should use. */
	public static final int REGISTRY_PORT;
	
	/** The RMI URL prefix used by services; */
	public static final String URL_PREFIX;
	
	static {
		int registryPort;
		
		registryPort = Registry.REGISTRY_PORT;
		try {
			String registryPortString;
			
			registryPortString = System.getenv( "BEEN_REGISTRY_PORT" );
			if ( null != registryPortString ) {
				registryPort = Integer.parseInt( registryPortString );
			}
		} catch ( NumberFormatException exception ) {
			System.err.println( "Malformed BEEN_REGISTRY_PORT variable. Using default value." );
		} catch ( SecurityException exception ) {
			System.err.println( "Cannot read BEEN_REGISTRY_PORT variable. Using default value." );
		}
		REGISTRY_PORT = registryPort;
		URL_PREFIX = "rmi://localhost:" + REGISTRY_PORT;
		System.out.println( "Using RMI registry port " + REGISTRY_PORT + "." );
	}
}
