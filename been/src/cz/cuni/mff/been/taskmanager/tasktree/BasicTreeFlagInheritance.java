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
 * An example implementation of tree flag inheritance. This class implements the interface
 * tree flags can use to propagate their values up the task tree. This class hides the tree
 * internals from the callers so that tree flags can be implemented in pluggable modules
 * and other untrusted code.
 * 
 * @author Andrej Podzimek
 */
final class BasicTreeFlagInheritance implements TreeFlagInheritance {
	
	/** The node this inheritance callback refers to. */
	private final TaskTreeNode node;

	/** The flag this inheritance callback can change. */
	private final TaskTreeFlag flag;
	
	/** Previous value of the flag that has changed in one of the children. */
	private final TreeFlagValue oldValue;
	
	/** New value of the flag that has changed in one of the children. */
	private TreeFlagValue newValue;

	/**
	 * The default constructor that binds the instance with a node and a flag.
	 * 
	 * @param node The task tree node this callback will influence.
	 * @param flag The flag this callback can change.
	 */
	public BasicTreeFlagInheritance( TaskTreeNode node, TaskTreeFlag flag ) {
		this.node = node;
		this.flag = flag;
		this.oldValue = this.newValue = node.getFlagValue( flag );
	}
	
	@Override
	public void setFlag( Enum< ? > value, Serializable message ) throws TreeFlagException {
		node.decrementCounter( flag, newValue.getOrdinal() );										// YUP, *really* newValue!
		newValue = new TreeFlagValue( value, message );
		node.setFlag( flag, newValue );
		node.incrementCounter( flag, newValue.getOrdinal() );
	}

	@Override
	public int getTotalCounter() {
		return node.getTotalCounter();
	}

	@Override
	public int getValueCounter( Enum< ? > value ) {
		return node.getValueCounter( flag, value );
	}

	@Override
	public Iterable< TreeFlagValue > getChildrenFlags() {
		return node.getValueIterable( flag );
	}

	/**
	 * Old value getter.
	 * 
	 * @return The previous (first) value associated with the (node, flag) pair.
	 */
	TreeFlagValue getOldValue() {
		return oldValue;
	}
	
	/**
	 * New value getter.
	 * 
	 * @return The new (last) value associated with the (node, flag) pair.
	 */
	TreeFlagValue getNewValue() {
		return newValue;
	}
}
