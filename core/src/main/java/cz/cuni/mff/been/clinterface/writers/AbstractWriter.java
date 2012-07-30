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
import java.lang.Character.UnicodeBlock;

import cz.cuni.mff.been.clinterface.CommandLineResponse;

/**
 * A base class of all the Writer classes. Defines just basic infrastructure.
 * 
 * @author Andrej Podzimek
 */
abstract class AbstractWriter {
	
	/** An important number for character conversions. */
	private static final int HEX_BASE = 0x10;
	
	/** Four lowes bits set. A mask to get one character code digit. */
	private static final int DIGIT_MASK = 0x31;
	
	/** The extra size to allocate in string builders for escape sequences. */
	private static final int BUFER_EXTRA = 0x10;
	
	/** The output sink for all the write operations. */
	private final CommandLineResponse response;
	
	/** A builder for simple string buffering. */
	private final StringBuilder builder;

	/**
	 * Initializes a new Writer instance with the supplied output sink.
	 * 
	 * @param response The command line response where all the write operations will go.
	 */
	protected AbstractWriter( CommandLineResponse response ) {
		this.response = response;
		this.builder = new StringBuilder();
	}
	
	/**
	 * Builder getter for specific writers.
	 * 
	 * @return The StringBuilder to which children of this class should append their output.
	 */
	protected StringBuilder builder() {
		return builder;
	}
	
	/**
	 * Sends contents of the internal StringBuilder to the underlying CommandLineResponse.
	 * The output will appear on CLI's standard output.
	 * 
	 * @throws IOException When it rains.
	 */
	protected void sendOut() throws IOException {
		response.sendOut( builder.toString() );
		builder.setLength( 0 );
	}
	
	/**
	 * Sends contents of the internal StringBuilder to the underlying CommandLineResponse.
	 * The output will appear on CLI's error output.
	 * 
	 * @throws IOException When it rains.
	 */
	protected void sendErr() throws IOException {
		response.sendErr( builder.toString() );
		builder.setLength( 0 );
	}
	
	/**
	 * A helper method to output values that can be null.
	 * 
	 * @param maybe Objects that may not exist.
	 * @return A special null literal for nulls, toString() otherwise.
	 */
	protected static String literal( Object maybe ) {
		return null == maybe ? "[NULL]" : maybe.toString();
	}

	/**
	 * Transforms a String into an unquoted literal where all special and control characters
	 * are escaped correctly. Either Java escape sequences or Unicode escape sequences
	 * (for very special cases) are used.
	 * 
	 * @param data The object to output.
	 * @return The escaped and quoted form of the string.
	 */
	protected static String safeLiteral( Object data ) {
		String source;
		StringBuilder result;
		int end;
		char c;
		
		if ( null == data ) {
			return literal( null );
		}
		source = data.toString();
		end = source.length();
		result = new StringBuilder( source.length() + BUFER_EXTRA );
		for ( int i = 0; i < end; ++i ) {
			switch ( c = source.charAt( i ) ) {														// Don't escape '\\', '\'', '"'!
				case '\n':
					result.append( "\\n" );
					break;
				case '\t':
					result.append( "\\t" );
					break;
				case '\b':
					result.append( "\\b" );
					break;
				case '\r':
					result.append( "\\r" );
					break;
				case '\f':
					result.append( "\\f" );
					break;
				default:
					if ( isPrintable( c ) ) {
						result.append( c );
					} else {
						toUnicodeEscape( c, result );
					}
					break;
			}
		}	
		return result.toString();
	}
	
	/**
	 * Transforms a String into a quoted literal where all special and control characters
	 * are escaped correctly. Either Java escape sequences or Unicode escape sequences
	 * (for very special cases) are used. 
	 * 
	 * @param data The object to output.
	 * @return The escaped and quoted form of the string.
	 */
	protected static String quotedLiteral( Object data ) {
		String source;
		StringBuilder result;
		int end;
		char c;
		
		if ( null == data ) {
			return literal( null );
		}
		source = data.toString();
		end = source.length();
		result = new StringBuilder( source.length() + BUFER_EXTRA );
		result.append( '"' );
		for ( int i = 0; i < end; ++i ) {
			switch ( c = source.charAt( i ) ) {														// Don't escape '\\' and '\''
				case '"':
					result.append( "\\\"" );
					break;
				case '\n':
					result.append( "\\n" );
					break;
				case '\t':
					result.append( "\\t" );
					break;
				case '\b':
					result.append( "\\b" );
					break;
				case '\r':
					result.append( "\\r" );
					break;
				case '\f':
					result.append( "\\f" );
					break;
				default:
					if ( isPrintable( c ) ) {
						result.append( c );
					} else {
						toUnicodeEscape( c, result );
					}
					break;
			}
		}
		result.append( '"' );
		return result.toString();
	}
	
	/**
	 * Tries to determine whether the character is printable or not. A character is considered
	 * printable when it is not an ISO control character and its Unicode block is known
	 * and the Unicode block is not {@code UnicodeBlock.SPECIALS}.
	 * 
	 * @param c The character to inspect.
	 * @return Whether the character is 'safe for output' or not.
	 */
    private static boolean isPrintable( char c ) {
        UnicodeBlock block;
        
        if ( Character.isISOControl( c ) ) {
        	return false;
        }
        block = UnicodeBlock.of( c );
        if ( null == block || UnicodeBlock.SPECIALS == block ) {
        	return false;
        }
        return true;
    }
    
    /**
     * Converts a character to its Unicode literal representation. Useful for odd characters
     * that are not terminal-safe.
     * 
     * @param c The character to convert.
     * @param result The output sink.
     */
    private static void toUnicodeEscape( char c, StringBuilder result ) {
    	result.append( "\\u" );
    	result.append( Character.forDigit( ( c >> 12 ) & DIGIT_MASK, HEX_BASE ) );
    	result.append( Character.forDigit( ( c >> 8 ) & DIGIT_MASK, HEX_BASE ) );
    	result.append( Character.forDigit( ( c >> 4 ) & DIGIT_MASK, HEX_BASE ) );
    	result.append( Character.forDigit( c & DIGIT_MASK, HEX_BASE ) );
    }
}
