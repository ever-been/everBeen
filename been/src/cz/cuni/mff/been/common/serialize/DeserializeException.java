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
package cz.cuni.mff.been.common.serialize;

import cz.cuni.mff.been.common.BeenException;

/**
 * This is thrown when object deserialization from XML (or other binding) fails.
 * 
 * @author Andrej Podzimek
 */
public class DeserializeException extends BeenException {

	private static final long	serialVersionUID	= 7529697713171243167L;
	
	/** The error code conveyed by this exception. */
	private final Errors error;
	
	/**
	 * A chaining constructor.
	 * 
	 * @param error The error message to report.
	 * @param cause The exception that caused this one.
	 */
	public DeserializeException( Errors error, Throwable cause ) {
		super( error.toString(), cause );
		this.error = error;
	}

	/**
	 * Error message getter.
	 * 
	 * @param <T> Type of the enum in which the message will be looked up.
	 * @param type An enum class of type {@code T}.
	 * @return The error message from the requested enum type.
	 */
	public < T extends Enum< T > > T getError( Class< T > type ) {
		return error.error( type );
	}
}
