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

import cz.cuni.mff.been.common.BeenException;

/**
 * This exception is thrown when a new identifier cannot be instantiated for some reason.
 * 
 * @author Andrej Podzimek
 */
public final class IDInstantiationException extends BeenException {

	private static final long	serialVersionUID	= 1090427762378632801L;

	/**
	 * Creates a new exception and sets its message.
	 * 
	 * @param message The message this exception will convey.
	 */
	public IDInstantiationException( String message ) {
		super( message );
	}

	/**
	 * Creates a new exception, sets its message and cause.
	 * 
	 * @param message The message this exception will convey.
	 * @param cause The Throwable that caused this exception.
	 */
	public IDInstantiationException( String message, Throwable cause ) {
		super( message, cause );
	}	
}
