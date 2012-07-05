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

import java.rmi.Remote;
import java.rmi.RemoteException;

import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeItem.Type;

/**
 * A simple interface to the visual tree of tasks. It might be useful to expose the tree through
 * RMI. This is a read-only interface. {@link TaskTreeInput} provides write access.
 * 
 * @author Andrej Podzimek
 */
public interface TaskTreeQuery extends Remote {
	
	/**
	 * Query the path database of the tree. If the given path string is in the database,
	 * its corresponding address object will be returned. Else a new address representing the
	 * given path will be created and inserted into the database. It is <b>important</b> to always
	 * use this method to obtain an address. This is how you can make sure hash values are
	 * unique within the given task tree.
	 * 
	 * @param path A path in the task tree the new address should represent.
	 * @return An address, NOT NECESSARILY VALID, corresponding to the given path.
	 * @throws MalformedAddressException When the path string is malformed.
	 * @throws RemoteException
	 */
	TaskTreeAddress addressFromPath( String path )
	throws MalformedAddressException, RemoteException;
	
	/**
	 * Query the path database of the tree. If the given path string is in the database,
	 * its corresponding address object will be returned. Else a new address representing the
	 * given path will be created and inserted into the database. It is <b>important</b> to always
	 * use this method to obtain an address. This is how you can make sure hash values are
	 * unique within the given task tree.
	 * 
	 * @param path A path in the task tree the new address should represent.
	 * @return An address, NOT NECESSARILY VALID, corresponding to the given path.
	 * @throws MalformedAddressException When the path string is malformed.
	 * @throws RemoteException
	 */
	TaskTreeAddress addressFromPath( String ... path )
	throws MalformedAddressException, RemoteException;
	
	/**
	 * Tree element type getter.
	 * 
	 * @param address Address of the element to query. 
	 * @return Type of the referenced element, Type.NODE or Type.LEAF.
	 * @throws IllegalAddressException When no such address exists in the tree.
	 * @throws RemoteException
	 */
	Type getTypeAt( TaskTreeAddress address ) throws IllegalAddressException, RemoteException;
	
	/**
	 * Returns a reference to the task entry at the given address.
	 * 
	 * @param address The tree address of the requested item.
	 * @return A reference to the task entry at the given address.
	 * @throws IllegalAddressException When address does not exist or called on an internal node.
	 * @throws RemoteException
	 */
	TaskEntry getTaskAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException;
	
	/**
	 * Returns the path segments of the given address if an item exists on that address.
	 * 
	 * @param address The address to look up.
	 * @return An array of path segments.
	 * @throws IllegalAddressException When the address is invalid (which should never happen).
	 * @throws RemoteException
	 */
	String[] getPathAt( TaskTreeAddress address ) throws RemoteException;
	
	/**
	 * Returns a reference to the children of the node at the given address.
	 * 
	 * @param address The tree address of the reqeusted node.
	 * @return An array of children of the node (in the order they were added).
	 * @throws IllegalAddressException When address does not exist or called on a leaf.
	 * @throws RemoteException
	 */
	TaskTreeAddress[] getChildrenAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException;
	
	/**
	 * Flags getter. Returns only flags whose values differ from the default value. Flags set
	 * to their default values may and may not be included in the output.
	 * 
	 * @param address Address of the node to query.
	 * @return An Iterable pointing at a collection of (modified) flags.
	 * @throws IllegalAddressException When no such element exists.
	 * @throws RemoteException
	 */
	TaskTreeFlag[] getFlagsAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException;
	
	/**
	 * Flag value getter. 
	 * 
	 * @param address Address of the node to query.
	 * @param flag The flag to inquire about.
	 * @return Value assigned to the flag. If not set, the default value is returned.
	 * @throws IllegalAddressException When no such element exists.
	 * @throws RemoteException
	 */
	TreeFlagValue getFlagValueAt( TaskTreeAddress address, TaskTreeFlag flag )
	throws IllegalAddressException, RemoteException;
	
	/**
	 * Returns a tripplet containing tree element type and the corresponding data based
	 * on the type. (A task entry for LEAF elements and an array of child addresses for NODE
	 * elements.)
	 * 
	 * @param address Address of the tree element to query.
	 * @param path Whether the path string should be included.
	 * @param data Whether the element data (type, task, children) should be included.
	 * @param flags Whether the flags contained in the element should be included.
	 * @return A triplet containing the element's data.
	 * @throws IllegalAddressException When no such element exists.
	 * @throws RemoteException
	 */
	TaskTreeRecord getRecordAt( TaskTreeAddress address, boolean path, boolean data, boolean flags )
	throws IllegalAddressException, RemoteException;
}
