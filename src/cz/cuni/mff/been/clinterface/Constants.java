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


/**
 * A simple container for error codes and other important shared constants.
 * 
 * @author Andrej Podzimek
 */
public final class Constants {
	
	/**
	 * This is a static class. No construction allowed, just destruction.
	 */
	private Constants() {
	}
	
	/** The default BEEN port. Conflicts with IANA ports and should be changed in the future. */
	static final int BEEN_PORT;
	
	static {
		int beenPort;
		
		beenPort = 2336;
		try {
			String beenPortString;
			
			beenPortString = System.getenv( "BEEN_PORT" );
			if ( null != beenPortString ) {
				beenPort = Integer.parseInt( beenPortString );
			}
		} catch ( NumberFormatException exception ) {
			System.err.println( "Malformed BEEN_PORT variable. Using default value." );
		} catch ( SecurityException exception ) {
			System.err.println( "Cannot read BEEN_PORT variable. Using default value." );
		}
		BEEN_PORT = beenPort;
	}
	
	/** A base value added to all module-specific error codes. Should avoid all errno values. */
	static final byte SPECIFIC_CODE_BASE = -64;
	
	/**
	 * List of all non-fatal error codes caused by incorrect input data.
	 */
	public static enum DataError {
		
		/** Unexpected (misplaced) quote character encountered. */
		UNEXP_QUOTE( "Unexpected quote symbol.", new byte[] { 0, -9 } ),
		
		/** Unexpected (misplaced) equal sign encountered. */
		UNEXP_EQ( "Unexpected '=' symbol.", new byte[] { 0, -8 } ),
		
		/** Unexpected (misplaced) zero symbol encountered. */
		UNEXP_ZERO( "Unexpected '\\0' (zero) symbol.", new byte[] { 0, -7 } ),
		
		/** Unexpected end of file. */
		UNEXP_EOF( "Unexpected end of input.", new byte[] { 0, -6 } ),
		
		/** No module name supplied. (This means that no tokens were sent.) */
		NO_MODULE( "No module specified. Available modules:", new byte[] { 0, -5 } ),
		
		/** No action name supplied. (This means that only one token was sent. */ 
		NO_ACTION( "No action specified. Available actions:", new byte[] { 0, -4 } ),
		
		/** Unknown module name has been sent. */
		UNKNOWN_MODULE( "Unknown module name.", new byte[] { 0, -3 } ),
		
		/** The requested character set is not supported. (Not used yet.) */
		UNKNOWN_CHARSET( "Unknown encoding name.", new byte[] { 0, -2 } );
		
		/** The message string. */
		final String MSG;
		
		/** A return code switch that should be sent back to the command line client. */
		final byte[] ERR;
		
		/**
		 * Initializes an item of {@link DataError} enum.
		 * 
		 * @param msg The human-readable message this item will convey.
		 * @param err The error code sent back to the command line client.
		 */
		private DataError( String msg, byte[] err ) {
			this.MSG = msg;
			this.ERR = err;
		}
	}
	
	static enum TempError {
		
		UNREACHABLE( "BEEN component unreachable.", new byte[] { 0, -11 } );
		
		/** The message string. */
		final String MSG;
		
		/** A return code switch that should be sent back to the command line client. */
		final byte[] ERR;
		
		/**
		 * Initializes an item of {@link TempError} enum.
		 * 
		 * @param msg The human-readable message this item will convey.
		 * @param err The error code sent back to the command line client.
		 */
		private TempError( String msg, byte[] err ) {
			this.MSG = msg;
			this.ERR = err;
		}		
	};
	
	/**
	 * List of non-fatal connection errors. Presumably, these errors cannot be reported back
	 * to the command line client.
	 */
	static enum ConnError {
		
		/** Unknown network error. */
		UNKNOWN_IOE( "Unknown network/IO error." ),
		
		/** When the TCP connection with bcmd fails. */
		MODULE_IOE( "Could not send response back to the command line client." ),
		
		/** Could not close a socket. (This is possibly a fatal OS problem, but who carse... */
		CLOSE_SOCK( "Could not close socket." );
		
		/** The message this item will convey. */
		final String MSG;
		
		/**
		 * Initializes an item of {@link ConnError} enum.
		 * 
		 * @param msg The human-readable message this item will convey.
		 */
		private ConnError( String msg ) {
			this.MSG = msg;
		}
	}
	
	/** Errors not related to user input. Caused by bugs or unusual events. */
	static enum IntegrityError {
		
		/** The parser has reached an illegal state. This is debugging stuff.*/
		INT_ERR( "Parser integrity error." ),
		
		/** Could not instantiate a module for the requested task. */
		MOD_INST_ERR( "Module instantiation error." );
		
		/** The message this item will convey. */
		final String MSG;
		
		/**
		 * Initializes an item of {@link IntegrityError} enum.
		 * 
		 * @param msg The human-readable message this item will convey.
		 */
		private IntegrityError( String msg ) {
			this.MSG = msg;
		}
	}
}
