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

import cz.cuni.mff.been.common.BeenException;

/**
 * This is the worst thing that can happen in the command line service. This usually involves
 * a module instantiation error or other unrecoverable errors. Exceptions of this class
 * are usually not worth reporting to the command line client's back channel, as there is
 * no valuable information for its user. Additionally, the network connection may be already
 * down when this brick is thrown.
 * 
 * @author Andrej Podzimek
 */
public class CommandLineException extends BeenException {

	private static final long	serialVersionUID	= 4866966587691884028L;

	/**
	 * A standard constructor. This is only good for subclassing, please avoid using it directly.
	 */
	public CommandLineException() {
	}

	/**
	 * This constructor conveys a message to whoever may catch this. The message will not be
	 * sent to the command line client.
	 * 
	 * @param message A message to be logged or written to the service's error output.
	 */
	public CommandLineException( String message ) {
		super( message );
	}

	/**
	 * A standard constructor. This is only good for subclassing, please avoid using it directly.
	 * 
	 * @param cause Cause of this exception for stack trace reports.
	 */
	public CommandLineException( Throwable cause ) {
		super( cause );
	}

	/**
	 * This constructor conveys a message to whoever may catch this. The message will not be
	 * sent to the command line client.
	 * 
	 * @param message A message to be logged or written to the service's error output.
	 * @param cause Cause of this exception for stack trace reports.
	 */
	public CommandLineException( String message, Throwable cause ) {
		super( message, cause );
	}
}
