/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager;

/**
 * 
 * This exception is thrown when argument passed to the method is invalid.
 *
 * @author Branislav Repcek
 * 
 */
public class InvalidArgumentException extends RuntimeException {
	
	private static final long	serialVersionUID	= 6530739867466004659L;

	/**
	 * Create new <code>InvalidArgumentException</code> object.
	 */
	public InvalidArgumentException() {
		
		super();
	}
	
	/**
	 * Create new <code>InvalidArgumentException</code> object with given cause.
	 * 
	 * @param cause Cause of exception.
	 */
	public InvalidArgumentException(Throwable cause) {
	
		super(cause);
	}
	
	/**
	 * Create new <code>InvalidArgumentException</code> with given message.
	 * 
	 * @param message Exception message.
	 */
	public InvalidArgumentException(String message) {
		
		super(message);
	}
	
	/**
	 * Create new <code>InvalidArgumentException</code> object with given message and cause.
	 *  
	 * @param message Exception message.
	 * @param cause Cause of exception.
	 */
	public InvalidArgumentException(String message, Throwable cause) {
		
		super(message, cause);
	}
}
