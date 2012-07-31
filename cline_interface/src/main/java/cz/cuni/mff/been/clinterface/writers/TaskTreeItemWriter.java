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
package cz.cuni.mff.been.clinterface.writers;

import java.io.IOException;

import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.taskmanager.tasktree.IllegalAddressException;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeAddress;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeFlag;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeQuery;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeRecord;
import cz.cuni.mff.been.taskmanager.tasktree.TreeFlagValue;

/**
 * A Writer that outputs data from TaskTreeItem instances.
 * 
 * @author Andrej Podzimek
 */
public final class TaskTreeItemWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public TaskTreeItemWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Outputs data from a task tree item with flag values.
	 * 
	 * @param query The task tree interface to read from.
	 * @param address The address to start at.
	 * @throws IOException When it rains.
	 */
	public void sendLineFlagsSimple( TaskTreeQuery query, TaskTreeAddress address )
	throws IOException {
		TaskTreeRecord record;
		
		try {
			record = query.getRecordAt( address, true, true, true );
		} catch ( IllegalAddressException exception ) {
			Task.getTaskHandle().logWarning(
				"Branch unavailable: " + assemblePath( query.getPathAt( address ) )
			);
			return;
		}	
		switch ( record.getType() ) {
			case LEAF:
				TaskEntry entry = record.getTask();
				builder()
				.append( "L " )
				.append( generateName( record ) ).append( ' ' )
				.append( entry.getTaskId() ).append( ' ' )
				.append( literal( entry.getState() ) ).append( ' ' )
				.append( literal( entry.getHostName() ) ).append( " |" );
				sendFlags( record.getFlags() );
				builder().append( "|\n" );
				sendOut();
				break;
			case NODE:
				builder()
				.append( "N " )
				.append( generateName( record ) ).append( " |" );
				sendFlags( record.getFlags() );
				builder().append( "|\n" );
				for ( TaskTreeAddress childAddress : record.getChildren() ) {
					sendLineFlagsSimple2( query, childAddress );
				}
				sendOut();
				break;
		}
	}
	
	/**
	 * Outputs data from a task tree item in a short form with no flag values.
	 * 
	 * @param query The task tree interface to read from.
	 * @param address The address to start at.
	 * @throws IOException When it rains.
	 */
	public void sendLinePlainSimple( TaskTreeQuery query, TaskTreeAddress address )
	throws IOException {
		TaskTreeRecord record;
		
		try {
			record = query.getRecordAt( address, true, true, false );
		} catch ( IllegalAddressException exception ) {
			Task.getTaskHandle().logWarning(
				"Branch unavailable: " + assemblePath( query.getPathAt( address ) )
			);
			return;
		}
		
		switch ( record.getType() ) {
			case LEAF:
				TaskEntry entry = record.getTask();
				builder()
				.append( "L " )
				.append( generateName( record ) ).append( ' ' )
				.append( entry.getTaskId() ).append( ' ' )
				.append( literal( entry.getState() ) ).append( ' ' )
				.append( literal( entry.getHostName() ) ).append( '\n' );
				sendOut();
				break;
			case NODE:
				builder()
				.append( "N " )
				.append( generateName( record ) ).append( '\n' );
				for ( TaskTreeAddress childAddress : record.getChildren() ) {
					sendLinePlainSimple2( query, childAddress );
				}
				sendOut();
				break;
		}
	}
	
	/**
	 * Outputs data from a task tree item with flag values recursively.
	 * 
	 * @param query The task tree interface to read from.
	 * @param address The address to start at.
	 * @param level Tree depth (and text indentation) level.
	 * @throws IOException When it rains.
	 */
	public void sendLineFlagsRecursive( TaskTreeQuery query, TaskTreeAddress address, int level )
	throws IOException {
		TaskTreeRecord record;
		
		try {
			record = query.getRecordAt( address, true, true, true );
		} catch ( IllegalAddressException exception ) {
			Task.getTaskHandle().logWarning(
				"Branch unavailable: " + assemblePath( query.getPathAt( address ) )
			);
			return;
		}	
		switch ( record.getType() ) {
			case LEAF:
				TaskEntry entry = record.getTask();
				builder()
				.append( "L " );
				for ( int i = 0; i < level; ++i ) {
					builder().append( '\t' );
				}
				builder()
				.append( generateName( record ) ).append( ' ' )
				.append( entry.getTaskId() ).append( ' ' )
				.append( literal( entry.getState() ) ).append( ' ' )
				.append( literal( entry.getHostName() ) ).append( " |" );
				sendFlags( record.getFlags() );
				builder().append( "|\n" );
				sendOut();
				break;
			case NODE:
				builder()
				.append( "N " );
				for ( int i = 0; i < level; ++i ) {
					builder().append( '\t' );
				}
				builder()
				.append( generateName( record ) ).append( " |" );
				sendFlags( record.getFlags() );
				builder().append( "|\n" );
				sendOut();
				for ( TaskTreeAddress childAddress : record.getChildren() ) {
					sendLineFlagsRecursive( query, childAddress, 1 + level );
				}
				break;
		}
	}
	
	/**
	 * Outputs data from a task tree item in a short form with no flag values recursively.
	 * 
	 * @param query The task tree interface to read from.
	 * @param address The address to start at.
	 * @param level Tree depth (and text indentation) level.
	 * @throws IOException When it rains.
	 */
	public void sendLinePlainRecursive( TaskTreeQuery query, TaskTreeAddress address, int level )
	throws IOException {
		TaskTreeRecord record;
		
		try {
			record = query.getRecordAt( address, true, true, false );
		} catch ( IllegalAddressException exception ) {
			Task.getTaskHandle().logWarning(
				"Branch unavailable: " + assemblePath( query.getPathAt( address ) )
			);
			return;
		}
		
		switch ( record.getType() ) {
			case LEAF:
				TaskEntry entry = record.getTask();
				builder()
				.append( "L " );
				for ( int i = 0; i < level; ++i ) {
					builder().append( '\t' );
				}
				builder()
				.append( generateName( record ) ).append( ' ' )
				.append( entry.getTaskId() ).append( ' ' )
				.append( literal( entry.getState() ) ).append( ' ' )
				.append( literal( entry.getHostName() ) ).append( '\n' );
				sendOut();
				break;
			case NODE:
				builder()
				.append( "N " );
				for ( int i = 0; i < level; ++i ) {
					builder().append( '\t' );
				}			
				builder()
				.append( generateName( record ) ).append( '\n' );
				sendOut();
				for ( TaskTreeAddress childAddress : record.getChildren() ) {
					sendLinePlainRecursive( query, childAddress, 1 + level );
				}
				break;
		}
	}

	/**
	 * Outputs data from a task tree item with flag values. No recursion, no sendOut().
	 * 
	 * @param query The task tree interface to read from.
	 * @param address The address to start at.
	 * @throws IOException When it rains.
	 */
	private void sendLineFlagsSimple2( TaskTreeQuery query, TaskTreeAddress address )
	throws IOException {
		TaskTreeRecord record;
		
		try {
			record = query.getRecordAt( address, true, true, true );
		} catch ( IllegalAddressException exception ) {
			Task.getTaskHandle().logWarning(
				"Branch unavailable: " + assemblePath( query.getPathAt( address ) )
			);
			return;
		}	
		switch ( record.getType() ) {
			case LEAF:
				TaskEntry entry = record.getTask();
				builder()
				.append( "L \t" )
				.append( generateName( record ) ).append( ' ' )
				.append( entry.getTaskId() ).append( ' ' )
				.append( literal( entry.getState() ) ).append( ' ' )
				.append( literal( entry.getHostName() ) ).append( " |" );
				sendFlags( record.getFlags() );
				builder().append( "|\n" );
				break;
			case NODE:
				builder()
				.append( "N \t" )
				.append( generateName( record ) ).append( " |" );
				sendFlags( record.getFlags() );
				builder().append( "|\n" );
				break;
		}
	}

	/**
	 * Outputs data from a task tree item in a short form with no flag values. No recursion,
	 * no sendOut().
	 * 
	 * @param query The task tree interface to read from.
	 * @param address The address to start at.
	 * @throws IOException When it rains.
	 */
	public void sendLinePlainSimple2( TaskTreeQuery query, TaskTreeAddress address )
	throws IOException {
		TaskTreeRecord record;
		
		try {
			record = query.getRecordAt( address, true, true, false );
		} catch ( IllegalAddressException exception ) {
			Task.getTaskHandle().logWarning(
				"Branch unavailable: " + assemblePath( query.getPathAt( address ) )
			);
			return;
		}
		
		switch ( record.getType() ) {
			case LEAF:
				TaskEntry entry = record.getTask();
				builder()
				.append( "L \t" )
				.append( generateName( record ) ).append( ' ' )
				.append( entry.getTaskId() ).append( ' ' )
				.append( literal( entry.getState() ) ).append( ' ' )
				.append( literal( entry.getHostName() ) ).append( '\n' );
				break;
			case NODE:
				builder()
				.append( "N \t" )
				.append( generateName( record ) ).append( '\n' );
				break;
		}
	}

	/**
	 * Outputs flags and their values.
	 * 
	 * @param flags Array of flags and their values.
	 */
	private void sendFlags( Pair< TaskTreeFlag, TreeFlagValue >[] flags ) {
		int i;
		
		TaskTreeFlag flag;
		TreeFlagValue value;
		
		i = 0;
		if ( flags.length > 0 ) {
			flag = flags[ i ].getKey();
			value = flags[ i++ ].getValue();
			builder()
			.append( literal( flag ) ).append( '=' )
			.append( literal( value.getOrdinal() ) ).append( '-' )
			.append( value.getOrdinal().ordinal() ).append( "-\"" )
			.append( quotedLiteral( value.getMessage() ) ).append( '"' );							
			while ( i < flags.length ) {
				flag = flags[ i ].getKey();
				value = flags[ i++ ].getValue();
				builder()
				.append( ',' )
				.append( literal( flag ) ).append( '=' )
				.append( literal( value.getOrdinal() ) ).append( '-' )
				.append( value.getOrdinal().ordinal() ).append( "-\"" )
				.append( quotedLiteral( value.getMessage() ) ).append( '"' );
			}
		}
	}
	
	/**
	 * Node name getter.
	 * 
	 * @param record A TaskTreeRecord with node information.
	 * @return Name of the node, which is the rightmost path segment.
	 */
	private static String generateName( TaskTreeRecord record ) {
		String[] segments;
		
		segments = record.getPathSegments();
		if ( 0 == segments.length ) {
			return "<ROOT>";
		} else {
			return quotedLiteral( segments[ segments.length - 1 ] );
		}
	}
	
	/**
	 * Assembles path segments into a path string.
	 * 
	 * @param segments An array of path segments.
	 * @return The path string consisting of the supplied segments.
	 */
	private static String assemblePath( String ... segments ) {
		StringBuilder result = new StringBuilder();
		for ( String segment : segments ) {
			result.append( '/' ).append( segment );
		}
		return result.toString();
	}
}
