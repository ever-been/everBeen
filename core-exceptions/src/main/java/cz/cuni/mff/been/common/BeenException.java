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
package cz.cuni.mff.been.common;

/**
 * This class is a base class for all user exceptions thrown in the whole BEEN project.
 * 
 * @author David Majda
 */
public class BeenException extends Exception {
	
	private static final long	serialVersionUID	= 6684491140909902247L;

	/**
	 * Allocates a new <code>BeenException</code> object.
	 */
	public BeenException() {
		super();
	}
	
	/**
	 * Allocates a new <code>BeenException</code> object
	 * with specified message.
	 * 
	 * @param message exception message
	 */
	public BeenException(String message) {
		super(message);
	}
	
	/**
	 * Allocates a new <code>BeenException</code> object
	 * with specified cause.
	 *
	 * @param cause exception cause
	 */
	public BeenException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Allocates a new <code>BeenException</code> object
	 * with specified message and cause.
	 *
	 * @param message exception message
	 * @param cause exception cause
	 */
	public BeenException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
