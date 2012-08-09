/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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

package cz.cuni.mff.been.debugassistant;

//import cz.cuni.mff.been.common.RMISafeException;

/**
 * All the bad things that could happen in the debug assistant.
 * 
 * @author Jan Tattermusch
 *
 * /



 /*
 * TODO: due to cyclic dependency between core and da-interface the class
 * hiearchy has been changed, this is WRONG! After mavenenization is completead
 * this change should be revereted!
 */
public class DebugAssistantException extends Exception /*extends RMISafeException*/ {

	private static final long	serialVersionUID	= -8835602578525791295L;

	/**
	 * The standard constructor.
	 * 
	 * @param message Error message this exception will convey.
	 */
	public DebugAssistantException( String message ) {
		super( message );
	}

	/**
	 * The extended constructor that conveys a stack of exceptions.
	 * 
	 * @param message Error message this exception will convey.
	 * @param cause Another exception that caused this one to be thrown.
	 */
	public DebugAssistantException( String message, Throwable cause ) {
		super( message, cause );
	}

	public DebugAssistantException( Throwable cause ){
		super( cause );
	}
}
