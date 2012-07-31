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
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import cz.cuni.mff.been.clinterface.Constants.DataError;
import cz.cuni.mff.been.clinterface.Constants.IntegrityError;
import cz.cuni.mff.been.common.Pair;

/**
 * This class is nothing but a getter for flags and parameters. (A flag is a parameter with
 * no value.) The web interface uses a similar mechanism, but it is built closely around
 * the Apache stuff. See {@code HttpServletRequest} for details.
 * 
 * @author Andrej Podzimek
 */
public class CommandLineRequest {

	/**
	 * This class represents the state of the parser. State changes based on tokens obtaind
	 * from the tokenizer.
	 */
	private interface State {
		
		/**
		 * Parses a token based on the state this class represents.
		 * 
		 * @return False if end of stream was seen, true otherwise.
		 * @throws IllegalInputException When a grammar error is encountered.
		 * @throws IOException When the underlying input stream fails.
		 * @throws CommandLineException When an integrity error is detected.
		 */
		abstract boolean parseToken()
		throws IllegalInputException, IOException, CommandLineException;
	}

	/**
	 * This class represents an initial parser state, either at the beginning of the input
	 * stream or after storing a complete parameter.
	 */
	private final class StateInitial implements State {

		@Override
		public boolean parseToken() throws IOException, CommandLineException {
			switch ( tokenizer.nextToken() ) {
				case StreamTokenizer.TT_WORD:
					state = afterWord;
					lastName = tokenizer.sval;
					return true;
				case StreamTokenizer.TT_EOF:
					return false;
				case '"':
				case '\'':
					throw new IllegalInputException( DataError.UNEXP_QUOTE );
				case '=':
					throw new IllegalInputException( DataError.UNEXP_EQ );
				case '\0':
					blobReady = true;
					return false;
				default:
					throw new CommandLineException( IntegrityError.INT_ERR.MSG );
			}
		}
	}

	/**
	 * This class represents a parser state after a plain word (not a quoted expression) was
	 * encountered.
	 */
	private final class StateAfterWord implements State {

		@Override
		public boolean parseToken() throws IOException, CommandLineException {
			switch( tokenizer.nextToken() ) {
				case StreamTokenizer.TT_WORD:
					flags.add( lastName );
					lastName = tokenizer.sval;
					return true;
				case StreamTokenizer.TT_EOF:
					flags.add( lastName );
					return false;
				case '"':
				case '\'':
					throw new IllegalInputException( DataError.UNEXP_QUOTE );
				case '=':
					state = afterEq;
					return true;
				case '\0':
					flags.add( lastName );
					blobReady = true;
					return false;
				default:
					throw new CommandLineException( IntegrityError.INT_ERR.MSG );	
			}
		}
	}

	/**
	 * This class represents a parser state after '='. The next word (whether qoted or not)
	 * will be stored as a parameter value.
	 */
	private final class StateAfterEq implements State {

		@Override
		public boolean parseToken() throws IOException, CommandLineException {
			switch ( tokenizer.nextToken() ) {
				case StreamTokenizer.TT_WORD:
					state = initial;
					parameters.put( lastName, tokenizer.sval );
					return true;
				case StreamTokenizer.TT_EOF:
					throw new IllegalInputException( DataError.UNEXP_EOF );
				case '"':
				case '\'':
					state = initial;
					parameters.put( lastName, tokenizer.sval );
					return true;
				case '=':
					throw new IllegalInputException( DataError.UNEXP_EQ );
				case '\0':
					throw new IllegalInputException( DataError.UNEXP_ZERO );
				default:
					throw new CommandLineException( IntegrityError.INT_ERR.MSG );
			}
		}
	}

	/** Maps parameter names to parameter values. */
	private final Map< String, String >	parameters;

	/** Lists flags. */
	private final Set< String > flags;
	
	/** Using the old and weird standard lexer. Read its source code before experimenting! */
	private final StreamTokenizer tokenizer;
	
	/** A reader useful to access the input stream directly. */
	private final InputStream stream;

	/** Instance of the initial parser state. */
	private final StateInitial initial;

	/** Instance of the "after word" parser state. */
	private final StateAfterWord afterWord;

	/** Instance of the "after '='" parser state. */ 
	private final StateAfterEq afterEq;

	/** An iterable that can provide parameter listing to the outside world. */
	private Iterable< Entry< String, String > > parametersIterable;

	/** An Iterable that can provide flag listing to the outside world. */
	private Iterable< String > flagsIterable;

	/** Current state of the grammar parser. */
	private State state;

	/** String value of the previous token. */
	private String lastName;
	
	/** Whether there is a blob to be read. */
	private boolean blobReady;

	/**
	 * Creates a new command line request based on an initialized {@link StreamTokenizer}.
	 * This class expects that all the initial metadata, such as module and action name
	 * or charset name have been read from the tokenizer and treats all tokens as flags
	 * or parameters.
	 * 
	 * @param tokenizer The tokenizer to read from.
	 * @param stream The underlying InputStream of the tokenizer.
	 * @throws IOException If the tokenizer's underlying stream fails.
	 * @throws CommandLineException When a malformed or misplaced construct is found.
	 */
	CommandLineRequest( StreamTokenizer tokenizer, InputStream stream )
	throws IOException, CommandLineException {
		this.tokenizer = tokenizer;
		this.stream = stream;
		parameters = new TreeMap< String, String >();
		flags = new TreeSet< String >();

		initial = new StateInitial();
		afterWord = new StateAfterWord();
		afterEq = new StateAfterEq();

		blobReady = false;
		state = initial;
		while ( state.parseToken() );																// This moves the automaton.
	}

	/**
	 * Parameter value getter.
	 * 
	 * @param name Name of the parameter to look for.
	 * @return The parameter's value or null if no such parameter exists.
	 */
	public String getParameter( String name ) {
		return parameters.get( name );
	}

	/**
	 * This function makes the list of parameters {@link Iterable} to the outside world.
	 * 
	 * @return A read-only {@link Iterable} that provides a list of oparameters.
	 */
	public Iterable< Entry< String, String > > iterateParameters() {
		return
		parametersIterable == null ? (
			parametersIterable = new Iterable< Entry< String, String > >() {
				@Override public Iterator< Entry< String,String > > iterator() {
					return new Iterator< Entry< String, String > >() {
						private final Iterator< Entry< String, String > > iterator;
						{ iterator = parameters.entrySet().iterator(); }
						@Override public boolean hasNext() { return iterator.hasNext(); }
						@Override public Entry<String,String> next() { return iterator.next(); }
						@Override public void remove() { throw new UnsupportedOperationException(); }
					};
				}
			}
		) : parametersIterable;
	}
	
	/**
	 * Verifies that only allowed parameters (and no other) are present.
	 * 
	 * @param expectedParameters A set of allowed parameters. 
	 * @return null if only allowed parameters are present, a list of disallowed ones otherwise. 
	 */
	@SuppressWarnings( "null" )
	public ArrayList< Entry< String, String > > ensureOnlyParameters(
		Set< String > expectedParameters
	) {
		Iterator< Entry< String, String > > iterator;
		Entry< String, String > entry;
		ArrayList< Entry< String, String > > list;
	
		list = null;																				// Formality...
		iterator = parameters.entrySet().iterator();

		while ( iterator.hasNext() ) {
			entry = iterator.next();
			if ( !expectedParameters.contains( entry.getKey() ) ) {
				list = new ArrayList< Entry< String,String > >();
				list.add( entry );
				break;
			}
		}
		
		while ( iterator.hasNext() ) {																// Now we know: list != null
			entry = iterator.next();
			if ( !expectedParameters.contains( entry.getKey() ) ) {
				list.add( entry );																	// @SuppressWarnings needed here.
			}
		}

		return list;
	}
	
	/**
	 * Verifies that all of the expected parameters exist.
	 * 
	 * @param expectedParameters Parameters expected to exist.
	 * @return null if all the expected parameters exist, a list of missing ones otherwise.
	 */
	@SuppressWarnings( "null" )
	public ArrayList< String > ensureParametersExist( Iterable< String > expectedParameters ) {
		Iterator< String > iterator;
		String parameter;
		ArrayList< String > list;
		
		list = null;
		iterator = expectedParameters.iterator();
		
		while ( iterator.hasNext() ) {
			parameter = iterator.next();
			if ( !parameters.containsKey( parameter ) ) {
				list = new ArrayList< String >();
				list.add( parameter );
				break;
			}
		}
		
		while ( iterator.hasNext() ) {
			parameter = iterator.next();
			if ( !parameters.containsKey( parameter ) ) {
				list.add( parameter );																// @SuppressWarnings needed here.
			}
		}
		
		return list;
	}
	
	/**
	 * Can be used to verify more complex conditions (at least one parameter, exactly one
	 * parameter).
	 * 
	 * @param expectedParameters A set of parameters that sould be queried.
	 * @return A list of the found parameters. Null if none found.
	 */
	@SuppressWarnings( "null" )
	public ArrayList< Entry< String, String > > getParametersThatExist(
		Iterable< String > expectedParameters
	) {
		ArrayList< Entry < String, String > > list;
		Iterator< String > iterator;
		String parameter;
		String value;
		
		list = null;
		iterator = expectedParameters.iterator();
		
		while ( iterator.hasNext() ) {
			parameter = iterator.next();
			if ( null != ( value = parameters.get( parameter ) ) ) {
				list = new ArrayList< Entry< String, String > >();
				list.add( Pair.pair( parameter, value ) );
				break;
			}
		}
		
		while ( iterator.hasNext() ) {
			parameter = iterator.next();
			if ( null != ( value = parameters.get( parameter ) ) ) {
				list.add( Pair.pair( parameter, value ) );
			}
		}
		
		return list;
	}

	/**
	 * Flag state getter.
	 * 
	 * @param name Name of the flag to look for.
	 * @return true if the flag has been set, false otherwise.
	 */
	public boolean getFlag( String name ) {
		return flags.contains( name );
	}

	/**
	 * This function makes the list of flags {@link Iterable} to the outside world.
	 * 
	 * @return A read-only {@link Iterable} that provides a list of flags.
	 */
	public Iterable< String > iterateFlags() {
		return
		flagsIterable == null ? (
			flagsIterable = new Iterable< String >() {
				@Override public Iterator< String > iterator() {
					return new Iterator< String >() {
						private final Iterator< String > iterator;
						{ iterator = flags.iterator(); }
						@Override public boolean hasNext() { return iterator.hasNext(); }
						@Override public String next() { return iterator.next(); }
						@Override public void remove() { throw new UnsupportedOperationException(); }
					};
				}
			}
		) : flagsIterable;
	}
	
	/**
	 * Verifies that only allowed flags (and no other) are present.
	 * 
	 * @param expectedFlags A set of allowed flags.
	 * @return null if only allowed flags are present, a list of disallowed ones otherwise.
	 */
	@SuppressWarnings( "null" )
	public ArrayList< String > ensureOnlyFlags( Set< String > expectedFlags ) {
		Iterator< String > iterator;
		String flag;
		ArrayList< String > list;
		
		list = null;
		iterator = flags.iterator();
		
		while ( iterator.hasNext() ) {
			flag = iterator.next();
			if ( !expectedFlags.contains( flag ) ) {
				list = new ArrayList< String >();
				list.add( flag );
				break;
			}
		}
		
		while ( iterator.hasNext() ) {
			flag = iterator.next();
			if ( !expectedFlags.contains( flag ) ) {
				list.add( flag );																	// @SuppressWarnings needed here.
			}
		}
		
		return list;
	}
	
	/**
	 * Verifies that all of the expected flags exist. This is probably a *useless* method.
	 * If a flag is required to exist, it doesn't carry any information at all.
	 * 
	 * @param expectedFlags Flags expected to exist.
	 * @return null if all the expected flags exist, a list of missing ones otherwise.
	 */
	@SuppressWarnings( "null" )
	public ArrayList< String > ensureFlagsExist( Iterable< String > expectedFlags ) {
		Iterator< String > iterator;
		String flag;
		ArrayList< String > list;
		
		list = null;
		iterator = expectedFlags.iterator();
		
		while ( iterator.hasNext() ) {
			flag = iterator.next();
			if ( !flags.contains( flag ) ) {
				list = new ArrayList< String >();
				list.add( flag );
				break;
			}
		}
		
		while ( iterator.hasNext() ) {
			flag = iterator.next();
			if ( !flags.contains( flag ) ) {
				list.add( flag );																	// @SuppressWarnings needed here.
			}
		}
		
		return list;
	}
	
	/**
	 * Can be used to verify more complex conditions (at least one flag, exactly one flag).
	 * 
	 * @param expectedFlags A set of flags that should be queried.
	 * @return A list of the found flags. Null if no flags found.
	 */
	@SuppressWarnings( "null" )
	public ArrayList< String > getFlagsThatExist( Iterable< String > expectedFlags ) {
		Iterator< String > iterator;
		String flag;
		ArrayList< String > list;
		
		list = null;
		iterator = expectedFlags.iterator();
		
		while ( iterator.hasNext() ) {
			flag = iterator.next();
			if ( flags.contains( flag ) ) {
				list = new ArrayList< String >();
				list.add( flag );
				break;
			}
		}
		
		while ( iterator.hasNext() ) {
			flag = iterator.next();
			if ( flags.contains( flag ) ) {
				list.add( flag );
			}
		}
		
		return list;
	}
	
	/**
	 * Blob status getter.
	 * 
	 * @return True when a blob can be received, false otherwise.
	 */
	public boolean hasBlob() {
		return blobReady;
	}
	
	/**
	 * Blob access getter.
	 * 
	 * @return A reader that can access a blob when available. When no blob is available,
	 * contents obtained from the reader are undefined.
	 */
	public InputStream getBlobStream() {
		return new DoNotCloseInputStream( stream );
	}
}
