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
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map.Entry;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;

/**
 * This is a common ancestor for tree items, both nodes and leaves.
 * 
 * @author Andrej Podzimek
 */
abstract class TaskTreeBasic implements TaskTreeItem, Serializable {

	private static final long serialVersionUID = 9078557759211125841L;
	
	/** Flags implemented inside an enum can't reimplement Comparable. This solves the problem. */
	private static final Comparator< TaskTreeFlag > FLAG_COMPARATOR =
		new Comparator< TaskTreeFlag >() {
			@Override
			public int compare( TaskTreeFlag o1, TaskTreeFlag o2 ) {
				return o1.toString().compareTo( o2.toString() );
			}
		};
	
	/** The lock status of this node. */
	private boolean lock;

	/** The task tree this node or leaf belongs to. */
	protected final TaskTree tree;
	
	/** Address of this tree item. */
	private final TaskTreeAddressBody address;

	/** Address of the parent tree item. */
	private final TaskTreeAddressBody parentAddress;

	/** Mapping tree flag names to their values and helper classes. */
	private final TreeMap< TaskTreeFlag, TreeFlagValue > flags;
	
	/** Cache of the last array of flags. */
	private TaskTreeFlag[] flagsArray;
	
	/** Cache of the last array of flag values. */
	private Pair< TaskTreeFlag, TreeFlagValue >[] flagValuesArray;

	/**
	 * Creates a new tree item initialized with two important addresses.
	 * 
	 * @param address Address of the current item.
	 * @param parentAddress Address of the parent tree node.
	 */
	TaskTreeBasic(
		TaskTree tree,
		TaskTreeAddressBody address,
		TaskTreeAddressBody parentAddress
	) {
		this.tree = tree;
		this.address = address;
		this.parentAddress = parentAddress;
		this.flags = new TreeMap< TaskTreeFlag, TreeFlagValue >( FLAG_COMPARATOR );
	}

	/**
	 * Address getter.
	 * 
	 * @return Address of the current tree node in the internal representation.
	 */
	public final TaskTreeAddressBody getAddress() {
		return address;
	}

	/**
	 * Parent address getter.
	 * 
	 * @return Address of the current node's parent, null if this is root.
	 */
	public final TaskTreeAddressBody getParentAddress() {
		return parentAddress;
	}

	@Override
	public Collection< TaskTreeAddressBody > getChildren() throws IllegalAddressException {
		throw new IllegalAddressException(
			"Subnodes of a leaf requested.",
			address
		);
	}
	
	@Override
	public TaskEntry getTask() throws IllegalAddressException {
		throw new IllegalAddressException(
			"Task descriptor of an internal node requested.",
			address
		);
	}
	
	/**
	 * Flags getter.
	 * 
	 * @return An Iterable through the collection of flags.
	 */
	public final TaskTreeFlag[] getFlags() {
		TaskTreeFlag[] result;
		
		result = flagsArray;																		// Assignment is atomic.
		if ( null == result ) {
			acquireLock();																			// CAUTION! Flags need this, ...
			result = flags.values().toArray( new TaskTreeFlag[ flags.size() ] );					// OK, read-only access.
			releaseLock();																			// ... unlike structure manip.
			flagsArray = result;																	// OK, atomic.
		}
		return result;
	}

	/**
	 * Flag value getter.
	 * 
	 * @param flag The flag for which the ordinal value should be found.
	 * @return The flag value pair associated with the requested flag.
	 */
	public final TreeFlagValue getFlagValue( TaskTreeFlag flag ) {
		TreeFlagValue entry;
		
		acquireLock();																				// The map must be protected.
		entry = flags.get( flag );
		releaseLock();
		return null == entry ? flag.getDefaultValue() : entry;
	}

	
	@SuppressWarnings( "unchecked" )
	@Override
	public final Pair< TaskTreeFlag, TreeFlagValue >[] getFlagValues() {
		Pair< TaskTreeFlag, TreeFlagValue >[] result;

		result = flagValuesArray;																	// Assignment is atomic.
		if ( null == result ) {
			int i;
			acquireLock();
			result = (Pair< TaskTreeFlag, TreeFlagValue >[])
			Array.newInstance( Pair.class, flags.size() );
			i = 0;
			for ( Entry< TaskTreeFlag, TreeFlagValue > flag : flags.entrySet() ) {					// OK, read-only access.
				result[ i++ ] = new Pair< TaskTreeFlag, TreeFlagValue >(
					flag.getKey(),
					flag.getValue()
				);
			}
			releaseLock();
			flagValuesArray = result;																// Fine, atomic again.
		}
		return result;
	}
	
	/**
	 * Sets a tree flag to the given value. Locks must be handled by the caller here, since they
	 * need to take the tree hierarchy into account!
	 * 
	 * @param flag Type of the flag to set.
	 * @param value An enum representing the flag's value.
	 * @return The previous value of the flag, null if set the first time.
	 * @throws TreeFlagException When a nonexistent flag is set null.
	 */
	TreeFlagValue setFlag( TaskTreeFlag flag, TreeFlagValue value ) throws TreeFlagException {
		TreeFlagValue entry;

		if ( value == flag.getDefaultValue() ) {													// YUP, instance does matter.
			entry = flags.remove( flag );
			if ( null == entry ) {
				throw new TreeFlagException( "Removing a nonexistent flag.", flag );				// No change, no invalidation.
			}
		} else {
			entry = flags.put( flag, value );														// May be null. OK.
		}
		flagsArray = null;																			// Invalidate.
		flagValuesArray = null;																		// Invalidate.
		return entry;
	}

	/**
	 * Acquires an advisory lock on the current task tree item. No debugging, no deadlock checks.
	 * Should be used with caution and called only by the TaskTree class.
	 */
	synchronized void acquireLock() {
		while ( lock ) {
			try {
				wait();
			} catch ( InterruptedException exception ) {
				System.err.println( "Interrupted during sleep." );
			}
		}
		lock = true;
	}

	/**
	 * Releases the advisory lock on the current task tree item. Should be used with caution
	 * and called only by the TaskTree class.
	 */
	synchronized void releaseLock() {
		lock = false;
		notify();
	}
}
