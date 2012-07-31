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
import java.text.DateFormat;
import java.util.Date;

import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.taskmanager.data.ContextEntry;

/**
 * A Writer that outputs data from ContextEntry instances.
 * 
 * @author Andrej Podzimek
 */
public final class ContextEntryWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public ContextEntryWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Outputs data from a context entry with a description.
	 * 
	 * @param context The context entry to read from.
	 * @throws IOException When it rains.
	 */
	public void sendLineDescr( ContextEntry context ) throws IOException {
		sendCommon( context );
		builder()
		.append( ' ' )
		.append( quotedLiteral( context.getContextDescription() ) ).append( '\n' );
		sendOut();
	}
	
	/**
	 * Outputs data from a context entry in a short form with no description.
	 * 
	 * @param context The context entry to read from.
	 * @throws IOException When it rains.
	 */
	public void sendLinePlain( ContextEntry context ) throws IOException {
		sendCommon( context );
		builder().append( '\n' );
		sendOut();
	}
	
	/**
	 * Outputs common parts of the context entry.
	 * 
	 * @param context The context entry to read from.
	 */
	private void sendCommon( ContextEntry context ) {
		DateFormat dateFormat;
		
		dateFormat = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT );
		builder()
		.append( context.getContextName() ).append( ' ' )
		.append( context.getContextId() ).append( ' ' )
		.append( dateFormat.format( new Date( context.getCurentTime() ) ) );
	}
}
