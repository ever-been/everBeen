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
import cz.cuni.mff.been.hostmanager.database.HostGroup;

/**
 * A Writer that outputs data from HostGroup instances.
 * 
 * @author Andrej Podzimek
 */
public final class HostGroupWriter extends AbstractWriter {

	/**
	 * Initializes a new (and emtpy) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public HostGroupWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Short output without description.
	 * 
	 * @param group The group data to write.
	 * @throws IOException When it rains.
	 */
	public void sendLinePlain( HostGroup group ) throws IOException {
		sendCommon( group );
		builder().append( "|\n" );
		sendOut();
	}
	
	/**
	 * Long output with a description.
	 * 
	 * @param group The group data to write.
	 * @throws IOException When it rains.
	 */
	public void sendLineXtend( HostGroup group ) throws IOException {
		sendCommon( group );
		builder()
		.append( "| " )
		.append( quotedLiteral( group.getDescription() ) ).append( ' ' )
		.append( quotedLiteral( group.getMetadata() ) ).append( '\n' );
		sendOut();
	}

	/**
	 * Writes common parts of the host group.
	 * 
	 * @param group The group data to write.
	 */
	private void sendCommon( HostGroup group ) {
		Iterator< String > it;
		
		it = group.getAllHosts().iterator();
		builder()
		.append( quotedLiteral( group.getName() ) ).append( " |" );
		if ( it.hasNext() ) {
			builder().append( it.next() );
			while ( it.hasNext() ) {
				builder().append( ',' ).append( it.next() );
			}
		}	
	}
}
