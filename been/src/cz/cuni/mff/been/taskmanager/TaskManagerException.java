/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager;

import cz.cuni.mff.been.common.BeenException;

/**
 * Thrown for all exceptions in package taskmanager.
 * 
 * @author Antonin Tomecek
 */
public class TaskManagerException extends BeenException {

	private static final long	serialVersionUID	= 8168021470381261883L;

	/**
	 * Constructs a new TaskManagerException with null as its detail
	 * message.
	 */
	public TaskManagerException() {
		super();
	}
	
	/**
	 * Constructs a new TaskManagerException with the specified detail
	 * message.
	 * 
	 * @param message The detail message.
	 */
	public TaskManagerException(String message) {
		super(message);
	}
	
	/**
	 * Constructs a new TaskManagerException with the specified detail
	 * message and cause.
	 * 
	 * @param message The detail message.
	 * @param cause The cause.
	 */
	public TaskManagerException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new TaskManagerException with the specified cause.
	 * 
	 * @param cause The cause.
	 */
	public TaskManagerException(Throwable cause) {
		super(cause);
	}
}
