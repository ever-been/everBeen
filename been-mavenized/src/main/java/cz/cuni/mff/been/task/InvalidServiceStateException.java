/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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


/**
 * Service is in a state that doesn't allow some requested operation.
 * 
 * @author Jaroslav Urban
 */
public class InvalidServiceStateException extends TaskException {

	private static final long	serialVersionUID	= 8153533175124550361L;

	/**
	 * Allocates a new <code>InvalidServiceStateException</code> object.
	 */
	public InvalidServiceStateException() {
		super();
	}

	/**
	 * Allocates a new <code>InvalidServiceStateException</code> object
	 * with specified message.
	 * 
	 * @param message exception message
	 */
	public InvalidServiceStateException(String message) {
		super(message);
	}

	/**
	 * Allocates a new <code>InvalidServiceStateException</code> object
	 * with specified cause.
	 *
	 * @param cause exception cause
	 */
	public InvalidServiceStateException(Throwable cause) {
		super(cause);
	}

	/**
	 * Allocates a new <code>InvalidServiceStateException</code> object
	 * with specified message and cause.
	 *
	 * @param message exception message
	 * @param cause exception cause
	 */
	public InvalidServiceStateException(String message, Throwable cause) {
		super(message, cause);
	}
}
