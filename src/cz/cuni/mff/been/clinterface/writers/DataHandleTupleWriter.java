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
import java.io.Serializable;
import java.util.Map.Entry;

import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.modules.ResultsModule.Errors;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * A Writer that outputs data from data handle tuples.
 * 
 * @author Andrej Podzimek
 */
public final class DataHandleTupleWriter extends AbstractWriter {
	
	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public DataHandleTupleWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Writes data from a data handle tuple without column names.
	 * 
	 * @param tuple The tuple to read from.
	 * @throws ModuleSpecificException When data casting fails.
	 * @throws IOException When it rains.
	 */
	public void sendLinePlain( DataHandleTuple tuple ) throws ModuleSpecificException, IOException {
		Entry< String, DataHandle > rentry;
		
		rentry = null;
		builder().append( tuple.getSerial() );
		try {
			for ( Entry< String, DataHandle > entry : tuple.getEntries() ) {
				rentry = entry;
				builder().append( ' ' );
				writeItemPlain( entry.getValue() );
			}
		} catch ( DataHandleException exception ) {
			throw new ModuleSpecificException(
				Errors.INTG_TYPE,
				" (" + ( null == rentry ? "null" : rentry.getKey() ) + ')',							// Null will never occur.
				exception
			);				
		}
		builder().append( '\n' );
		sendOut();
	}
	
	/**
	 * Writes data from a data handle tuple with column names.
	 * 
	 * @param tuple The tuple to read from.
	 * @throws ModuleSpecificException When data casting fails.
	 * @throws IOException When it rains.
	 */
	public void sendLineNames( DataHandleTuple tuple ) throws ModuleSpecificException, IOException {
		Entry< String, DataHandle > rentry;
		
		rentry = null;
		builder().append( "serial:" ).append( tuple.getSerial() );
		try {
			for ( Entry< String, DataHandle > entry : tuple.getEntries() ) {
				rentry = entry;
				builder().append( ' ' );
				writeItemNames( entry.getKey(), entry.getValue() );
			}
		} catch ( DataHandleException exception ) {
			throw new ModuleSpecificException(
				Errors.INTG_TYPE,
				" (" + ( null == rentry ? "null" : rentry.getKey() ) + ')',
				exception
			);				
		}
		builder().append( '\n' );
		sendOut();
	}
	
	/**
	 * Writes a single data item.
	 * 
	 * @param handle The data handle to read from.
	 * @throws DataHandleException When data casting fails.
	 */
	private void writeItemPlain( DataHandle handle ) throws DataHandleException {
		switch ( handle.getType() ) {
			case SMALL_BINARY:
				byte[] value;
				
				value = handle.getValue( byte[].class );
				if ( null == value ) {
					builder().append( literal( null ) );
				} else {
					builder().append( "[blob " ).append( value.length ).append( " B]" );				
				}
				break;
			case SERIALIZABLE:
				builder()
				.append( "[serializable " )
				.append( safeLiteral( handle.getValue( Serializable.class ) ) )
				.append( ']' );
				break;
			case STRING:
				builder()
				.append( quotedLiteral( handle.getValue( String.class ) ) );
				break;
			default:
				builder()
				.append( literal( handle.getValue( handle.getType().getJavaType() ) ) );
				break;
		}
	}

	/**
	 * Writes a single data item.
	 * 
	 * @param name Name of the column.
	 * @param handle The data handle to read from.
	 * @throws DataHandleException When data casting fails.
	 */
	private void writeItemNames( String name, DataHandle handle ) throws DataHandleException {
		switch ( handle.getType() ) {
			case SMALL_BINARY:
				byte[] value;
				
				value = handle.getValue( byte[].class );
				builder().append( name ).append( ':' );
				if ( null == value ) {
					builder().append( literal( null ) );
				} else {
					builder().append( "[blob " ).append( value.length ).append( " B]" );
				}
				break;
			case SERIALIZABLE:
				builder()
				.append( name ).append( ':' )
				.append( "[serializable " )
				.append( safeLiteral( handle.getValue( Serializable.class ) ) )
				.append( ']' );
				break;
			case STRING:
				builder()
				.append( name ).append( ':' )
				.append( quotedLiteral( handle.getValue( String.class ) ) );
				break;
			default:
				builder()
				.append( name ).append( ':' )
				.append( literal( handle.getValue( handle.getType().getJavaType() ) ) );
				break;
		}
	}
}
