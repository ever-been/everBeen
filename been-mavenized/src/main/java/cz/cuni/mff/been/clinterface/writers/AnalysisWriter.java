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

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMEvaluator;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMModule;
import cz.cuni.mff.been.clinterface.CommandLineResponse;

/**
 * A writer that outputs data from Analysis instances.
 * 
 * @author Andrej Podzimek
 */
public final class AnalysisWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public AnalysisWriter( CommandLineResponse response ) {
		super( response );
	}
	
	/**
	 * Outputs data from an analysis entry with a description.
	 * 
	 * @param analysis The analysis entry to read from.
	 * @throws IOException When it rains.
	 */
	public void sendLineDescr( Analysis analysis ) throws IOException {
		sendCommon( analysis );
		builder()
		.append( ' ' )
		.append( quotedLiteral( analysis.getDescription() ) ).append( '\n' );
		sendOut();
	}
	
	/**
	 * Outputs data from an analysis entry in a short form with no description.
	 * 
	 * @param analysis The analysis entry to read from.
	 * @throws IOException When it rains.
	 */
	public void sendLinePlain( Analysis analysis ) throws IOException {
		sendCommon( analysis );
		builder().append( '\n' );
		sendOut();
	}
	
	/**
	 * Outputs the common parts of the analysis entry.
	 * 
	 * @param analysis The analysis entry to read from.
	 */
	private void sendCommon( Analysis analysis ) {
		BMModule module;
		Iterator< BMEvaluator > it;
		
		builder()
		.append( analysis.getName() ).append( ' ' )
		.append( literal( analysis.getState() ) ).append( ' ' );
		module = analysis.getGenerator();
		builder()
		.append( module.getName() ).append( ':' ).append( module.getVersion() )
		.append( " |" );
		it = analysis.getEvaluators().iterator();
		if ( it.hasNext() ) {
			module = it.next();
			builder()
			.append( module.getName() ).append( ':' ).append( module.getVersion() );
			while ( it.hasNext() ) {
				module = it.next();
				builder()
				.append( ',' )
				.append( module.getName() ).append( ':' ).append( module.getVersion() );
			}
		}
		builder()
		.append( "| " )
		.append( literal( analysis.getRunPeriod() ) ).append( ' ' )
		.append( literal( analysis.getRunCount() ) ).append( ' ' )
		.append( literal( analysis.getLastTime() ) );		
	}
}
