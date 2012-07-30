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
import java.rmi.Remote;
import java.rmi.RemoteException;

import cz.cuni.mff.been.taskmanager.data.TaskEntry;

/**
 * This interface exposes the input to the visual tree of tasks. It is useful when tasks are
 * generated and registered through RMI. {@link TaskTreeQuery} provides the read-only part.
 * 
 * @author Andrej Podzimek
 */
public interface TaskTreeInput extends Remote {
	
	/**
	 * Creates an internal node, including the whole path when necessary. This method is only useful
	 * when either empty internal nodes need to be created or a special order of nodes is desired,
	 * when the tree is generated using BFS, for example...
	 * 
	 * @param address Address of the new internal node.
	 * @throws IllegalAddressException If either a node or a leaf with this address already exists.
	 * @throws RemoteException
	 */
	void addNode( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException;

	/**
	 * Creates a leaf, including the whole path when necessary.
	 * 
	 * @param address Address of the new leaf.
	 * @param entry The task entry the leaf will store.
	 * @throws IllegalAddressException If either a node or a leaf with this address already exists.
	 * @throws RemoteException
	 */
	void addLeaf( TaskTreeAddress address, TaskEntry entry )
	throws IllegalAddressException, RemoteException;
	
	/**
	 * Deletes the given subtree of the task tree. The path string cache remains untouched.
	 * 
	 * @param address Address of the subtree (o leaf node) to delete.
	 * @throws IllegalAddressException When the address does not exist.
	 * @throws RemoteException
	 */
	void clearInclusive( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException;
	
	/**
	 * Deletes all subtrees (or leaves) of the given subtree. The root node will remain.
	 * 
	 * @param address Address of the root node whose subtrees will be deleted.
	 * @throws IllegalAddressException When the address does not exist or represents a leaf.
	 * @throws RemoteException
	 */
	void clearSubtrees( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException;
	
	/**
	 * Sets a tree flag to the given value.
	 * 
	 * @param address Address of the tree leaf whose flag should be set.
	 * @param flag Type of the flag to set.
	 * @param ordinal The numeric value of the flag.
	 * @throws TreeFlagException On duplicate flags.
	 * @throws IllegalAddressException When a node or a non-existent address is referenced.
	 * @throws RemoteException
	 */
	void setFlag( TaskTreeAddress address, TaskTreeFlag flag, Enum< ? > ordinal )
	throws TreeFlagException, IllegalAddressException, RemoteException;
	
	/**
	 * Sets a tree flag to the given value.
	 * 
	 * @param address Address of the tree leaf whose flag should be set.
	 * @param flag Type of the flag to set.
	 * @param ordinal The numeric value of the flag.
	 * @param message An optional String value of the flag. The toString() method will be used.
	 * @throws TreeFlagException On duplicate flags.
	 * @throws IllegalAddressException When a node or a non-existent address is referenced.
	 * @throws RemoteException
	 */
	void setFlag(
		TaskTreeAddress address,
		TaskTreeFlag flag,
		Enum< ? > ordinal,
		Serializable message
	) throws TreeFlagException, IllegalAddressException, RemoteException;
	
	/**
	 * Removes all the tree nodes. Does the same as {@code clearInclusive()} on the root node,
	 * but is somewhat more efficient.
	 * 
	 * @throws RemoteException
	 */
	void clear() throws RemoteException;
}
