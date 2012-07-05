/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.softwarerepository;

/**
 * Thrown when some error occurs when matching packages in the
 * <code>PackageQueryCallbackInterface</code>.
 * 
 * @author David Majda
 */
public class MatchException extends SoftwareRepositoryException {
	
	private static final long	serialVersionUID	= 5281190954960599035L;

	/**
	 * Allocates a new <code>MatchException</code> object.
	 */
	public MatchException() {
		super();
	}
	
	/**
	 * Allocates a new <code>MatchException</code> object
	 * with specified message.
	 * 
	 * @param message exception message
	 */
	public MatchException(String message) {
		super(message);
	}
	
	/**
	 * Allocates a new <code>MatchException</code> object
	 * with specified cause.
	 *
	 * @param cause exception cause
	 */
	public MatchException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Allocates a new <code>MatchException</code> object
	 * with specified message and cause.
	 *
	 * @param message exception message
	 * @param cause exception cause
	 */
	public MatchException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
