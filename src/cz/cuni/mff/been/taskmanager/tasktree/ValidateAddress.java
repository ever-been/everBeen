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

/**
 * This is a dummy class containing utility methods for task tree address validation.
 * It is *NOT* recommended to use this class! If you do so, you can theoreticaly save one RMI
 * roundtrip on the slow path, but you significantly slow down the fast path!
 * 
 * The preferred way of tree address validation is the optimistic approach -- just ask the
 * Task Manager to parse the address. You cannot create instances of TaskTreeAddress outside
 * the TaskTree anyway! (Doing that would cause the whole tree to stop working. The implementation
 * is based on the assumption that there exists exactly ONE TaskTreeAddress instance for each
 * distinct address.)
 * 
 * @author Andrej Podzimek
 */
@Deprecated
public final class ValidateAddress {
	
	/**
	 * No, just don't do this.
	 */
	private ValidateAddress() {}

	/**
	 * Checks whether the supplied path is a valid path and splits the path into segments.
	 * Deprecated! Use the optimistic approach whenever possible. There's absolutely no point
	 * in making the slow path faster if it affects the fast path.
	 * 
	 * @param address The path string to parse.
	 * @throws MalformedAddressException When a zero-length segment is found.
	 */
	@Deprecated
	public void fromString( String address ) throws MalformedAddressException {
		if ( address.isEmpty() ) {
			throw new MalformedAddressException( "Empty path supplied." );
		}
		if ( address.charAt( 0 ) != '/' ) {
			throw new MalformedAddressException( "First character not a slash: " + address );
		}
		
		final StringBuilder builder = new StringBuilder();
		final int end = address.length();

		String segment;
		int i;
		char c;
		
		for ( i = 1; i < end; ++i ) {
			switch ( c = address.charAt( i ) ) {
				case '/':
					segment = builder.toString();
					if ( segment.isEmpty() ) {
						throw new MalformedAddressException( "Empty path segment in: " + address );
					}
					builder.delete( 0, builder.length() );
					break;
				default:
					builder.append( c );
					break;
			}
		}
		segment = builder.toString();
		if ( segment.isEmpty() ) {
			throw new MalformedAddressException( "Empty path segment in: " + address );
		}
	}
	
	/**
	 * Checks whether the list of string segments contains a zero length segment.
	 * Deprecated! Use the optimistic approach whenever possible. There's absolutely no point
	 * in making the slow path faster if it affects the fast path.
	 * 
	 * @param address The list of segments.
	 * @throws MalformedAddressException 
	 */
	@Deprecated
	public void fromSegments( String ... address ) throws MalformedAddressException {
		if ( address.length == 0 ) {
			throw new MalformedAddressException( "Empty segments." );
		}
		for ( String segment : address ) {
			if ( segment == null ) {
				throw new MalformedAddressException( "Invalid null segment." );
			} else if ( segment.isEmpty() ) {
				throw new MalformedAddressException( "Invalid empty segment." );
			} else if ( segment.indexOf( '/' ) != -1 ) {
				throw new MalformedAddressException( "Segment containing a slash: " + segment );
			}
		}
	}
}
