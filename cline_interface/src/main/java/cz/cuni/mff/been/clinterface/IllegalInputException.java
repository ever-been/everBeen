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

/**
 * This exception is thrown when a module-independent input error occurs. This is always caused
 * by a malformed parameter string. TODO: Exception hierarchy in this package is an open question.
 * Modules should have the possibility to introduce their own error messages without modifying
 * the {@code Constants} class. For the time being, they can throw a {@code MODULE_SPECIFIC} error
 * code and report the error to stderr before doing that.
 * 
 * @author Andrej Podzimek
 */
public class IllegalInputException extends CommandLineException {

	private static final long serialVersionUID = 7068269441079472008L;

	/** The error code conveyed by this exception. */
	private final Constants.DataError error;
	
	/**
	 * Constructor from error code.
	 * 
	 * @param error The reason why this has been thrown.
	 */
	public IllegalInputException( Constants.DataError error ) {
		super( error.MSG );
		this.error = error;
	}
	
	/**
	 * Constructor from error code and an additional part (such as a list of modules or actions).
	 * 
	 * @param error The reason why this has been thrown.
	 * @param append A string to append.
	 */
	public IllegalInputException( Constants.DataError error, String append ) {
		super( error.MSG + '\n' + append.substring( 0, append.length() - 1 ) );
		this.error = error;
	}
	
	/**
	 * Constructor from error code and cause. Useful for debugging and detailed stack traces.
	 * 
	 * @param error The reason why this has been thrown.
	 * @param cause Exception that directly implies this one.
	 */
	public IllegalInputException( Constants.DataError error, Throwable cause ) {
		super( error.MSG, cause );
		this.error = error;
	}
	
	/**
	 * Error code getter.
	 * 
	 * @return The error code to send back.
	 */
	byte[] getError() {
		return error.ERR;
	}
}
