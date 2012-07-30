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
 * Class which represents exception thrown when object with specified property (index, name...)
 * was not found. It's replacement for ArrayIndexOutOfBounds and similar exceptions to hide implementation
 * details.
 *
 * @author Branislav Repcek
 *
 */
public class ValueNotFoundException extends RuntimeException {
	
	private static final long	serialVersionUID	= 2878557231997852830L;

	/**
	 * Create new ValueNotFoundException object.
	 */
	public ValueNotFoundException() {
		
		super();
	}
	
	/**
	 * Create new ValueNotFoundException object with given cause.
	 * 
	 * @param cause Cause of exception.
	 */
	public ValueNotFoundException(Throwable cause) {
	
		super(cause);
	}
	
	/**
	 * Create new ValueNotFoundException with given message.
	 * 
	 * @param message Exception message.
	 */
	public ValueNotFoundException(String message) {
		
		super(message);
	}
	
	/**
	 * Create new ValueNotFoundException object with given message and cause.
	 *  
	 * @param message Exception message.
	 * @param cause Cause of exception.
	 */
	public ValueNotFoundException(String message, Throwable cause) {
		
		super(message, cause);
	}
}
