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
package cz.cuni.mff.been.clinterface;

import cz.cuni.mff.been.common.Message;

/**
 * This exception circumvents the open question mentioned in IllegalInputException.
 * Methods that don't have direct access to a CommandLineResponse can produce error messages
 * using this exception.
 * 
 * @author Andrej Podzimek
 */
public class ModuleSpecificException extends CommandLineException {

	private static final long serialVersionUID = -5629710198507872195L;
	
	/** The error code conveyed by this exception. */
	private final Message error;
	
	/**
	 * A message-only constructor.
	 * 
	 * @param error The error message to report.
	 */
	public ModuleSpecificException( Message error ) {
		super( error.getMessage() );
		this.error = error;
	}
	
	/**
	 * A message-only constructor with an additional String.
	 * 
	 * @param error The error message to report.
	 * @param additional Additional information (illegal string, variable name and the like).
	 */
	public ModuleSpecificException( Message error, String additional ) {
		super( error.getMessage() + additional );
		this.error = error;
	}

	/**
	 * A message-only constructor with an additional StringBuilder.
	 * 
	 * @param error The error message to report. Must be non-empty. First character is discarded.
	 * @param additional Additional information (illegal string, variable name and the like).
	 */
	public ModuleSpecificException( Message error, StringBuilder additional ) {
		super( error.getMessage() + additional.substring( 1 ) );
		this.error = error;
	}
	
	/**
	 * A chaining constructor.
	 * 
	 * @param error The error message to report.
	 * @param cause The exception that caused this one.
	 */
	public ModuleSpecificException( Message error, Throwable cause ) {
		super( error.getMessage(), cause );
		this.error = error;
	}
	
	/**
	 * A chaining constructor with an aditional String.
	 * 
	 * @param error The error message to report.
	 * @param additional Additional information (illegal string, variable name and the like).
	 * @param cause The exception that caused this one.
	 */
	public ModuleSpecificException( Message error, String additional, Throwable cause ) {
		super( error.getMessage() + additional, cause );
		this.error = error;
	}
	
	/**
	 * A chaining constructor with an aditional StringBuilder.
	 * 
	 * @param error The error message to report. Must be non-empty. Fist character is discarded.
	 * @param additional Additional information (illegal string, variable name and the like).
	 * @param cause The exception that caused this one.
	 */
	public ModuleSpecificException( Message error, StringBuilder additional, Throwable cause ) {
		super( error.getMessage() + additional.substring( 1 ), cause );
		this.error = error;
	}
	
	/**
	 * Error message getter.
	 * 
	 * @return The reason why this has been thrown.
	 */
	Message getError() {
		return error;
	}
}
