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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeItem.Type;

/**
 * This class represents a tree of task descriptors. This tree has nothing in common with the order
 * of task execution or the method of results collection. It used <b>only</b> on the web interface
 * to simplify the task list presentation.
 * 
 * @author Andrej Podzimek
 */
public final class TaskTree extends UnicastRemoteObject implements TaskTreeInput, TaskTreeQuery {

	private static final long serialVersionUID = 57888486211450234L;
	
	/** An exclusive lock acquired when tree structure is changed. */
	private final Lock treeWriteLock;
	
	/** A shared lock acquired when tree data is read or modified (without structural changes). */
	private final Lock treeReadLock;
	
	/** An exclusive lock acquired when address tables are updated. */
	private final Lock addressWriteLock;
	
	/** A shared lock acquired when address tables are read. */
	private final Lock addressReadLock;

	/** Mapping path strings to addresses. */
	private final HashMap< String, TaskTreeAddressBody > pathToAddress =							// Protected by address locks.
		new HashMap< String, TaskTreeAddressBody >();

	/** A set of ALL existing hash codes. */
	private final HashMap< Long, TaskTreeAddressBody > hashToAddress =								// Protected by address locks.
		new HashMap< Long, TaskTreeAddressBody >();

	/** Mapping address hash codes to node types. */
	private final HashMap< Long, Type > hashToType =												// Protected by tree locks.
		new HashMap< Long, Type >();

	/** Mapping address hash codes to leaves. */
	private final HashMap< Long, TaskTreeLeaf > hashToLeaf =										// Protected by tree locks.
		new HashMap< Long, TaskTreeLeaf >();

	/** Special mapping of hash codes to tree nodes. Just to avoid casting. */
	private final HashMap< Long, TaskTreeNode > hashToNode =										// Protected by tree locks.
		new HashMap< Long, TaskTreeNode >();

	/** Address of the root node. */
	private final TaskTreeAddressBody rootAddress;
	
	/** Hash code of the root address. */
	private final long rootHash;
	
	/** The root node Himself. */
	private final TaskTreeNode rootNode;
	
	/**
	 * The default constructor. Initializes a new empty task tree with a root node.
	 */
	public TaskTree() throws RemoteException {
		ReentrantReadWriteLock lock;

		this.rootAddress = TaskTreeAddressBody.getRootAddress();
		this.rootHash = this.rootAddress.longHashCode();
		this.rootNode = new TaskTreeNode( this, this.rootAddress, null );
		this.pathToAddress.put( this.rootAddress.getPathString(), this.rootAddress );
		this.hashToAddress.put( this.rootHash, this.rootAddress );		
		this.hashToType.put( this.rootHash, Type.NODE );
		this.hashToNode.put( this.rootHash, this.rootNode );
		lock = new ReentrantReadWriteLock();
		treeWriteLock = lock.writeLock();
		treeReadLock = lock.readLock();
		lock = new ReentrantReadWriteLock();
		addressWriteLock = lock.writeLock();
		addressReadLock = lock.readLock();
	}

	@Override
	public void addLeaf( TaskTreeAddress address, TaskEntry entry )
	throws IllegalAddressException, RemoteException {
		Type type;
		Long curHash;
		TaskTreeAddressBody firstAddr, curAddr, curParentAddr;
		TaskTreeNode curNode, curParentNode;
		TaskTreeLeaf curLeaf;
		
		curHash = address.longHashCode();															// Get hash code of the address.
		firstAddr = curAddr = addressFromHash( curHash );											// It must exist.
		treeWriteLock.lock();
		try {
			type = hashToType.get( curHash );														// Do we know this address?
			
			if ( null == type ) {																	// No, we don't know the address.
				final Deque< TaskTreeAddressBody > stack;
	
				stack = new ArrayDeque< TaskTreeAddressBody >();									// Stack of unknown addresses.
				do {
					stack.push( curAddr );															// Store the unknown address.
					curAddr = getParentAddress( curAddr );											// Get its parent.
					curHash = curAddr.longHashCode();												// Compute the parent's hash.
				} while ( !hashToType.containsKey( curHash ) );										// Do we already know this parent?
				curNode = hashToNode.get( curHash );												// Get the first known ancestor!
				if ( null == curNode ) {															// Exists && !Node => Leaf
					throw new IllegalAddressException(												// Leaves can't have children.
						"Attempting to add children to a leaf.",
						firstAddr
					);
				}
				while ( stack.size() > 1 ) {														// For all our unknown ancestors:
					curParentAddr = curAddr;														// Save the previous level.
					curParentNode = curNode;														// Save the previous level.
					curAddr = stack.pop();															// Step one level deeper.
					curHash = curAddr.longHashCode();												// Step one level deeper.
					curNode = new TaskTreeNode( this, curAddr, curParentAddr );						// Create the new node.
					curParentNode.addChild( curAddr );												// Let the parent know.
					hashToType.put( curHash, Type.NODE );											// Register node's type.
					hashToNode.put( curHash, curNode );												// Register node's instance.
				}
				curParentAddr = curAddr;															// Save the last node level.
				curParentNode = curNode;															// Save the last node level.
				curAddr = stack.pop();																// Step to the new leaf level.
				curHash = curAddr.longHashCode();													// Step to the new leaf level.
				curLeaf = new TaskTreeLeaf( this, curAddr, curParentAddr, entry );					// Create the new leaf.
				curParentNode.addChild( curAddr );													// Let the parent (node) know.
				hashToType.put( curHash, Type.LEAF );												// Register node's type.
				hashToLeaf.put( curHash, curLeaf );													// Register node's instance.
			} else {																				// An item with this address exists.
				switch ( type ) {
					case NODE:																		// Leaves cannot replace nodes.
						throw new IllegalAddressException(
							"Attempted to insert a conflicting leaf.",
							firstAddr
						);
					case LEAF:																		// A leaf can be reinserted...
						if ( entry != hashToLeaf.get( curHash ).getTask() ) {						// ...if it carries the same task.
							throw new IllegalAddressException(
								"Attempted to reinsert a leaft with a different task.",
								firstAddr
							);
						}
						curParentAddr = getParentAddress( curAddr );								// Just find the parent address...
						hashToNode.get( curParentAddr.longHashCode() ).addChild( curAddr );			// ...and re-add the child.
						break;
					default:
						throw new IllegalStateException( "The Impossible has happened" );			// This should never happen.
				}
			}
		} finally {
			treeWriteLock.unlock();
		}
	}

	@Override
	public void addNode( TaskTreeAddress address ) throws IllegalAddressException, RemoteException {
		Type type;
		Long curHash;
		TaskTreeAddressBody	firstAddr, curAddr, curParentAddr;
		TaskTreeNode curNode, curParentNode;

		curHash = address.longHashCode();															// Get the hash code of the address.
		firstAddr = curAddr = addressFromHash( curHash );											// It must exist.
		treeWriteLock.lock();
		try {
			type = hashToType.get( curHash );														// Do we know this address?
			
			if ( null == type ) {																	// No, we don't know this address.
				final Deque< TaskTreeAddressBody > stack;
	
				stack = new ArrayDeque< TaskTreeAddressBody >();
				do {																				// Find the nearest ancestor.
					stack.push( curAddr );															// Store the unknown address.
					curAddr = getParentAddress( curAddr );											// Get its parent.
					curHash = curAddr.longHashCode();												// Compute the parent's hash.
				} while ( !hashToType.containsKey( curHash ) );										// Do we already know this parent?
				curNode = hashToNode.get( curHash );												// Get the first known ancestor!
				if ( null == curNode ) {															// Exists && !Node => Leaf
					throw new IllegalAddressException(												// Leaves can't have children.
						"Attempting to add children to a leaf.",
						firstAddr
					);
				}
				do {																				// For all our unknown ancestors:
					curParentAddr = curAddr;														// Save the previous level.
					curParentNode = curNode;														// Save the previous level.
					curAddr = stack.pop();															// Step one level deeper.
					curHash = curAddr.longHashCode();												// Step one level deeper.
					curNode = new TaskTreeNode( this, curAddr, curParentAddr );						// Create the new node.
					curParentNode.addChild( curAddr );												// Let the parent know.
					hashToType.put( curHash, Type.NODE );											// Register node's type.
					hashToNode.put( curHash, curNode );												// Register node's instance.
				} while ( !stack.isEmpty() );														// End of the stack.
			} else {																				// An item with this address exists.
				switch ( type ) {
					case NODE:																		// A node can be reinserted.
						curParentAddr = getParentAddress( curAddr );								// Just find the parent address...
						hashToNode.get( curParentAddr.longHashCode() ).addChild( curAddr );			// ...and re-add the child.
						break;
					case LEAF:																		// Nodes cannot replace leaves.
						throw new IllegalAddressException(
							"Attempting to insert a conflicting node.",
							firstAddr
						);
					default:
						throw new IllegalStateException( "The Impossible has happened." );
				}
			}
		} finally {
			treeWriteLock.unlock();
		}
	}

	@Override
	public TaskTreeAddress addressFromPath( String path )
	throws MalformedAddressException, RemoteException {
		TaskTreeAddressBody result;

		addressWriteLock.lock();
		try {
			result = pathToAddress.get( path );
			if ( null == result ) {
				result = new TaskTreeAddressBody( path );
				while( hashToAddress.containsKey( result.longHashCode() ) ) {
					result.rehash();
				}
				pathToAddress.put( path, result );
				hashToAddress.put( result.longHashCode(), result );
			}
		} finally {
			addressWriteLock.unlock();
		}
		return result.getTreeAddress();
	}

	@Override
	public TaskTreeAddress addressFromPath( String ... path )
	throws MalformedAddressException, RemoteException {
		TaskTreeAddressBody result;

		addressWriteLock.lock();
		try {
			result = pathToAddress.get( TaskTreeAddressBody.segToString( path ) );
			if ( null == result ) {
				result = new TaskTreeAddressBody( path );
				while ( hashToAddress.containsKey( result.longHashCode() ) ) {
					result.rehash();
				}
				pathToAddress.put( result.getPathString(), result );
				hashToAddress.put( result.longHashCode(), result );
			}
		} finally {
			addressWriteLock.unlock();
		}
		return result.getTreeAddress();
	}

	@Override
	public void clearInclusive( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		TaskTreeAddressBody addr;
		TaskTreeNode parentNode;
		Type type;
		Long hash;

		hash = address.longHashCode();
		treeWriteLock.lock();
		try {
			type = hashToType.get( hash );
			if ( null == type ) {
				throw new IllegalAddressException(
					"Clear INC with unknown address.",
					addressFromHash( hash )															// It must exist.
				);
			} else if ( rootHash == hash ) {
				throw new IllegalAddressException(
					"Clear INT on the root address.",
					TaskTreeAddressBody.getRootAddress()
				);
			}
			addr = addressFromHash( hash );															// It must exist.
			parentNode = hashToNode.get( getParentAddress( addr ).longHashCode() );
			parentNode.removeChild( addr );
			clearNodeRecursive( addr );
		} finally {
			treeWriteLock.unlock();
		}
	}

	@Override
	public void clearSubtrees( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		TaskTreeNode node;
		Long hash;

		hash = address.longHashCode();
		treeWriteLock.lock();
		try {
			node = hashToNode.get( hash );
			if ( null == node ) {
				throw new IllegalAddressException(
					"Clear SUB with unknown address.",
					addressFromHash( hash )															// It must exist.
				);
			}
			for ( TaskTreeAddressBody subnode : node.getChildren() ) {
				clearNodeRecursive( subnode );
			}
			node.clear();
		} finally {
			treeWriteLock.unlock();
		}
	}

	@Override
	public Type getTypeAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		Long hash;
		Type type;
		
		hash = address.longHashCode();
		treeReadLock.lock();
		try {
			type = hashToType.get( hash );
			if ( null == type ) {
				throw new IllegalAddressException(
					"Type query for unknown address.",
					addressFromHash( hash )															// It must exist.
				);
			}
			return type;
		} finally {
			treeReadLock.unlock();
		}
	}
	
	@Override
	public TaskTreeAddress[] getChildrenAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		Long hash;
		Type type;
		
		hash = address.longHashCode();
		treeReadLock.lock();
		try {
			type = hashToType.get( hash );
			if ( null == type ) {
				throw new IllegalAddressException(
					"Children query for unknown address.",
					addressFromHash( hash )															// It must exist.
				);
			}
			switch ( type ) {
				case LEAF:
					return addresses( hashToLeaf.get( hash ).getChildren() );						// Yes, this throws. OK.
				case NODE:
					return addresses( hashToNode.get( hash ).getChildren() );
				default:
					throw new IllegalStateException( "Unknown enum member." );
			}
		} finally {
			treeReadLock.unlock();
		}
	}

	@Override
	public TaskEntry getTaskAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		Long hash;
		Type type;
		
		hash = address.longHashCode();
		treeReadLock.lock();
		try {
			type = hashToType.get( hash );
			if ( null == type ) {
				throw new IllegalAddressException(
					"Task query at unknown address.",
					addressFromHash( hash )															// It must exist.
				);
			}
			switch ( type ) {
				case LEAF:
					return hashToLeaf.get( hash ).getTask();
				case NODE:
					return hashToNode.get( hash ).getTask();										// Yes, this throws. OK.
				default:
					throw new IllegalStateException( "Unknown enum member." );
			}
		} finally {
			treeReadLock.unlock();
		}
	}
	
	@Override
	public String[] getPathAt( TaskTreeAddress address ) {
		addressReadLock.lock();
		try {
			return addressFromHash( address.longHashCode() ).getPathSegments();						// It must exist.
		} finally {
			addressReadLock.unlock();
		}
	}
	
	@Override
	public void clear() {
		treeWriteLock.lock();
		hashToType.clear();
		hashToLeaf.clear();
		hashToNode.clear();
		treeWriteLock.unlock();
	}

	@Override
	public void setFlag( TaskTreeAddress address, TaskTreeFlag flag, Enum< ? > ordinal )
	throws IllegalAddressException, TreeFlagException, RemoteException {
		setFlag( address, flag, ordinal, null );
	}
	
	@Override
	public void setFlag(
		TaskTreeAddress address,
		TaskTreeFlag flag,
		Enum< ? > ordinal,
		Serializable message
	) throws IllegalAddressException, TreeFlagException, RemoteException {
		flag.validate( ordinal );
		treeReadLock.lock();
		try {
			setFlagNonblocking(
				address.longHashCode(),
				flag,
				new TreeFlagValue( ordinal, message )
			);
		} finally {
			treeReadLock.unlock();
		}
	}

	@Override
	public TreeFlagValue getFlagValueAt( TaskTreeAddress address, TaskTreeFlag flag )
	throws RemoteException, IllegalAddressException {
		treeReadLock.lock();
		try {
			return getFlagValueAtNonblocking( address.longHashCode(), flag );
		} finally {
			treeReadLock.unlock();
		}
	}

	@Override
	public TaskTreeFlag[] getFlagsAt( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		Long hash;
		Type type;
		
		hash = address.longHashCode();
		treeReadLock.lock();
		try {
			type = hashToType.get( hash );
			if ( null == type ) {
				throw new IllegalAddressException(
					"Flags list query at unknown address.",
					addressFromHash( hash )															// It must exist.
				);
			}
			switch ( type ) {
				case LEAF:
					return hashToLeaf.get( hash ).getFlags();
				case NODE:
					return hashToLeaf.get( hash ).getFlags();
				default:
					throw new IllegalStateException( "Unknown enum member." );
			}
		} finally {
			treeReadLock.unlock();
		}
	}

	@Override
	public TaskTreeRecord getRecordAt( TaskTreeAddress address, boolean path, boolean data, boolean flags )
	throws IllegalAddressException, RemoteException {
		TaskTreeAddressBody addr;
		Long hash;
		Type type;
		TaskTreeBasic el;
		
		hash = address.longHashCode();
		treeReadLock.lock();
		try {
			type = hashToType.get( hash );
			if ( null == type ) {
				throw new IllegalAddressException(
					"Record query at unknown address.",
					addressFromHash( hash )															// It must exist.
				);
			}
			addr = addressFromHash( hash );															// It must exist.
			if ( path ) {
				if ( flags ) {
					if ( data ) {																	// Path + Data + Flags
						switch ( type ) {
							case LEAF:
								el = hashToLeaf.get( hash );
								return new TaskTreeRecord(
									addr.getPathSegments(),
									el.getTask(),
									el.getFlagValues()
								);
							case NODE:
								el = hashToNode.get( hash );
								return new TaskTreeRecord(
									addr.getPathSegments(),
									addresses( el.getChildren() ),
									el.getFlagValues()
								);
							default:
								throw new IllegalStateException( "Unknown enum member." );
						}
					} else {																		// Path + Flags
						switch ( type ) {
							case LEAF:
								el = hashToLeaf.get( hash );
								break;
							case NODE:
								el = hashToNode.get( hash );
								break;
							default:
								throw new IllegalStateException( "Unknown enum member." );
						}
						return new TaskTreeRecord( addr.getPathSegments(), el.getFlagValues() );
					}
				} else {																				
					if ( data ) {																	// Path + Data
						switch ( type ) {
							case LEAF:
								el = hashToLeaf.get( hash );
								return new TaskTreeRecord( addr.getPathSegments(), el.getTask() );
							case NODE:
								el = hashToNode.get( hash );
								return new TaskTreeRecord(
									addr.getPathSegments(),
									addresses( el.getChildren() )
								);
							default:
								throw new IllegalStateException( "Unknown enum member." );
						}				
					} else {																		// Path
						return new TaskTreeRecord( addr.getPathSegments() );
					}
				}
			} else {
				if ( flags ) {
					if ( data ) {																	// Data + Flags
						switch ( type ) {
							case LEAF:
								el = hashToLeaf.get( hash );
								return new TaskTreeRecord( el.getTask(), el.getFlagValues() );
							case NODE:
								el = hashToNode.get( hash );
								return new TaskTreeRecord(
									addresses( el.getChildren() ),
									el.getFlagValues()
								);
							default:
								throw new IllegalStateException( "Unknown enum member." );
						}
					} else {																		// Flags
						switch ( type ) {
							case LEAF:
								el = hashToLeaf.get( hash );
								break;
							case NODE:
								el = hashToNode.get( hash );
								break;
							default:
								throw new IllegalStateException( "Unknown enum member." );
						}
						return new TaskTreeRecord( el.getFlagValues() );
					}
				} else {																				
					if ( data ) {																	// Data
						switch ( type ) {
							case LEAF:
								el = hashToLeaf.get( hash );
								return new TaskTreeRecord( el.getTask() );
							case NODE:
								el = hashToNode.get( hash );
								return new TaskTreeRecord( addresses( el.getChildren() ) );
							default:
								throw new IllegalStateException( "Unknown enum member." );
						}				
					} else {																		// Nuffing
						return new TaskTreeRecord();
					}
				}
				
			}
		} finally {
			treeReadLock.unlock();
		}
	}

	/**
	 * Implementation of he flag getter that does not care about node locking. (Called from places
	 * where locks are already acquired. For example, all the needed locks are guaranteed to be
	 * acquired when inheritance handlers run.)
	 * 
	 * @param hash Hash code of the address pointing at the requested node.
	 * @param flag The flag to return.
	 * @return A flag value instance from the required flag.
	 * @throws IllegalAddressException When an invalid address is supplied.
	 */
	TreeFlagValue getFlagValueAtNonblocking( Long hash, TaskTreeFlag flag )
	throws IllegalAddressException {
		Type type;
		
		type = hashToType.get( hash );
		if ( null == type ) {
			throw new IllegalAddressException(
				"Flag value query at unknown address.",
				addressFromHash( hash )																// It must exist.
			);
		}
		switch ( type ) {
			case LEAF:
				return hashToLeaf.get( hash ).getFlagValue( flag );
			case NODE:
				return hashToNode.get( hash ).getFlagValue( flag );
			default:
				throw new IllegalStateException( "Unknown enum member." );
		}
	}
	
	/**
	 * Implemetation of the flag setter that does not care about node locking. (Called from
	 * places where locks are already acquired.
	 * 
	 * @param hash Hash code of the address pointing at the node in which the flag will beset.
	 * @param flag The flag to modify.
	 * @param newValue Value of the flag to set.
	 * @throws IllegalAddressException When an invalid address is supplied.
	 * @throws TreeFlagException When troubles with flags occur, possibly due to bugs. ;-)
	 * @throws RemoteException Almost never. Should manipulate local objects only!
	 */
	private void setFlagNonblocking(
		Long hash,
		TaskTreeFlag flag,
		TreeFlagValue newValue
	) throws IllegalAddressException, TreeFlagException, RemoteException {
		TaskTreeAddressBody addr;
		TaskTreeBasic current;
		TaskTreeNode parent;
		TreeFlagValue oldValue;
		BasicTreeFlagInheritance inheritance;
		
		current = hashToLeaf.get( hash );
		if ( null == current ) {
			throw new IllegalAddressException(
				"Set flag query at unknown address.",
				addressFromHash( hash )
			);
		}
		current.acquireLock();
		try {
			oldValue = current.setFlag( flag, newValue );
			addr = current.getParentAddress();
			parent = hashToNode.get( addr.longHashCode() );
			parent.acquireLock();
			current.releaseLock();
			current = parent;
			while ( addr != rootAddress ) {
				inheritance = new BasicTreeFlagInheritance( parent, flag );
				if ( !flag.inherit( oldValue, newValue, inheritance ) ) { return; }
				newValue = inheritance.getNewValue();
				oldValue = inheritance.getOldValue();
				addr = current.getParentAddress();
				parent = hashToNode.get( addr.longHashCode() );
				parent.acquireLock();
				current.releaseLock();
				current = parent;																	// Must be here (for finally).
			}
			inheritance = new BasicTreeFlagInheritance( parent, flag );
			flag.inherit( oldValue, newValue, inheritance );
		} finally {
			current.releaseLock();																	// Variable current changes. OK.
		}
	}

/*
 * Automatic flags cleanup should not occur on this level. The user and maintainer of the given
 * task should follow the inheritance rules of the flags he uses and reset flags to default
 * values when necessary.
 */
//	private void clearFlagsNonblockingMustExist(
//		TaskTreeAddress address,
//		Iterable< TaskTreeFlag > flags
//	) {
//		try {
//			for ( TaskTreeFlag flag : flags ) {
//				setFlagNonblocking( address, flag, flag.getDefaultValue() );
//			}
//		} catch ( TreeFlagException exception ) {
//			System.err.println( "Bug in the task tree!" );
//			exception.printStackTrace();
//			System.exit( -1 );
//		} catch ( IllegalAddressException exception ) {
//			System.err.println( "Bug in the task tree!" );
//			exception.printStackTrace();
//			System.exit( -2 );
//		}
//	}

	/**
	 * A special parent address getter. Tries to look up the address in the database. Creates
	 * a new one (efficiently) if necessary.
	 * 
	 * @param address An address to which the parent address should be computed.
	 * @return A unique tree address corresponding to the parent path.
	 * @throws RemoteException Almost never. Should manipulate local objects only!
	 */
	private TaskTreeAddressBody getParentAddress( TaskTreeAddressBody address )
	throws RemoteException {
		String parentPath;
		TaskTreeAddressBody result;

		parentPath = address.getParentPathString();
		synchronized ( hashToAddress ) {
			result = pathToAddress.get( parentPath );
			if ( null == result ) {
				result = new TaskTreeAddressBody( parentPath, address.parentSegments() );
				while ( hashToAddress.containsKey( result.longHashCode() ) ) {
					result.rehash();
				}
				pathToAddress.put( parentPath, result );
				hashToAddress.put( result.longHashCode(), result );
			}
		}
		return result;
	}

	/**
	 * Scans a subtree using DFS and removes all of its nodes from this tree's tables.
	 * When this method is called, the root of the deleted node must be still present
	 * at least in hashToItem. hashToType and hashToNode need not contain that element any more.
	 * However, it should not be deleted in advance for preformance reasons. (The mappings need
	 * to be accessed anyway.)
	 * 
	 * @param root Address of the root element to remove.
	 * @throws RemoteException Almost never. Should manipulate local objects only!
	 */
	private void clearNodeRecursive( TaskTreeAddressBody root ) throws RemoteException {
		Long hash;
		Type type;

		hash = root.longHashCode();
		type = hashToType.remove( hash );															// Assuming it exists.
		switch ( type ) {															
			case NODE:
				TaskTreeBasic item;
				
				item = hashToNode.get( hash );
				try {
					for ( TaskTreeAddressBody subnode : item.getChildren() ) {
						clearNodeRecursive( subnode );
					}
				} catch ( IllegalAddressException exception ) {
					System.err.println( "This should have NEVER happened." );
					exception.printStackTrace();
				}
				hashToNode.remove( hash );
				break;
			case LEAF:
				// clearFlagsNonblockingMustExist( root, hashToLeaf.remove( hash ).getFlags() );
				/*
				 * Unsetting flags should be the user's responsibility. The user knows how
				 * the corresponding flag's inheritance is designed and whether flags of deleted
				 * items need to be reset or not.
				 */
				hashToLeaf.remove( hash );
				break;
			default:
				throw new IllegalStateException( "The impossible has happened." );
		}
	}

	/**
	 * Reads an address from the address table in a thread-safe manner.
	 * 
	 * @param hash The hash code to look up.
	 * @return An address body instance.
	 */
	private TaskTreeAddressBody addressFromHash( long hash ) {
		addressReadLock.lock();
		try {
			return hashToAddress.get( hash );
		} finally {
			addressReadLock.unlock();
		}
	}
	
	/**
	 * Converts an array of internal task tree adress representation intsances to an array
	 * of the publically visible hash codes.
	 * 
	 * @param addresses The collection of addresses to convert.
	 * @return An array of publically visible task tree addresses.
	 */
	private TaskTreeAddress[] addresses( Collection< TaskTreeAddressBody > addresses ) {
		TaskTreeAddress[] result;
		int i;
		
		result = new TaskTreeAddress[ addresses.size() ];
		i = 0;
		for ( TaskTreeAddressBody address : addresses ) {
			result[ i++ ] = address.getTreeAddress();
		}
		return result;
	}
}
