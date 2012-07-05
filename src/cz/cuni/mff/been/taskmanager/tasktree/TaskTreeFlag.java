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
 * This interface represents a tree flag data type. It provides a unique String key used as flag
 * identifier, a default value to return when the flag has not been set and a callback
 * aggregation method to handle flag inheritance in the task tree.
 * 
 * @author Andrej Podzimek
 */
public interface TaskTreeFlag extends Serializable {
	
	/**
	 * A String representation of the flag. This should be unique within one running instance
	 * of BEEN. This is the one and only unique flag identifier.
	 * 
	 * @return String identifier of the flag to be used as unique ID in mappings.
	 */
	String toString();

	/**
	 * Decides whether the supplied enum instance (and its ordinal value) is compatible with
	 * the current flag or not.
	 * 
	 * @param ordinal The ordinal value to check.
	 * @throws TreeFlagException When an incompatible ordinal (or Enum) value was supplied.
	 */
	void validate( Enum< ? > ordinal ) throws TreeFlagException;
	
	/**
	 * Default value getter.
	 * 
	 * @return The implicit value returned by flag getters before the flag has been set.
	 */
	TreeFlagValue getDefaultValue();
	
	/**
	 * Flag inheritance handler.
	 * 
	 * @param oldValue The original value of the flag.
	 * @param newValue The new value of the flag.
	 * @param inheritance Access to flag modification and iteration through children.
	 * @return Whether the change should be propagated further. (true -> propagate)
	 * @throws TreeFlagException When something weird or a bug occurs.
	 */
	boolean inherit(
		TreeFlagValue oldValue,
		TreeFlagValue newValue,
		TreeFlagInheritance inheritance
	) throws TreeFlagException;	
}
