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
package cz.cuni.mff.been.clinterface;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * This class represents a sink to which response streams can be sent. Currently, standard
 * output and error output are supported, both connected to the command line client's output.
 * There is a similar mechanism in the web interface, but it is built closely around the Apache
 * stuff: It uses a utility class {@code Params} to verify correctness of input parameters,
 * then sets tome data to to the {@code Page} singleton and uses this singleton to write JSP
 * templates based on the data. Unlike {@code HttpServletResponse}, this class does not use
 * any templates. Instead, it provides an API useful for writing either strings or raw data
 * to a terminal output. Templating could be introduced by subclassing this class.
 * 
 * @author Andrej Podzimek
 */
public class CommandLineResponse {
	
	/**
	 * This is a list of all possible output senders. This is a butterfly mechanism again. ;-)
	 * It is similar to {@code CommandLineRequest.State} and its subclasses, but implemented
	 * the static way this time. 
	 */
	private enum Sender {
		
		/** This item uses the output stream and switches to ERR if error stream is requested.*/
		OUT( new char[] { '\0', '\1' }, new byte[] { 0, 1 } ) {
			
			@Override
			void sendOut( CommandLineResponse response, String message ) throws IOException {
				response.buffered.write( message );
			}

			@Override
			void sendErr( CommandLineResponse response, String message ) throws IOException {
				response.sender = ERR;
				response.buffered.write( ERR.switchTo );
				ERR.sendErr( response, message );
			}

			@Override
			void sendRawOut( CommandLineResponse response, byte[] ... messages )
			throws IOException {
				response.rawOut.write( concat( messages ) );
			}

			@Override
			void sendRawErr( CommandLineResponse response, byte[] ... messages )
			throws IOException {
				response.sender = ERR;
				response.rawOut.write( concat( ERR.switchRawTo, messages) );
			}
		},
		
		/** This item uses the error stream and switches to OUT if output stream is requested. */
		ERR( new char[] { '\0', '\2' }, new byte[] { 0, 2 } ) {

			@Override
			void sendOut( CommandLineResponse response, String message ) throws IOException {
				response.sender = OUT;
				response.buffered.write( OUT.switchTo );
				OUT.sendOut( response, message );
			}

			@Override
			void sendErr( CommandLineResponse response, String message ) throws IOException {
				response.buffered.write( message );
			}

			@Override
			void sendRawOut( CommandLineResponse response, byte[] ... messages )
			throws IOException {
				response.sender = OUT;
				response.rawOut.write( concat( OUT.switchRawTo, messages ) );
			}

			@Override
			void sendRawErr( CommandLineResponse response, byte[] ...  messages )
			throws IOException {
				response.rawOut.write( concat( messages ) );
			}
		};
		
		/** The switch escape sequence in {@code char[]} format. */
		private final char[] switchTo;
		
		/** The switch escape sequence in {@code byte[]} format. */
		private final byte[] switchRawTo;
		
		/**
		 * This initializer set stream switching escape sequences the command line client
		 * will recognize.
		 * 
		 * @param switchTo Stream switching sequence in {@code char[]} format.
		 * @param switchRawTo Stream switching sequence in {@code byte[]} format.
		 */
		private Sender( char[] switchTo, byte[] switchRawTo ) {
			this.switchTo = switchTo;
			this.switchRawTo = switchRawTo;
		}
		
		/**
		 * Sends a string to the standard output of the command line client.
		 * 
		 * @param response Circumvents the fact that nested enums are static by default.
		 * @param message The string to be sent (and converted using a suitable charset).
		 * @throws IOException When a network error occurs.
		 */
		abstract void sendOut( CommandLineResponse response, String message ) throws IOException;
		
		/**
		 * Sends a string to the error output of the command line client.
		 * 
		 * @param response Circumvents the fact that nested enums are static by default.
		 * @param message The string to be sent (and converted using a suitable charset).
		 * @throws IOException When a network error occurs.
		 */
		abstract void sendErr( CommandLineResponse response, String message ) throws IOException;
		
		/**
		 * Sends a raw sequence of bytes to the standard output of the command line client.
		 *  
		 * @param response Circumvents the fact that nested enums are static by default.
		 * @param messages Byte arrays that will be concatenated and sent in one call.
		 * @throws IOException When a network error occurs.
		 */
		abstract void sendRawOut( CommandLineResponse response, byte[] ... messages )
		throws IOException;
		
		/**
		 * Sends a raw sequence of bytes to the error output of the command line client.
		 *  
		 * @param response Circumvents the fact that nested enums are static by default.
		 * @param messages Byte arrays that will be concatenated and sent in one call.
		 * @throws IOException When a network error occurs.
		 */
		abstract void sendRawErr( CommandLineResponse response, byte[] ... messages )
		throws IOException;		
	}
	
	/** A somewhat short byte array for technical reasons. */
	private static final byte[] ZERO_BYTES = new byte[ 0 ];
	
	/** A buffered access to the underlying network socket. */
	private final BufferedWriter buffered;
	
	/** An unbuffered byte-wise access to the underlying network socket. */
	private final OutputStream rawOut;

	/** The current {@link Sender} used for data transfers. */
	private Sender sender;

	/**
	 * This initializes a new instance and its data structures.
	 * 
	 * @param outStream The stream all messages should be sent to.
	 * @param charset Character set used in the current stream or connection.
	 */
	CommandLineResponse( OutputStream outStream, Charset charset ) {
		rawOut = outStream;
		buffered = new BufferedWriter( new OutputStreamWriter( outStream, charset ) );
		sender = Sender.OUT;
	}
	
	/**
	 * Sends a string to the standard output of the command line client.
	 * 
	 * @param message The string to be sent (and converted using a suitable charset).
	 * @throws IOException When a network error occurs.
	 */
	public void sendOut( String message ) throws IOException {
		sender.sendOut( this, message );
	}
	
	/**
	 * Sends a string to the error output of the command line client.
	 * 
	 * @param message The string to be sent (and converted using a suitable charset).
	 * @throws IOException When a network error occurs.
	 */
	public void sendErr( String message ) throws IOException {
		sender.sendErr( this, message );
	}
	
	/**
	 * Sends a raw sequence of bytes to the standard output of the command line client.
	 * Make <b>sure</b> you call {@code flush()} before this method if {@code sendOut()}
	 * or {@code sendErr()} had been called before!
	 *  
	 * @param messages Byte arrays that will be concatenated and sent in one call.
	 * @throws IOException When a network error occurs.
	 */
	public void sendRawOut( byte[] ... messages ) throws IOException {
		sender.sendRawOut( this, messages );
	}
	
	/**
	 * Sends a raw sequence of bytes to the standard output of the command line client.
	 * Make <b>sure</b> you call {@code flush()} before this method if {@code sendOut()}
	 * or {@code sendErr()} had been called before!
	 * 
	 * @param message A byte array to send to the standard output.
	 * @throws IOException When a network error occurs.
	 */
	public void sendRawOut( byte ... message ) throws IOException {
		sender.sendRawOut( this, message );
	}
	
	/**
	 * Sends a raw sequence of bytes to the error output of the command line client.
	 * Make <b>sure</b> you call {@code flush()} before this method if {@code sendOut()}
	 * or {@code sendErr()} had been called before!
	 *  
	 * @param messages Byte arrays that will be concatenated and sent in one call.
	 * @throws IOException When a network error occurs.
	 */
	public void sendRawErr( byte[] ... messages ) throws IOException {
		sender.sendRawErr( this, messages );
	}
	
	/**
	 * Sends a raw sequence of bytes to the error output of the command line client.
	 * Make <b>sure</b> you call {@code flush()} before this method if {@code sendOut()}
	 * or {@code sendErr()} had been called before!
	 *  
	 * @param message A byte array to send to the error output.
	 * @throws IOException When a network error occurs.
	 */
	public void sendRawErr( byte ... message ) throws IOException {
		sender.sendRawErr( this, message );
	}
	
	/**
	 * Sends a raw sequence of bytes to command line client without taking care of output stream
	 * switching. This is mainly useful to send escape sequences where current output stream
	 * settings do not matter at all. This is a low-level call and should not be used
	 * by standard module implementations.
	 * Make <b>sure</b> you call {@code flush()} before this method if {@code sendOut()}
	 * or {@code sendErr()} had been called before!
	 *  
	 * @param messages Byte arrays that will be concatenated and sent in one call.
	 * @throws IOException When a network error occurs.
	 */
	void sendRawAnywhere( byte[] ... messages ) throws IOException {
		rawOut.write( concat( messages ) );
	}
	
	/**
	 * Sends a raw sequence of bytes to command line client without taking care of output stream
	 * switching. This is mainly useful to send escape sequences where current output stream
	 * settings do not matter at all. This is a low-level call and should not be used
	 * by standard module implementations.
	 * Make <b>sure</b> you call {@code flush()} before this method if {@code sendOut()}
	 * or {@code sendErr()} had been called before!
	 *  
	 * @param message A byte array to transmit.
	 * @throws IOException When a network error occurs.
	 */
	void sendRawAnywhere( byte ... message ) throws IOException {
		rawOut.write( message );
	}
	
	/**
	 * Flushes the buffered stream controlled by {@code sendOut()} and {@code sendErr()}.
	 * This method will be invoked automatically by the thread that created this response,
	 * so the module code does not need to care about it. However, it must be called before
	 * {@code sendRawOut()}, {@code sendRawErr()} or {@code sendRawAnywhere()} in case if
	 * (and probably <i>only if</i>) buffered output methods ({@code sendOut()} or
	 * {@code sendErr()}) had been called immediately before.
	 * 
	 * @throws IOException When a network error occurs.
	 */
	public void flush() throws IOException {
		buffered.flush();
	}
	
	/**
	 * Standard output stream getter. CAUTION! A flush is necessary.
	 * 
	 * @return A stream-like wrapper of this response's standard output.
	 */
	public OutputStream getStandardOutputStream() {
		return new ResponseEscapeStream(
			new BufferedOutputStream(
				new ResponseOutputRawStream( this )
			)
		);
	}
	
	/**
	 * Error output stream getter. CAUTION! A flush is necessary.
	 * 
	 * @return A stream-like wrapper of this response's error output.
	 */
	public OutputStream getErrOutputStream() {
		return new ResponseEscapeStream(
			new BufferedOutputStream(
				new ResponseErrorRawStream( this )
			)
		);
	}

	/**
	 * Concatenates a bunch of arrays into one. TODO: This should be in a shared static class.
	 * 
	 * @param messages Array of arrays of byte.
	 * @return Array of byte.
	 */
	private static final byte[] concat( byte[] ... messages ) {
		if ( messages.length > 1 ) {
			int length, position;
			byte[] result;
			
			length = 0;
			for ( byte[] message : messages ) {
				length += message.length;
			}
			result = new byte[ length ];
			position = 0;
			for ( byte[] message : messages ) {
				System.arraycopy( message, 0, result, position, message.length );
				position += message.length;
			}
			return result;
		} else {
			return messages.length == 0 ? ZERO_BYTES : messages[ 0 ];
		}
	}
	
	/**
	 * Concatenates a bunch of arrays into one. TODO: This should be in a shared static class.
	 * 
	 * @param first The first array to add.
	 * @param messages Array of arrays of byte.
	 * @return Array of byte.
	 */
	private static final byte[] concat( byte[] first, byte[] ... messages ) {
		if ( messages.length > 0 ) {
			int length, position;
			byte[] result;
			
			length = first.length;
			for ( byte[] message : messages ) {
				length += message.length;
			}
			result = new byte[ length ];
			System.arraycopy( first, 0, result, 0, first.length );
			position = first.length;
			for ( byte[] message : messages ) {
				System.arraycopy( message, 0, result, position, message.length );
				position += message.length;
			}
			return result;
		} else {
			return first;
		}
	}
}
