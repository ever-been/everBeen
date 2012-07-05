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
package cz.cuni.mff.been.hostruntime;

import cz.cuni.mff.been.common.BeenException;

/**
 * Base class for all user exceptions thrown in Host Runtime package.
 * 
 * @author Antonin Tomecek
 * @author David Majda
 */
public class HostRuntimeException extends BeenException {

	private static final long	serialVersionUID	= 8034895172162133850L;

	/**
	 * Allocates a new <code>HostRuntimeException</code> object.
	 */
	public HostRuntimeException() {
		super();
	}
	
	/**
	 * Allocates a new <code>HostRuntimeException</code> object
	 * with specified message.
	 * 
	 * @param message exception message
	 */
	public HostRuntimeException(String message) {
		super(message);
	}
	
	/**
	 * Allocates a new <code>HostRuntimeException</code> object
	 * with specified cause.
	 *
	 * @param cause exception cause
	 */
	public HostRuntimeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Allocates a new <code>HostRuntimeException</code> object
	 * with specified message and cause.
	 *
	 * @param message exception message
	 * @param cause exception cause
	 */
	public HostRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
