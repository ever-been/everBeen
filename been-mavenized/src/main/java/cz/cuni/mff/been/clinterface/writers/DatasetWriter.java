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
import java.util.Collection;
import java.util.Iterator;

import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;

/**
 * A Writer that outputs data from DatasetDescriptor instances.
 * 
 * @author Andrej Podzimek
 */
public final class DatasetWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public DatasetWriter( CommandLineResponse response ) {
		super( response );
	}
	
	/**
	 * Outputs information about one descriptor.
	 * 
	 * @param dataset The dataset metadata to output.
	 * @throws IOException When it rains.
	 */
	public void sendLine( String name, DatasetDescriptor dataset ) throws IOException {
		Iterator< String > it;
		Collection< String > keys;
		String tag;
		
		builder().append( name ).append( " |" );
		it = dataset.tags().iterator();		
		if ( it.hasNext() ) {
			keys = dataset.idTags();
			tag = it.next();
			if ( keys.contains( tag ) ) {
				builder().append( '*' );
			}
			builder()
			.append( tag ).append( ':' ).append( literal( dataset.get( tag ) ) );
			while ( it.hasNext() ) {
				tag = it.next();
				builder().append( ',' );
				if ( keys.contains( tag ) ) {
					builder().append( '*' );
				}
				builder()
				.append( tag ).append( ':' ).append( literal( dataset.get( tag ) ) );
			}
		}
		builder().append( "|\n" );
		sendOut();
	}
}
