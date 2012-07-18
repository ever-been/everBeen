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

package cz.cuni.mff.been.hostmanager.load;

import cz.cuni.mff.been.hostmanager.HostManagerException;

/**
 * Basic exception class for all exceptions specific to the Load Monitor.
 *  
 * @author Branislav Repcek
 *
 */
public class LoadMonitorException extends HostManagerException {

	private static final long	serialVersionUID	= 6312872620914425572L;

	/**
	 * Create new LoadMonitorException object.
	 *
	 */
	public LoadMonitorException() {
		
		super();
	}
	
	/**
	 * Create new LoadMonitorException object with given cause.
	 * 
	 * @param cause Cause of exception.
	 */
	public LoadMonitorException(Throwable cause) {
	
		super(cause);
	}
	
	/**
	 * Create new LoadMonitorException object with given message.
	 * 
	 * @param message Exception message.
	 */
	public LoadMonitorException(String message) {
	
		super(message);
	}

	/**
	 * Create new LoadMonitorException with given message and cause.
	 * 
	 * @param message Exception message.
	 * @param cause Exception cause.
	 */
	public LoadMonitorException(String message, Throwable cause) {
		
		super(message, cause);
	}
}
