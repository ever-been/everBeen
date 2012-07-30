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
package cz.cuni.mff.been.taskmanager.tasktree;

import cz.cuni.mff.been.taskmanager.TaskManagerException;

/**
 * This exception is thrown when either a non-existent node is requested or insertion
 * of children into a leaf node is attempted.
 * 
 * @author Andrej Podzimek
 */
public class IllegalAddressException extends TaskManagerException {

	private static final long serialVersionUID = -4777449158519130067L;

	/**
	 * The standard constructor.
	 * 
	 * @param message Error message this exception will convey.
	 * @param address The address that did not match the current tree structure.
	 */
	public IllegalAddressException( String message, TaskTreeAddressBody address ) {
		super( message + " (" + address.getPathString() + ')' );
	}

	/**
	 * The extended constructor that conveys a stack of exceptions.
	 * 
	 * @param message Error message this exception will convey.
	 * @param address The address that did not match the current tree structure.
	 * @param cause Another exception that caused this one to be thrown.
	 */
	public IllegalAddressException( String message, TaskTreeAddressBody address, Throwable cause ) {
		super( message + " (" + address.getPathString() + ')', cause );
	}		
}
