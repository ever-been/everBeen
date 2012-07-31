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
import cz.cuni.mff.been.resultsrepositoryng.RRTrigger;

/**
 * A Writer that outputs data from RRTrigger instances.
 * 
 * @author Andrej Podzimek
 *
 */
public final class TriggerWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the writ operations will be relayed.
	 */
	public TriggerWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Outputs information about a Results Repository trigger.
	 * 
	 * @param trigger The trigger metadata to output.
	 * @throws IOException When it rains.
	 */
	public void sendLine( RRTrigger trigger ) throws IOException {
		builder()
		.append( trigger.getAnalysis() ).append( ' ' )
		.append( trigger.getDataset() ).append( ' ' )
		.append( trigger.getEvaluator() ).append( ' ' )
		.append( trigger.getLastProcessedSerial() ).append( ' ' )
		.append( literal( trigger.getId() ) ).append( '\n' );
		sendOut();
	}	
}
