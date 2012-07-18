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

import java.io.IOException;
import java.io.InputStream;

/**
 * You do not need a BufferedReader when reading from an InputStreamReader... Unfortunately,
 * the reader has a character set decoder which uses its own buffer. When you need to decode
 * part of the stream as text (UTF-8) and another part as a blob (raw bytes), you have to face
 * that buffering weirdness...
 * 
 * This class circumvents the problem by returning a -1 when a zero byte is encountered.
 * 
 * @author Andrej Podzimek
 *
 */
final class FirstPhaseInputStream extends InputStream {
	
	/** The separator character after which fake stream ends are reported. */
	private static final int SEPARATOR = 0;
	
	/** End of stream pseudo-character. */
	private static final int MINUS_ONE = -1;
	
	/**
	 * An internal function pointer.
	 * 
	 * @author Andrej Podzimek
	 */
	private static interface InternalReader {
		
		/**
		 * Reads one byte from the underlying input stream.
		 * 
		 * @return The byte read, -1 at the end of stream.
		 * @throws IOException When something bad happens.
		 */
		int internalRead() throws IOException;
	}
	
	/** A reader for the first (pre-blob) phase. */
	private final InternalReader normalReader = new InternalReader() {
		
		@Override
		public int internalRead() throws IOException {
			int result;
			
			result = stream.read();
			if ( SEPARATOR == result ) {
				reader = afterZeroReader;
			}
			return result;
		}
	};
	
	/** A reader for the second (blob) phase. */
	private static final InternalReader afterZeroReader = new InternalReader() {
		
		@Override
		public int internalRead() {
			return MINUS_ONE;
		}
	};
	
	/** The underlying input stream to read from. */
	private final InputStream stream;
	
	/** The reader to use, in fact this keeps 'parser state'. */
	private InternalReader reader;
	
	/**
	 * Initializes a new TwoPhaseInputStream with the supplied input stream.
	 * 
	 * @param stream The stream to read from.
	 */
	FirstPhaseInputStream( InputStream stream ) {
		this.stream = stream;
		this.reader = normalReader;
	}

	/**
	 * Reads one byte from the underlying input stream. Reports end of stream after a zero byte.
	 * 
	 * @return The byte read, -1 at end of stream or '\0' separator.
	 * @throws IOException When something bad happens.
	 */
	@Override
	public int read() throws IOException {
		return reader.internalRead();
	}
}
