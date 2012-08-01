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
import java.util.Iterator;

import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.softwarerepository.PackageMetadata;

/**
 * A Writer that outputs data from PackageMetadata instances.
 * 
 * @author Andrej Podzimek
 */
public final class PackageMetadataWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public PackageMetadataWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Standart short package metadata output.
	 * 
	 * @param metadata Package metadata to display.
	 * @throws IOException When it rains.
	 */
	public void sendLinePlain( PackageMetadata metadata ) throws IOException {
		sendCommon( metadata );
		builder().append( "|\n" );
		sendOut();
	}
	
	/**
	 * Extended long package metadata output.
	 * 
	 * @param metadata Package metadata to display.
	 * @throws IOException When it rains.
	 */
	public void sendLineXtend( PackageMetadata metadata ) throws IOException {
		sendCommon( metadata );
		builder()
		.append( "| " )
		.append( quotedLiteral( metadata.getHumanName() ) ).append( ' ' )
		.append( metadata.getBinaryIdentifier() ).append( ' ' )
		.append( quotedLiteral( metadata.getBuildConfiguration() ) ).append( ' ' )
		.append( metadata.getSourcePackageFilename() ).append( ' ' )
		.append( metadata.getDownloadURL() ).append( '\n' );
		sendOut();
	}
	
	/**
	 * Outputs common parts of the metadata.
	 * 
	 * @param metadata Package metadata to display.
	 */
	private void sendCommon( PackageMetadata metadata ) {
		Iterator< ? > it;
		
		builder()
		.append( metadata.getName() ).append( ' ' )
		.append( metadata.getType() ).append( ' ' )
		.append( metadata.getFilename() ).append( ' ' )
		.append( metadata.getDownloadDate() ).append( ' ' )
		.append( metadata.getSize() ).append( ' ' )
		.append( metadata.getVersion() ).append( " |" );
		it = metadata.getHardwarePlatforms().iterator();
		if ( it.hasNext() ) {
			builder().append( it.next() );
			while ( it.hasNext() ) {
				builder().append( ',' ).append( it.next() );
			}
		}
		builder().append( "| |" );
		it = metadata.getSoftwarePlatforms().iterator();
		if ( it.hasNext() ) {
			builder().append( it.next() );
			while ( it.hasNext() ) {
				builder().append( ',' ).append( it.next() );
			}
		}
		builder().append( "| |" );
		it = metadata.getProvidedInterfaces().iterator();
		if ( it.hasNext() ) {
			builder().append( it.next() );
			while ( it.hasNext() ) {
				builder().append( ',' ).append( it.next() );
			}
		}
	}
}
