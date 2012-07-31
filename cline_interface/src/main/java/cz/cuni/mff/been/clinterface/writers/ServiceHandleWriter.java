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
import cz.cuni.mff.been.clinterface.adapters.ServiceHandle;

/**
 * A Writer that outputs data from ServiceHandle instances.
 * 
 * @author Andrej Podzimek
 */
public final class ServiceHandleWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public ServiceHandleWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Outputs a line of information about a BEEN's core service.
	 * 
	 * @param handle The service handle (enum member) of the (singleton) service.
	 * @throws IOException When it rains.
	 */
	public void sendLine( ServiceHandle handle ) throws IOException {
		String status;
		
		status = null == handle.getStatus() ? literal( null ) : handle.getStatus().name();
		builder()
		.append( handle.getName() ).append( ' ' )
		.append( handle.getTid() ).append( ' ' )
		.append( literal( handle.getHost() ) ).append( ' ' )
		.append( status ).append( '\n' );
		sendOut();
	}
}
