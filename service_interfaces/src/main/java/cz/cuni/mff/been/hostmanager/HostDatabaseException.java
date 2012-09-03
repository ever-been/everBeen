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
 * This exception is thrown if there was unrecoverable error during database operation (eg. error
 * writing files to the disk).
 *
 * @author Branislav Repcek
 * 
 */
public class HostDatabaseException extends HostManagerException {
	
	private static final long	serialVersionUID	= 7624775010058816887L;

	/**
	 * Create new <code>HostDatabaseException</code> object.
	 */
	public HostDatabaseException() {
		
		super();
	}
	
	/**
	 * Create new <code>HostDatabaseException</code> object with given cause.
	 * 
	 * @param cause Cause of exception.
	 */
	public HostDatabaseException(Throwable cause) {
	
		super(cause);
	}
	
	/**
	 * Create new <code>HostDatabaseException</code> with given message.
	 * 
	 * @param message Exception message.
	 */
	public HostDatabaseException(String message) {
		
		super(message);
	}
	
	/**
	 * Create new <code>HostDatabaseException</code> object with given message and cause.
	 *  
	 * @param message Exception message.
	 * @param cause Cause of exception.
	 */
	public HostDatabaseException(String message, Throwable cause) {
		
		super(message, cause);
	}
}
