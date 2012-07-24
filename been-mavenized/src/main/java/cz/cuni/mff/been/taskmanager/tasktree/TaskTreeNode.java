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

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * This class represents an internal node of the visual tree. It can contain both subnodes
 * and leaves in arbitrary order.
 * 
 * @author Andrej Podzimek
 */
final class TaskTreeNode extends TaskTreeBasic {

	private static final long serialVersionUID = 8788634183361913476L;

	/**
	 * A small Iterable to step through flag values of a subnode. Instances of this class will
	 * be spawned on demand when a flag inheritance handler needs to know the values of all
	 * the children.
	 */
	private final class ChildrenFlagIterable implements Iterable< TreeFlagValue > {
		
		/** The flag instance this Iterable will use to request values. */
		private final TaskTreeFlag flag;
		
		/**
		 * The one and only constructor.
		 * 
		 * @param flag The flag this Iterable will use when requesting the return values.
		 */
		ChildrenFlagIterable( TaskTreeFlag flag ) {
			this.flag = flag;
		}
		
		@Override
		public Iterator< TreeFlagValue > iterator() {
			return new Iterator< TreeFlagValue >() {
				
				/** Internal iterator over the collection of subnodes/children. */
				private final Iterator< TaskTreeAddressBody > iterator =
					childAddressUnsafeIterable.iterator();
				
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public TreeFlagValue next() {
					try {
						return TaskTreeNode.this.tree.getFlagValueAtNonblocking(
							iterator.next().longHashCode(),
							flag
						);
					} catch ( IllegalAddressException exception ) {
						System.err.println( "Internal error in the task tree." );
						exception.printStackTrace();
						System.exit( -1 );
						return null;
					}
				}
				
				@Override
				public void remove() {
					throw new UnsupportedOperationException( "No flag removals from iterator!" );
				}				
			};
		}
	}
	
	/** Mapping of the order of a child to its tree address. */
	private final TreeMap< Integer, TaskTreeAddressBody > counterToAddress =						// CAUTION!!! Order does matter.
		new TreeMap< Integer, TaskTreeAddressBody >();

	/** Mapping of a child's address to its counter order. */
	private final HashMap< Long, Integer > hashToCounter =
		new HashMap< Long, Integer >();
	
	/** Mapping flags to flag counters. */
	private final HashMap< String, HashMap< Enum< ? >, Integer > > flagToCounters =
		new HashMap< String, HashMap< Enum< ? >, Integer > >();

	/** A pre-initialized Iterable to step through this node's children in the correct order. */
	private final Iterable< TaskTreeAddressBody > childAddressUnsafeIterable =
		new Iterable< TaskTreeAddressBody >() {

			/** Keys of the {@link orderToAddresss} map. */
			private final Set< Entry< Integer, TaskTreeAddressBody> > entries = counterToAddress.entrySet();
	
			@Override
			public Iterator< TaskTreeAddressBody > iterator() {
				return new Iterator< TaskTreeAddressBody >() {
	
					/** An internal iterator. */
					private final Iterator< Entry< Integer, TaskTreeAddressBody > > iterator =
						entries.iterator();
	
					/** A variable to move the actual iterator one step forward. */
					private Entry< Integer, TaskTreeAddressBody > last = null;
	
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}
	
					@Override
					public TaskTreeAddressBody next() {
						return ( last = iterator.next() ).getValue();
					}
	
					@Override
					public void remove() {
						if ( null == last ) {
							throw new IllegalStateException( "remove() did not follow next()." );
						}
						iterator.remove();
						hashToCounter.remove( last.getValue().longHashCode() );
					}
				};
			}
		};

	/** Number of children inserted since construction or since last {@link #clear()}. */
	private int counter;
	
	/** Current number of children */
	private int nChildren;

	/**
	 * Creates a new leaf and initializes its contents.
	 * 
	 * @param address Address of the node.
	 * @param parentAdddress Address of this node's parent.
	 */
	TaskTreeNode(
		TaskTree tree,
		TaskTreeAddressBody address,
		TaskTreeAddressBody parentAdddress
	) {
		super( tree, address, parentAdddress );
		this.counter = 0;
	}

	@Override
	public Collection< TaskTreeAddressBody > getChildren() {										// Doesn't need to be sync'd!
		return counterToAddress.values();
	}

	@Override
	public Type getType() {
		return Type.NODE;
	}

	/**
	 * Children counters getter.
	 * 
	 * @param flag The flag to look up.
	 * @param value The flag value to look up.
	 * @return Number of children where flag is set to value.
	 */
	int getValueCounter( TaskTreeFlag flag, Enum< ? > value ) {
		final String flagKey;
		final Integer result;
		HashMap< Enum< ? >, Integer > map;
		
		flagKey = flag.toString();
		map = flagToCounters.get( flagKey );
		if ( null == map ) {
			return value.ordinal() == flag.getDefaultValue().getOrdinal().ordinal() ? nChildren : 0;
		}		
		result = map.get( value );
		return null == result ? 0 : result;
	}

	/**
	 * Number of children getter.
	 * 
	 * @return The total number of children.
	 */
	int getTotalCounter() {
		return counter;
	}

	/**
	 * Flag value iterable getter. Returns flag values of a node's children without providing
	 * access to their interfaces.
	 * 
	 * @param flag The flag whose values will be shown.
	 * @return An Iterable through the flag values of this node's children.
	 */
	Iterable< TreeFlagValue > getValueIterable( TaskTreeFlag flag ) {
		return new ChildrenFlagIterable( flag );
	}

	/**
	 * Flag value removal announcement. This method is called when a flag value has changed
	 * in one of this node's children.
	 * 
	 * @param flag The flag that has been changed.
	 * @param value The old value that was replaced.
	 */
	void decrementCounter( TaskTreeFlag flag, Enum< ? > value ) {
		final String flagKey;
		final Integer oldCounter;
		HashMap< Enum< ? >, Integer > map;
		
		flagKey = flag.toString();
		map = flagToCounters.get( flagKey );
		if ( null == map ) {																		// If & only if value == default
			map = new HashMap< Enum< ? >, Integer >();
			map.put( flag.getDefaultValue().getOrdinal(), nChildren );								// Number of default values.
			flagToCounters.put( flagKey, map );
		}
		oldCounter = map.get( value );																// This can't be null!
		if ( oldCounter == 1 ) {
			map.remove( value );
			if ( getValueCounter( flag, flag.getDefaultValue().getOrdinal() ) == nChildren ) {		// All flag values unset?
				flagToCounters.remove( flagKey );													// Delete their mapping!
			}
		} else {
			map.put( value, oldCounter - 1 );
		}
	}

	/**
	 * Flag value addition announcement. This method is called when a flag value has changed
	 * in one of this node's children.
	 * 
	 * @param flag The flag that has been changed.
	 * @param value The new value that replaced the old one.
	 */
	void incrementCounter( TaskTreeFlag flag, Enum< ? > value ) {
		final String flagKey;
		final Integer oldCounter;
		HashMap< Enum< ? >, Integer > map;
		
		flagKey = flag.toString();		
		map = flagToCounters.get( flagKey );
		if ( null == map ) {
			map = new HashMap< Enum< ? >, Integer >();
			flagToCounters.put( flagKey, map );
		}
		oldCounter = map.get( value );
		map.put( value, null == oldCounter ? 1 : oldCounter + 1 );
	}
	
	/**
	 * Adds a child to this node's list of children. If the child is already on the list, it will
	 * be moved to the end of the list.
	 * CAUTION!!! Address validity is not checked in any way!
	 * 
	 * @param child Address of the child node to be inserted.
	 * @throws RemoteException Almost never. Manipulates local objects, so it shouldn't happen.
	 */
	void addChild( TaskTreeAddressBody child ) throws RemoteException {
		Integer prevCounter;
		
		if ( ( prevCounter = hashToCounter.put( child.longHashCode(), counter ) ) != null ) {		// Re-inserting a child?
			counterToAddress.remove( prevCounter );													// Remove its old counter value.
		}
		counterToAddress.put( counter, child );														// Put the new counter value.
		++counter;
		++nChildren;
	}
	
	/**
	 * Removes one child node of this node specified by the address.
	 * 
	 * @param child The child node to remove.
	 * @throws IllegalAddressException when the specified child does not exist.
	 * @throws RemoteException Almost never. Manipulates local objects, so it shouldn't happen.
	 */
	void removeChild( TaskTreeAddressBody child ) throws IllegalAddressException, RemoteException {
		Integer prevCounter;
		
		if ( ( prevCounter = hashToCounter.remove( child.longHashCode() ) ) != null ) {				// Did the child exist?
			counterToAddress.remove( prevCounter );													// Remove it from counter mapping.
			--nChildren;
		} else {
			throw new IllegalAddressException( "Child does not exist.", child );
		}
	}
	
	/**
	 * Deletes all the child nodes of this node.
	 */
	void clear() {
		counter = Integer.MIN_VALUE;
		nChildren = 0;
		counterToAddress.clear();
		hashToCounter.clear();
	}
}
