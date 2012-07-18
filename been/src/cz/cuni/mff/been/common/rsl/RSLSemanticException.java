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
package cz.cuni.mff.been.common.rsl;

/**
 * Thrown by the RSL interpreter if some semantic error is detected in the RSL
 * expression.
 * 
 * @author David Majda
 */
public class RSLSemanticException extends Exception {

	private static final long	serialVersionUID	= 4760210617731093073L;

	/**
	 * Allocates a new <code>RSLSemanticException</code> object.
	 */
	public RSLSemanticException() {
		super();
	}
	
	/**
	 * Allocates a new <code>RSLSemanticException</code> object
	 * with specified message.
	 * 
	 * @param message exception message
	 */
	public RSLSemanticException(String message) {
		super(message);
	}
	
	/**
	 * Allocates a new <code>RSLSemanticException</code> object
	 * with specified cause.
	 *
	 * @param cause exception cause
	 */
	public RSLSemanticException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Allocates a new <code>RSLSemanticException</code> object
	 * with specified message and cause.
	 *
	 * @param message exception message
	 * @param cause exception cause
	 */
	public RSLSemanticException(String message, Throwable cause) {
		super(message, cause);
	}
}
