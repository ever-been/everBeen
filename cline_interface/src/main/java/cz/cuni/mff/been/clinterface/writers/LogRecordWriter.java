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
import cz.cuni.mff.been.logging.LogRecord;

/**
 * A Writer that outputs data from LogRecord instances.
 * 
 * @author Andrej Podzimek
 */
public final class LogRecordWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public LogRecordWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Outputs a standard one-line log message.
	 * 
	 * @param record The log record instance to read from.
	 * @throws IOException When it rains.
	 */
	public void sendLine( LogRecord record ) throws IOException {
		builder()
		.append( record.getTimestamp() ).append( ' ' )
		.append( record.getHostname() ).append( ' ' )
		.append( record.getContext() ).append( ' ' )
		.append( record.getTaskID() ).append( ' ' )
		.append( record.getLevel() ).append( ' ' )
		.append( record.getMessage() ).append( '\n' );
		sendOut();
	}
	
	/**
	 * Outputs a standard one-line log message prefixed by a line number.
	 * 
	 * @param number Line number to print out.
	 * @param record The log record to read from.
	 * @throws IOException When it rains.
	 */
	public void sendLine( long number, LogRecord record ) throws IOException {
		builder()
		.append( number ).append( ' ' );
		sendLine( record );
	}
}
