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
 * This exception is thrown when a malformed address is provided. A path string (or an array
 * of path segments) with empty segment(s) are simple examples of malformed addresses.
 * 
 * @author Andrej Podzimek
 */
public class MalformedAddressException extends TaskManagerException {

	private static final long serialVersionUID = -2550958103642362392L;

	public MalformedAddressException( String message ) {
		super( message );
	}

	public MalformedAddressException( String message, Throwable cause ) {
		super( message, cause );
	}
}
