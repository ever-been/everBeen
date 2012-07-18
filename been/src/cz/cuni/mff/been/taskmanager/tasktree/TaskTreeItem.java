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
import java.util.Collection;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;

/**
 * The public API to access single nodes or leaves of the task tree.
 * 
 * @author Andrej Podzimek
 */
interface TaskTreeItem extends Serializable {
	
	/**
	 * A list of possible tree item types. Currently, only internal nodes and leaves are supported.
	 */
	public enum Type {
		
		/** Internal node of the tree. It may contain leaves and subnodes in arbitrary order. */
		NODE,
		
		/** Leaf of the tree. It represents one single task descriptor. */
		LEAF
	}
	
	/**
	 * Address getter.
	 * 
	 * @return A reference to the one and only instance of TaskTreeAddress representing
	 * this tree item's position in the tree.
	 */
	TaskTreeAddressBody getAddress();	

	/**
	 * Parent address getter.
	 * 
	 * @return A reference to the one and only instance of TaskTreeAddress pointing at this item's
	 * parent node. 
	 */
	TaskTreeAddressBody getParentAddress();

	/**
	 * Flags getter. Returns only flags whose values differ from the default value. Flags set
	 * to their default values may and may not be included in the output.
	 * 
	 * @return An Iterable pointing at a collection of (modified) flags.
	 */
	TaskTreeFlag[] getFlags();
	
	/**
	 * Flag value getter. 
	 * 
	 * @param flag The flag to inquire about.
	 * @return Value assigned to the flag. If not set, the default value is returned.
	 */
	TreeFlagValue getFlagValue( TaskTreeFlag flag );
	
	/**
	 * Flag values getter.
	 * 
	 * @return All the flags and their values in one array.
	 */
	Pair< TaskTreeFlag, TreeFlagValue >[] getFlagValues();
	
	/**
	 * Subnodes getter.
	 * 
	 * @return An Iterable through the array of child elements.
	 * @throws IllegalAddressException When called on a leaf.
	 */
	Collection< TaskTreeAddressBody > getChildren() throws IllegalAddressException;
	
	/**
	 * Task descriptor getter.
	 * 
	 * @return A task entry represented by the current leaf.
	 * @throws IllegalAddressException When called on an internal node.
	 */
	TaskEntry getTask() throws IllegalAddressException;
	
	/**
	 * Item type getter.
	 * 
	 * @return An item of the {@link Type} enum.
	 */
	Type getType();
}
