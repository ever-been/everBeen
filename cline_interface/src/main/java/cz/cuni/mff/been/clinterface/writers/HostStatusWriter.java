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
import cz.cuni.mff.been.hostmanager.load.HostStatus;
import cz.cuni.mff.been.hostmanager.load.LoadSample;

/**
 * A Writer that outputs data from HostStatus and LoadSample instances.
 * 
 * @author Andrej Podzimek
 */
public final class HostStatusWriter extends AbstractWriter {
	
	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public HostStatusWriter( CommandLineResponse response ) {
		super( response );
	}
	
	/**
	 * Outputs one line of host status and load information.
	 * 
	 * @param hostName Name of the host.
	 * @param status Status information for the host.
	 * @param sample A load sample for the host.
	 * @throws IOException When it rains.
	 */
	public void sendLine(
		String hostName,
		HostStatus status,
		LoadSample sample
	) throws IOException {
		long[] read, write;
		int[] recv, send;
		short[] aux;

		builder()
		.append( hostName ).append( ' ' )
		.append( sample.getTimeStamp() ).append( ' ' )
		.append( literal( status ) ).append( ' ' );
		
		builder().append( '|' );
		aux = sample.getProcessorUsage();
		if ( aux.length > 0 ) {
			builder().append( aux[ 0 ] );
			for ( int idx = 1; idx < aux.length; ++idx ) {
				builder().append( ',' ).append( aux[ idx ] );
			}
		}
		builder().append( '|' ).append( ' ' );
		
		builder()
		.append( sample.getMemoryFree() ).append( ' ' )
		.append( sample.getProcessCount() ).append( ' ' )
		.append( sample.getProcessQueueLength() ).append( ' ' );
		
		builder().append( '|' );
		read = sample.getDiskReadBytesPerSecond();
		write = sample.getDiskWriteBytesPerSecond();
		if ( read.length > 0 ) {
			builder().append( read[ 0 ] ).append( '/' ).append( write[ 0 ] );
			for ( int idx = 1; idx < read.length; ++idx ) {
				builder()
				.append( ',' )
				.append( read[ idx ] )
				.append( '/' )
				.append( write[ idx ] );
			}
		}
		builder().append( '|' ).append( ' ' );
		
		builder().append( '|' );
		recv = sample.getNetworkBytesReceivedPerSecond();
		send = sample.getNetworkBytesSentPerSecond();
		if ( recv.length > 0 ) {
			builder().append( recv[ 0 ] ).append( '/' ).append( send[ 0 ] );
			for ( int idx = 1; idx < recv.length; ++idx ) {
				builder()
				.append( ',' )
				.append( recv[ idx ] )
				.append( '/' )
				.append( send[ idx ] );
			}
		}
		builder().append( '|' ).append( '\n' );
		sendOut();
	}
}
