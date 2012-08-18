/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager.data;

import java.util.TreeMap;

/**
 * Enum representing state of task in Task Manager.
 * 
 * @author Antonin Tomecek
 */
public enum TaskState {
	/**
	 * Task was submitted and not scheduled yet.
	 */
	SUBMITTED( "submitted" ),

	/**
	 * Task was scheduled and not started yet.
	 */
	SCHEDULED( "scheduled" ),

	/**
	 * Task was started and not finished yet and currently is running.
	 */
	RUNNING( "running" ),

	/**
	 * Task was started and not finished yet and currently is waiting (for
	 * something).
	 */
	SLEEPING( "sleeping" ),

	/**
	 * Task was finished and not removed from TaskManager yet.
	 */
	FINISHED( "finished" ),

	/**
	 * Task was aborted and not removed from TaskManager yet.
	 */
	ABORTED( "aborted" );

	private static final TreeMap< String, TaskState > reverseMap;

	static {
		reverseMap = new TreeMap< String, TaskState >();

		for ( TaskState taskState : TaskState.values() ) {
			reverseMap.put( taskState.toString(), taskState );
		}
	}

	private	final String name;

	private TaskState( String name ) {
		this.name = name;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return A string representation of the object.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns enum value represented by <code>String</code>.
	 * 
	 * @param string String representation of enum value.
	 * @return Enum value represented by <code>string</code>.
	 * @throws NullPointerException If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException If value for specified
	 * 	<code>string</code> not found.
	 */
	public static TaskState fromString(String string) {
		TaskState	result;

		/* Check input parameters. */
		if (string == null) {
			throw new NullPointerException("string is null");
		}

		/* Do search. */
		result = reverseMap.get( string );

		if ( result == null ) {
			/* Throw exception if not found. */
			throw new IllegalArgumentException("No enum value has its string "
				+ "representation equal to \"" + string + "\"");
		} else {
			return result;
		}
	}
}
