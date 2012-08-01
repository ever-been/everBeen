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
package cz.cuni.mff.been.clinterface.modules;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import cz.cuni.mff.been.clinterface.CommandLineModule;
import cz.cuni.mff.been.clinterface.CommandLineRequest;
import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.clinterface.ModuleOutputException;

/**
 * An example implementation of a command line interface module. This module exercises
 * the native command line client's back channel API. It reads all the input flags and parameters
 * and pretty-prints them to STDOUT and STDERR. You can verify this by piping the output of
 * the command line client to {@code cat -n}. Lines sent to STDOUT will be numbered. But note that
 * the order of lines will change, for {@code cat} buffers its output.
 * 
 * @author Andrej Podzimek
 */
public class DummyModule extends CommandLineModule {
	
	/** Name of the current module for the lookup tables of {@link CommandLineModule} */
	public static final String MODULE_NAME = "dummy";
	
	/**
	 * This interface is a base class for a butterfly-switch bunch of classes. It's nothing worth
	 * reading, just a weird tric. ;-)
	 */
	private interface Sender {
		
		/**
		 * Changes the output stream.
		 */
		void swap();
		
		/**
		 * Sends a message to the curent output stream.
		 * 
		 * @param message The message to send, in a String form.
		 * @throws IOException When there is a network failure.
		 */
		void send( String message ) throws IOException;
		
		/**
		 * Line prefix getter.
		 * 
		 * @return What lines of the current output should begin with.
		 */
		String getPrefix();
	}
	
	/**
	 * This {@link Sender} sends a message to the standard output of the command line client.
	 */
	private final class OutSender implements Sender {
		
		@Override
		public void send( String message ) throws IOException {
			response.sendOut( message );
		}
		
		@Override
		public void swap() {
			sender = errSender;
		}
		
		@Override
		public String getPrefix() {
			return "OUT: ";
		}
	}

	/**
	 * This {@link Sender} sends a message to the error output of the command line client.
	 */
	private final class ErrSender implements Sender {
		
		@Override
		public void send( String message ) throws IOException {
			response.sendErr( message );
		}
		
		@Override
		public void swap() {
			sender = outSender;
		}
		
		@Override
		public String getPrefix() {
			return "ERR: ";
		}
	}
	
	/** An instance of {@link OutSender} used for output swapping. */
	private final OutSender outSender;
	
	/** An instance of {@link OutSender} used for output swapping. */
	private final ErrSender errSender;
	
	/** The currently used {@link Sender}. */
	private Sender sender;
	
	/** The sink to which all the output is sent. */
	private CommandLineResponse response;
	
	/**
	 * This constructor must be public so that reflection can access it! Think about it when
	 * implementing our own modules. It should only initialize fields that are meant to persist
	 * when the module instance is re-used. All other fields should be initialized in
	 * {@link #restoreState()}.
	 */
	public DummyModule() {																			// Needed for reflection
		outSender = new OutSender();
		errSender = new ErrSender();
	}

	@Override
	protected
	void handleAction( String action, CommandLineRequest request, CommandLineResponse response )
	throws ModuleOutputException {
		this.sender = outSender;
		this.response = response;
		
		try {
			sender.send( sender.getPrefix() + "Action name: " + action + '\n' );

			sender.send( sender.getPrefix() + "Writing all flags to stdout.\n" );
			for ( String flag : request.iterateFlags() ) {
				sender.send( sender.getPrefix() + '\t' + flag + '\n' );
			}
			sender.send( sender.getPrefix() + "Writing all parameters to stdout.\n" );
			for ( Entry< String, String > param : request.iterateParameters() ) {
				sender.send(
					sender.getPrefix() + '\t' + param.getKey() + "=\"" + param.getValue() + "\"\n"
				);
			}

			sender.swap();

			sender.send( sender.getPrefix() + "Writing all flags to stderr.\n" );
			for ( String flag : request.iterateFlags() ) {
				sender.send( sender.getPrefix() + '\t' + flag + '\n' );
			}
			sender.send( sender.getPrefix() + "Writing all parameters to stderr.\n" );
			for ( Entry< String, String > param : request.iterateParameters() ) {
				sender.send(
					sender.getPrefix() + '\t' + param.getKey() + "=\"" + param.getValue() + "\"\n"
				);
			}

			sender.send(
				sender.getPrefix() + "The same again, swapping the stream after each line.\n"
			);
			for ( String flag : request.iterateFlags() ) {
				sender.swap();
				sender.send( sender.getPrefix() + '\t' + flag + '\n' );
			}
			for ( Entry< String, String > param : request.iterateParameters() ) {
				sender.swap();
				sender.send(
					sender.getPrefix() + '\t' + param.getKey() + "=\"" + param.getValue() + "\"\n"
				);
			}

			sender.swap();
			sender.send( sender.getPrefix() + "Parameters to one output:\n" );
			for ( Entry< String, String > param : request.iterateParameters() ) {
				sender.send(
					sender.getPrefix() + '\t' + param.getKey() + "=\"" + param.getValue() + "\"\n"
				);
			}
			sender.swap();
			sender.send( sender.getPrefix() + "Flags to the other output.\n" );
			for ( String flag : request.iterateFlags() ) {
				sender.send( sender.getPrefix() + '\t' + flag + '\n' );
			}
			sender.swap();
			sender.send( sender.getPrefix() + "And parameters again:\n" );
			for ( Entry< String, String > param : request.iterateParameters() ) {
				sender.send(
					sender.getPrefix() + '\t' + param.getKey() + "=\"" + param.getValue() + "\"\n"
				);
			}
			sender.swap();
			sender.send( sender.getPrefix() + "And flags again:\n" );
			for ( String flag : request.iterateFlags() ) {
				sender.send( sender.getPrefix() + '\t' + flag + '\n' );
			}

			if ( request.hasBlob() ) {
				InputStream stream;
				long size;

				stream = request.getBlobStream();
				size = 0;
				while ( -1 != stream.read() ) {
					size += 1 + stream.skip( Long.MAX_VALUE );
				}
				response.sendOut( "Received a blob of length: " + size + "\n" );
			} else {
				response.sendOut( "No blob received.\n" );
			}
		} catch ( IOException exception ) {
			throw new ModuleOutputException( exception );
		}
	}

	@Override
	protected String getName() {
		return MODULE_NAME;
	}
	
	@Override
	protected void restoreState() {
		sender = outSender;
		response = null;
	}

	@Override
	protected String getActionsList() {
		return "All action names are accepted.\n";
	}
}
