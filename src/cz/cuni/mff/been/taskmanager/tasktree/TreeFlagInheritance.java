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
 * The callback API provided to tree flags. Tree flag implementations can use this interface
 * when handling flag change request. Besides changing a parent node's flag value based on changes
 * in children's values, two methods of flag values access are available: a simple counter-based
 * method for simple evaluation and progress bars and a complex Iterable for the cases when
 * something more elaborate is needed.
 * 
 * @author Andrej Podzimek
 */
public interface TreeFlagInheritance {

	/**
	 * Inherited flag value setter.
	 * 
	 * @param value The ordinal part of the flag value.
	 * @param message The Object part of the flag value.
	 * @throws TreeFlagException When a weir error occurs, possibly due to a synchronization bug.
	 */
	void setFlag( Enum< ? > value, Serializable message ) throws TreeFlagException;

	/**
	 * Children counter getter.
	 * 
	 * @return The total number of children connected to the referenced node.
	 */
	int getTotalCounter();
	
	/**
	 * Value counter getter.
	 * 
	 * @param value The value to look up.
	 * @return Number of children of the referenced node which have the referenced flag set
	 * to the given value.
	 */
	int getValueCounter( Enum< ? > value );
	
	/**
	 * A detailed Iterable access getter.
	 * 
	 * @return An Iterable to the list of values children of the referenced node associate
	 * to the referenced flag, child by child.
	 */
	Iterable< TreeFlagValue > getChildrenFlags();
}
