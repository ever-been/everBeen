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
import cz.cuni.mff.been.clinterface.adapters.BenchmarkManagerAbstractAdapter.ModuleInfo;

/**
 * A writer that outputs data from ModuleInfo instances.
 * 
 * @author Andrej Podzimek
 */
public final class ModuleInfoWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public ModuleInfoWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Outputs data from a ModuleInfo entry.
	 * 
	 * @param info The module info to read from.
	 * @throws IOException When it rains.
	 */
	public void sendLine( ModuleInfo info ) throws IOException {
		builder()
		.append( info.getName() ).append( ' ' )
		.append( info.getVersion() ).append( ' ' )
		.append( info.getPackageName() ).append( '\n' );
		sendOut();
	}
}
