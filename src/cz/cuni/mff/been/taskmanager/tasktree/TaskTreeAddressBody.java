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
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a path in the tree of tasks. These paths only exist from the user's point
 * of view and do not influence task execution in any way. 
 * 
 * @author Andrej Podzimek
 */
final class TaskTreeAddressBody {
	
	static {
		try {
			rootAddress = new TaskTreeAddressBody( "", new String[ 0 ] );
		} catch ( RemoteException exception ) {
			exception.printStackTrace();															// Nothing can be done here.
			System.out.println( "Root address initialization failed. Exiting." );
			throw new ExceptionInInitializerError( exception );										// This is dirty.
		}
	}
	
	/** The most important address ever. */
	private static final TaskTreeAddressBody rootAddress;
	
	/** Separate segments of the path. */
	private final String[] segments;
	
	/** The whole path. */
	private final String path;
	
	/** The lightweight tree address reference to this path object. */
	private TaskTreeAddress address;
	
	/** A hash value to use in the internal "tree" data structures. */
	private Long hash;
	
	/**
	 * Creates a new address from an array of String. (Or from a bunch of variable arguments.)
	 * This constructor should never be called directly from outside this package!
	 * 
	 * @param segments Segments of the path this address should represent.
	 * @throws MalformedAddressException When a segment is empty, null or contains a slash.
	 * @throws RemoteException When it rains.
	 */
	TaskTreeAddressBody( String ... segments ) throws MalformedAddressException, RemoteException {
		this.segments = parseSegments( segments );
		this.path = segToString( this.segments );
		this.hash = hashSegs( this.segments );
		this.address = new TaskTreeAddress( this.hash );
	}
	
	/**
	 * Creates a new address from a formatted String. This constructor should never be called
	 * directly from outside this package!
	 * 
	 * @param path The path string this address should represent.
	 * @throws MalformedAddressException When empty segments or other flaws are encountered.
	 * @throws RemoteException When it rains.
	 */
	TaskTreeAddressBody( String path ) throws MalformedAddressException, RemoteException {
		this.segments = parseSegments( path );
		this.path = path;
		this.hash = hashSegs( this.segments );
		this.address = new TaskTreeAddress( this.hash );
	}
	
	/**
	 * A special unchecked constructor just for the sake of efficiency. Must not be called from
	 * outside this package. No sanity checks are performed.
	 * 
	 * @param path The path string this address should represent.
	 * @param segments Segments of the path this address should represent.
	 * @throws RemoteException When it rains.
	 */
	TaskTreeAddressBody( String path, String ... segments ) throws RemoteException {
		this.segments = segments;
		this.path = path;
		this.hash = hashSegs( this.segments );
		this.address = new TaskTreeAddress( this.hash );
	}
	
	/**
	 * A dummy helper that could help find some toString() misuse cases.
	 * 
	 * @return null
	 */
	@Override
	public String toString() {
		return null;
	}
	
	/**
	 * Path string getter.
	 * 
	 * @return The path this object represents.
	 */
	final String getPathString() {
		return path;
	}

	/**
	 * Path segments getter.
	 * 
	 * @return Segments of the path this object represents.
	 */
	String[] getPathSegments() {
		final String[] result = new String[ segments.length ];
		System.arraycopy( segments, 0, result, 0, segments.length );
		return result;
	}
	
	/**
	 * Hash code getter.
	 * 
	 * @return Hash code of this object (used for efficient address lookup).
	 */
	Long longHashCode() {
		return hash;
	}
	
	/**
	 * Task tree address reference getter.
	 * 
	 * @return The lightweight reference to this tree address.
	 */
	TaskTreeAddress getTreeAddress() {
		return address;
	}
	
	/**
	 * Change the hash function value. This can be used in the (very rare and nearly impossible)
	 * case of hash collisions.
	 */
	void rehash() {
		address = new TaskTreeAddress( ++hash );													// Ugly, but unlikely. :-)
	}
	
	/**
	 * Parent path getter.
	 * 
	 * @return Path to the parent node, null if this is a root node.
	 */
	String getParentPathString() {
		switch ( segments.length ) {
			case 0:
				return null;																		// Bug catcher.
			case 1:
				return rootAddress.path;
			default:
				final StringBuilder builder;
				
				builder = new StringBuilder();
				for ( int i = 0; i < segments.length - 1; ++i ) {
					builder.append( '/' ).append( segments[ i ] );
				}
				return builder.toString();					
		}
	}
	
	/**
	 * Parent segments getter.
	 * 
	 * @return Array of segments without the last segment. Null if this is a root node.
	 */
	String[] parentSegments() {
		final String[] result;
		
		if ( segments.length == 1 ) {
			return rootAddress.segments;
		}		
		result = new String[ segments.length - 1 ];
		System.arraycopy( segments, 0, result, 0, result.length );
		return result;
	}
	
	/**
	 * Converts an array of path segments to a path string.
	 * 
	 * @param segments Array of path segments.
	 * @return A path string created usign the slash syntax.
	 */
	static String segToString( String[] segments ) {
		final StringBuilder builder;
		
		builder = new StringBuilder();
		for ( String segment : segments ) {
			builder.append( '/' ).append( segment );												// This method is null-tolerant.
		}
		return builder.toString();
	}
	
	static TaskTreeAddressBody getRootAddress() {
		return rootAddress;
	}

	/**
	 * Checks whether the list of string segments contains a zero length segment.
	 * 
	 * @param segments The list of segments.
	 * @return A copy of the array of segments. (This means that the original array can be modified
	 * and re-used when necessary.)
	 * @throws MalformedAddressException 
	 */
	private static final String[] parseSegments( String[] segments )
	throws MalformedAddressException {
		if ( segments.length == 0 ) {
			throw new MalformedAddressException( "Empty segments." );
		}
		for ( String segment : segments ) {
			if ( null == segment ) {
				throw new MalformedAddressException( "Invalid null segment." );
			} else if ( segment.isEmpty() ) {
				throw new MalformedAddressException( "Invalid empty segment." );
			} else if ( segment.indexOf( '/' ) != -1 ) {
				throw new MalformedAddressException( "Segment containing a slash: " + segment );
			}
		}
		
		final String[] result = new String[ segments.length ];
		System.arraycopy( segments, 0, result, 0, segments.length );
		return result;
	}
	
	
	/**
	 * Checks whether the supplied path is a valid path and splits the path into segments.
	 * 
	 * @param path The path string to parse.
	 * @return An array of segments
	 * @throws MalformedAddressException When a zero-length segment is found.
	 */
	private static final String[] parseSegments( String path ) throws MalformedAddressException {
		if ( path.isEmpty() ) {
			throw new MalformedAddressException( "Empty path supplied." );
		}
		if ( path.charAt( 0 ) != '/' ) {
			throw new MalformedAddressException( "First character not a slash: " + path );
		}
		
		final StringBuilder builder = new StringBuilder();
		final List< String > segmentList = new LinkedList< String >();
		final int end = path.length();

		String segment;
		int i;
		char c;
		
		for ( i = 1; i < end; ++i ) {
			switch ( c = path.charAt( i ) ) {
				case '/':
					segment = builder.toString();
					if ( segment.length() > 0 ) {
						segmentList.add( segment );
					} else {
						throw new MalformedAddressException( "Empty path segment in: " + path );
					}
					builder.delete( 0, builder.length() );
					break;
				default:
					builder.append( c );
					break;
			}
		}
		segment = builder.toString();
		if ( segment.length() > 0 ) {
			segmentList.add( segment );
		} else {
			throw new MalformedAddressException( "Empty path segment in: " + path );
		}

		return segmentList.toArray( new String[ segmentList.size() ] );
	}
	
	/**
	 * Computes a simple hash of the input string segments.
	 * 
	 * @param segments Array of path segments.
	 * @return A hash value of the current path.
	 */
	private static final long hashSegs( String[] segments ) {
		long result;
		
		result = 0;
		for ( String segment : segments ) {
			result *= segment.hashCode();
			result += segment.hashCode();
		}
		return result;
	}
}
