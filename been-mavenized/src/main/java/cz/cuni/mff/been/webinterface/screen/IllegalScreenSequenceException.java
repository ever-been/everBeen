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
package cz.cuni.mff.been.webinterface.screen;

import cz.cuni.mff.been.common.BeenException;


/**
 * Thrown form the <code>BenchmarkManagerInterface.getNextScreen</code> and
 * <code>BenchmarkManagerInterface.getPreviousScreen</code>, if the web
 * interface request screen out of the allowed sequence.
 * 
 * The Benchmark Manager's contract specifies following behaviour:
 * 
 * <ul>
 *   <li><em>Visited screen list</em> is a list of screens. All screens in this
 *   list were displayed to the user at some time, but not all screens
 *   displayed to the user are necessarily in the list.</li>
 *  
 *   <li>When <code>getFirst</code> is called, visited screen list is cleared
 *   and the returned screen is put into the list.</li>
 *   
 *   <li>When <code>getNextScreen</code> is called, visited screen list is
 *   searched for the screen passed as the parameter to the
 *   <code>getNextScreen</code>. If this screen is found, list is truncated
 *   there (all following screens are deleted) and returned screen (if
 *   non-null) is appended to the list. If the screen is not found,
 *   <code>IllegalScreenSequenceException</code> is thrown.</li>   
 *     
 *   <li>When <code>getPrevScreen</code> is called, visited screen list is
 *   searched for the screen passed as the parameter to the
 *   <code>getPrevScreen</code>. If this screen is found, list is truncated
 *   there (all following screens, including the found one, are deleted). If
 *   the screen is not found, <code>IllegalScreenSequenceException</code> is
 *   thrown.</li>
 * </ul>   
 * 
 * @author David Majda
 */
public class IllegalScreenSequenceException extends BeenException {
    
	private static final long	serialVersionUID	= 3508571974737275258L;

	/**
	 * Allocates a new <code>IllegalScreenSequenceException</code> object.
	 */
	public IllegalScreenSequenceException() {
		super();
	}
    
	/**
	 * Allocates a new <code>IllegalScreenSequenceException</code> object
	 * with specified message.
	 * 
	 * @param message exception message
	 */
	public IllegalScreenSequenceException(String message) {
		super(message);
	}
    
	/**
	 * Allocates a new <code>IllegalScreenSequenceException</code> object
	 * with specified cause.
	 * 
	 * @param cause exception cause
	 */
	public IllegalScreenSequenceException(Throwable cause) {
		super(cause);
	}
    
	/**
	 * Allocates a new <code>IllegalScreenSequenceException</code> object
	 * with specified message and cause.
	 * 
	 * @param message exception message
	 * @param cause exception cause
	 */
	public IllegalScreenSequenceException(String message, Throwable cause) {
		super(message, cause);
	}
}
