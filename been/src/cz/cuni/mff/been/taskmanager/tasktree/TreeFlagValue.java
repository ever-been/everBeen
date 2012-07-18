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

import java.io.Serializable;

/**
 * Abstract ancestor of all possible flag values. This class can store an object of any type.
 * 
 * @author Andrej Podzimek
 */
public class TreeFlagValue implements Serializable {

	private static final long serialVersionUID = -6074524731110414413L;

	/** Numeric value of the flag. */
	private Enum< ? > ordinal;

	/** String-like value of the flag. */
	private Serializable message;

	/**
	 * Creates a new TreeFlagValue, which is in fact a Pair.
	 * 
	 * @param ordinal Numeric value of the flag.
	 * @param message String-like value of the flag.
	 */
	TreeFlagValue( Enum< ? > ordinal, Serializable message ) {
		this.ordinal = ordinal;
		this.message = message;
	}
	
	/**
	 * Creates a numeric-only flag with string-like value set to null.
	 * 
	 * @param ordinal Numeric value of the flag.
	 */
	TreeFlagValue( Enum< ? > ordinal ) {
		this( ordinal, null );
	}

	/**
	 * String-like value getter.
	 * 
	 * @return String-like value of the flag.
	 */
	public Serializable getMessage() {
		return message;
	}
	
	/**
	 * Numeric value getter.
	 * 
	 * @return Numeric value of the flag.
	 */
	public Enum< ? > getOrdinal() {
		return ordinal;
	}
}
