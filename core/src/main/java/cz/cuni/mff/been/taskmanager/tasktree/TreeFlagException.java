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
 * This exception is thrown when illegal flag manipulation is attempted. This may involve
 * setting duplicate flags or unsetting nonexistent ones.
 * 
 * @author Andrej Podzimek
 */
public class TreeFlagException extends TaskManagerException {

	private static final long serialVersionUID = 6381462453215333831L;

	/** String representation of the reported illegal flag. */
	private final String flagString;
	
	/**
	 * The standard constructor.
	 * 
	 * @param message Error message this exception will convey.
	 * @param flag The flag that did not match the current tree structure.
	 */
	public TreeFlagException( String message, TaskTreeFlag flag ) {
		super( message );
		this.flagString = flag.toString();
	}

	/**
	 * The extended constructor that conveys a stack of exceptions.
	 * 
	 * @param message Error message this exception will convey.
	 * @param flag The flag that did not match the current tree structure.
	 * @param cause Another exception that caused this one to be thrown.
	 */
	public TreeFlagException( String message, TaskTreeFlag flag, Throwable cause ) {
		super( message, cause );
		this.flagString = flag.toString();
	}
	
	/**
	 * Illegal flag getter.
	 * 
	 * @return The reported illegal flag.
	 */
	public String getFlag() {
		return flagString;
	}
}
