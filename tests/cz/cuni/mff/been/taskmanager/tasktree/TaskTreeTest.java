package cz.cuni.mff.been.taskmanager.tasktree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.been.taskmanager.data.TaskEntry;

public class TaskTreeTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAddChildPreorder() throws IllegalAddressException, MalformedAddressException, RemoteException {
		TaskTree tree;
		
		for ( String[][] templatesByLevel : TREES )
			for ( String[] templatesBySeed : templatesByLevel )
				for ( String template : templatesBySeed ) {
					tree = makeTreePreorder( template );
					assertEquals( "Error in tree construction!", template, treeToString( tree ) );
					assertEquals( "Iteration clobbered the tree", template, treeToString( tree ) );
				}
	}
	
	@Test
	public void testAddChildPostorder() throws MalformedAddressException, IllegalAddressException, RemoteException {
		TaskTree tree;
		
		for ( String[][] templatesByLevel : TREES )
			for ( String[] templatesBySeed : templatesByLevel )
				for ( String template : templatesBySeed ) {
					tree = makeTreePostorder( template );
					assertEquals( "Error in tree construction!", template, treeToString( tree ) );
					assertEquals( "Iteration clobbered the tree", template, treeToString( tree ) );
				}
	}
	
	@Test
	public void testIllegalAddChildPreorder() throws MalformedAddressException, IllegalAddressException, RemoteException {
		TaskTree tree;
		
		for ( String[][] templatesByLevel : TREES )
			for ( String[] templatesBySeed : templatesByLevel )
				for ( String template : templatesBySeed ) {
					tree = makeTreePreorderIRI( template );
					assertEquals( "Error in tree construction!", template, treeToString( tree ) );
					assertEquals( "Iteration clobbered the tree", template, treeToString( tree ) );
				}
	}
	
	@Test
	public void testIllegalAddChildPostorder() throws MalformedAddressException, IllegalAddressException, RemoteException {
		TaskTree tree;
		
		for ( String[][] templatesByLevel : TREES )
			for ( String[] templatesBySeed : templatesByLevel )
				for ( String template : templatesBySeed ) {
					tree = makeTreePostorderIRI( template );
					assertEquals( "Error in tree construction!", template, treeToString( tree ) );
					assertEquals( "Iteration clobbered the tree", template, treeToString( tree ) );
				}
	}
	
	@Test
	public void testReaddChildPreorder() throws MalformedAddressException, IllegalAddressException, RemoteException {
		TaskTree tree;
		
		for ( String[][] templatesByLevel : TREES )
			for ( String[] templatesBySeed : templatesByLevel )
				for ( String template : templatesBySeed ) {
					tree = makeTreePreorderLRI( template );
					assertEquals( "Error in tree construction!", template, treeToString( tree ) );
					assertEquals( "Iteration clobbered the tree", template, treeToString( tree ) );
				}
	}
	
	@Test
	public void testReaddChildPostorder() throws MalformedAddressException, IllegalAddressException, RemoteException {
		TaskTree tree;
		
		for ( String[][] templatesByLevel : TREES )
			for ( String[] templatesBySeed : templatesByLevel )
				for ( String template : templatesBySeed ) {
					tree = makeTreePostorderLRI( template );
					assertEquals( "Error in tree construction!", template, treeToString( tree ) );
					assertEquals( "Iteration clobbered the tree", template, treeToString( tree ) );
				}
	}
	
	@After
	public void tearDown() throws Exception {
	}

	private static final String[] TEMPLATES = {
		"leaf",
		"node()",
		"node(#)",
		"node(##)",
		"node(###)",
		"node(####)",
		"node(#####)"
	};
	
	private static final int[] LEVEL_SIZES = { 0, 2, 15, 17 * 15, 7 * 15 * 15, 7 * 15 * 15 };
	
	private static final int MAX_LEVELS = LEVEL_SIZES.length - 1;
	
	private static final String[] SEEDS = { "#", "##", "###", "####", "#####" };
	
	private static final String[][][] TREES = new String[][][] {
		new String[ 0 ][ 0 ],
		new String[ LEVEL_SIZES[ 1 ] ][ SEEDS.length ],
		new String[ LEVEL_SIZES[ 2 ] ][ SEEDS.length ],
		new String[ LEVEL_SIZES[ 3 ] ][ SEEDS.length ],
		new String[ LEVEL_SIZES[ 4 ] ][ SEEDS.length ],
		new String[ LEVEL_SIZES[ 5 ] ][ SEEDS.length ]
	};
	
	private static final class StackItem {
		StackItem( int position, int depth ) {
			this.position = position;
			this.depth = depth;
		}
		final int position;
		final int depth;
	}
	
	private static class InfiniteIterator implements Iterator< String > {
		int internalCounter = 0;
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public String next() {
			internalCounter %= TEMPLATES.length;
			return TEMPLATES[ internalCounter++ ];
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	};
	
	private static class InfiniteTerminalIterator implements Iterator< String > {
		int internalCounter = 0;
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public String next() {
			internalCounter &= 1;
			return TEMPLATES[ internalCounter++ ];
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static String replace(
		String original,
		int position,
		String template,
		ArrayDeque< StackItem > stack,
		int depth,
		int counter
	) {
		String modifiedTemplate;
		
		modifiedTemplate = " X-" + counter + '-' + template + ' ';
		for ( int i = 0; i < modifiedTemplate.length(); ++i ) {
			if ( modifiedTemplate.charAt( i ) == '#' ) {
				stack.push( new StackItem( position + i, depth ) );
			}
		}
		return original.substring( 0, position ) + modifiedTemplate + original.substring( position + 1 );
	}

	static {
		final InfiniteIterator templates;
		final InfiniteTerminalIterator terminals;
		
		ArrayDeque< StackItem > stack;
		String result;
		StackItem item;
		int counter;
		
		templates = new InfiniteIterator();
		terminals = new InfiniteTerminalIterator();
		
		for ( int level = 1; level <= MAX_LEVELS; ++level ) {										// For all the maximum depths.
			for ( int i = 0; i < LEVEL_SIZES[ level ]; ++i ) {										// Number of trees for the given depth.
				for ( int j = 0; j < SEEDS.length; ++j ) {											// For all the initial seeds.
					stack = new ArrayDeque< StackItem >();
					result = SEEDS[ j ];
					for ( int k = 0; k < SEEDS[ j ].length(); ++k ) {								// For each hash in an initial seed.
						stack.push( new StackItem( k, 0 ) );
					}
					counter = 0;

					do {
						item = stack.pop();
						if ( item.depth >= level - 1 ) {
							result = replace( result, item.position, terminals.next(), stack, item.depth + 1, counter++ );
						} else {
							result = replace( result, item.position, templates.next(), stack, item.depth + 1, counter++ );
						}
					} while ( !stack.isEmpty() );
					TREES[ level ][ i ][ j ] = result;
				}
			}
		}
	}
	
	/* Standard and correct tree construction. */
	
	private TaskTree makeTreePreorder( String treePattern ) throws IllegalAddressException, MalformedAddressException, RemoteException {
		String myAddress;
		TaskEntry te;
		TokenData data;
		TaskTree result;

		data = new TokenData( treePattern );
		result = new TaskTree();
		nextToken( data );

		while ( data.type != TokenType.END ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					result.addNode( result.addressFromPath( myAddress ) );
					nextToken( data );
					makeTreePreorder( myAddress, data, result );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					break;
				case NAME:
				case END:
					te = new TaskEntry( myAddress.replace( '/', '-' ) );
					result.addLeaf( result.addressFromPath( myAddress ), te );
					break;
				case RIGHT_BRACE:
					throw new IllegalStateException( "Unexpected right brace." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			}
		}
		return result;
	}

	private TaskTree makeTreePostorder( String treePattern ) throws MalformedAddressException, IllegalAddressException, RemoteException {
		String myAddress;
		TaskEntry te;
		TokenData data;
		TaskTree result;

		data = new TokenData( treePattern );
		result = new TaskTree();
		nextToken( data );

		while ( data.type != TokenType.END ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					nextToken( data );
					makeTreePostorder( myAddress, data, result );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					result.addNode( result.addressFromPath( myAddress ) );
					break;
				case NAME:
				case END:
					te = new TaskEntry( myAddress.replace( '/', '-' ) );
					result.addLeaf( result.addressFromPath( myAddress ), te );
					break;
				case RIGHT_BRACE:
					throw new IllegalStateException( "Unexpected right brace." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			}
		}
		return result;
	}

	private void makeTreePreorder( String address, TokenData data, TaskTree tree ) throws IllegalAddressException, MalformedAddressException, RemoteException {
		String myAddress;
		TaskEntry te;

		while ( data.type != TokenType.RIGHT_BRACE ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = address + '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					tree.addNode( tree.addressFromPath( myAddress ) );
					nextToken( data );
					makeTreePreorder( myAddress, data, tree );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					break;
				case NAME:
				case RIGHT_BRACE:
					te = new TaskEntry( myAddress.replace( '/', '-' ) );
					tree.addLeaf( tree.addressFromPath( myAddress ), te );
					break;
				case END:
					throw new IllegalStateException( "Tree pattern ended prematurely." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			} 
		}
	}

	private void makeTreePostorder( String address, TokenData data, TaskTree tree ) throws MalformedAddressException, IllegalAddressException, RemoteException {
		String myAddress;
		TaskEntry te;
		
		while ( data.type != TokenType.RIGHT_BRACE ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = address + '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					nextToken( data );
					makeTreePostorder( myAddress, data, tree );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					tree.addNode( tree.addressFromPath( myAddress ) );
					break;
				case NAME:
				case RIGHT_BRACE:
					te = new TaskEntry( myAddress.replace( '/', '-' ) );
					tree.addLeaf( tree.addressFromPath( myAddress ), te );
					break;
				case END:
					throw new IllegalStateException( "The pattern ended prematurely." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			}
		}
	}
	
	/* Illegal re-insertion.
	 * 
	 * An important note: We do not test re-insertion "from the middle". Such a test would be
	 * too hard to implement. Furthermore, it would test the underlying collections and
	 * the TreeNode class rather than the tree itself.
	 */

	private TaskTree makeTreePreorderIRI( String treePattern ) throws IllegalAddressException, MalformedAddressException, RemoteException {
		String myAddress;
		TaskEntry te0, te1;
		TokenData data;
		TaskTree result;

		data = new TokenData( treePattern );
		result = new TaskTree();
		nextToken( data );

		while ( data.type != TokenType.END ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					te0 = new TaskEntry( myAddress.replace( '/', '-' ) );
					result.addNode( result.addressFromPath( myAddress ) );
					try {
						result.addLeaf( result.addressFromPath( myAddress ), te0 );
						fail( "A node replaced by a leaf." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						result.addLeaf( result.addressFromPath( myAddress ), te0 );
						fail( "A node replaced by a leaf, second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					nextToken( data );
					makeTreePreorderIRI( myAddress, data, result );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					break;
				case NAME:
				case END:
					te0 = new TaskEntry( myAddress.replace( '/', '-' ) );
					te1 = new TaskEntry( myAddress.replace( '/', '-' ) );
					result.addLeaf( result.addressFromPath( myAddress ), te0 );
					try {
						result.addNode( result.addressFromPath( myAddress ) );
						fail( "A leaf replaced by a node." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						result.addLeaf( result.addressFromPath( myAddress ), te1 );
						fail( "A leaf with different descriptor inserted." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						result.addNode( result.addressFromPath( myAddress ) );
						fail( "A leaf replaced by a node, second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						result.addLeaf( result.addressFromPath( myAddress ), te1 );
						fail( "A leaf replaced by a node on second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					break;
				case RIGHT_BRACE:
					throw new IllegalStateException( "Unexpected right brace." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			}
		}
		return result;
	}

	private TaskTree makeTreePostorderIRI( String treePattern ) throws MalformedAddressException, IllegalAddressException, RemoteException {
		String myAddress;
		TaskEntry te0, te1;
		TokenData data;
		TaskTree result;

		data = new TokenData( treePattern );
		result = new TaskTree();
		nextToken( data );

		while ( data.type != TokenType.END ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					nextToken( data );
					makeTreePostorderIRI( myAddress, data, result );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					te0 = new TaskEntry( myAddress.replace( '/', '-' ) );
					result.addNode( result.addressFromPath( myAddress ) );
					try {
						result.addLeaf( result.addressFromPath( myAddress ), te0 );
						fail( "A node replaced by a leaf." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						result.addLeaf( result.addressFromPath( myAddress ), te0 );
						fail( "A node replaced by a leaf, second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					break;
				case NAME:
				case END:
					te0 = new TaskEntry( myAddress.replace( '/', '-' ) );
					te1 = new TaskEntry( myAddress.replace( '/', '-' ) );
					result.addLeaf( result.addressFromPath( myAddress ), te0 );
					try {
						result.addLeaf( result.addressFromPath( myAddress ), te1 );
						fail( "A leaf with different descriptor inserted." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						result.addNode( result.addressFromPath( myAddress ) );
						fail( "A leaf replaced by a node." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						result.addLeaf( result.addressFromPath( myAddress ), te1 );
						fail( "A leaf with different descriptor inserted on second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						result.addNode( result.addressFromPath( myAddress ) );
						fail( "A leaf replaced by a node, second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					break;
				case RIGHT_BRACE:
					throw new IllegalStateException( "Unexpected right brace." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			}
		}
		return result;
	}

	private void makeTreePreorderIRI( String address, TokenData data, TaskTree tree ) throws IllegalAddressException, MalformedAddressException, RemoteException {
		String myAddress;
		TaskEntry te0, te1;

		while ( data.type != TokenType.RIGHT_BRACE ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = address + '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					tree.addNode( tree.addressFromPath( myAddress ) );
					try {
						tree.addLeaf( tree.addressFromPath( myAddress ), new TaskEntry( "dummy" ) );
						fail( "A node replaced by a leaf." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						tree.addLeaf( tree.addressFromPath( myAddress ), new TaskEntry( "dummy" ) );
						fail( "A node replaced by a leaf, second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					nextToken( data );
					makeTreePreorderIRI( myAddress, data, tree );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					break;
				case NAME:
				case RIGHT_BRACE:
					te0 = new TaskEntry( myAddress.replace( '/', '-' ) );
					te1 = new TaskEntry( myAddress.replace( '/', '-' ) );
					tree.addLeaf( tree.addressFromPath( myAddress ), te0 );
					try {
						tree.addLeaf( tree.addressFromPath( myAddress ), te1 );
						fail( "A leaf with different descriptor inserted." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						tree.addNode( tree.addressFromPath( myAddress ) );
						fail( "A leaf replaced by a node." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						tree.addLeaf( tree.addressFromPath( myAddress ), te1 );
						fail( "A leaf with different descriptor inserted on second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						tree.addNode( tree.addressFromPath( myAddress ) );
						fail( "A leaf replaced by a node, second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					break;
				case END:
					throw new IllegalStateException( "Tree pattern ended prematurely." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			} 
		}
	}

	private void makeTreePostorderIRI( String address, TokenData data, TaskTree tree ) throws MalformedAddressException, IllegalAddressException, RemoteException {
		String myAddress;
		TaskEntry te0, te1;
		
		while ( data.type != TokenType.RIGHT_BRACE ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = address + '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					nextToken( data );
					makeTreePostorderIRI( myAddress, data, tree );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					tree.addNode( tree.addressFromPath( myAddress ) );
					try {
						tree.addLeaf( tree.addressFromPath( myAddress ), new TaskEntry( "dummy" ) );
						fail( "A node replaced by a leaf." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						tree.addLeaf( tree.addressFromPath( myAddress ), new TaskEntry( "dummy" ) );
						fail( "A node replaced by a leaf, second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					break;
				case NAME:
				case RIGHT_BRACE:
					te0 = new TaskEntry( myAddress.replace( '/', '-' ) );
					te1 = new TaskEntry( myAddress.replace( '/', '-' ) );
					tree.addLeaf( tree.addressFromPath( myAddress ), te0 );
					try {
						tree.addNode( tree.addressFromPath( myAddress ) );
						fail( "A leaf replaced by a node." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						tree.addLeaf( tree.addressFromPath( myAddress ), te1 );
						fail( "A leaf with different descriptor inserted." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						tree.addNode( tree.addressFromPath( myAddress ) );
						fail( "A leaf replaced by a node, second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					try {
						tree.addLeaf( tree.addressFromPath( myAddress ), te1 );
						fail( "A leaf with different descriptor inserted on second attempt." );
					} catch ( IllegalAddressException exception ) {
						// This is what we want.
					}
					break;
				case END:
					throw new IllegalStateException( "The pattern ended prematurely." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			}
		}
	}

	/* Legal re-insertion.
	 * 
	 * Again, we do not test any reinsertion "from the middle". Such tests would be too hard
	 * to implement and we do not intend to test the underlying collections or the TreeNode class.
	 * The latter is tested thoroughly enough...
	 */
	
	private TaskTree makeTreePreorderLRI( String treePattern ) throws IllegalAddressException, MalformedAddressException, RemoteException {
		String myAddress;
		TaskEntry te;
		TokenData data;
		TaskTree result;

		data = new TokenData( treePattern );
		result = new TaskTree();
		nextToken( data );

		while ( data.type != TokenType.END ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					result.addNode( result.addressFromPath( myAddress ) );
					result.addNode( result.addressFromPath( myAddress ) );
					if ( ( result.addressFromPath( myAddress ).longHashCode() & 1L ) != 0L ) {
						result.addNode( result.addressFromPath( myAddress ) );
					}
					result.addNode( result.addressFromPath( myAddress ) );
					nextToken( data );
					makeTreePreorderLRI( myAddress, data, result );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					break;
				case NAME:
				case END:
					te = new TaskEntry( myAddress.replace( '/', '-' ) );
					result.addLeaf( result.addressFromPath( myAddress ), te );
					result.addLeaf( result.addressFromPath( myAddress ), te );
					if ( ( result.addressFromPath( myAddress ).longHashCode() & 1L ) == 0L ) {
						result.addLeaf( result.addressFromPath( myAddress ), te );
					}
					break;
				case RIGHT_BRACE:
					throw new IllegalStateException( "Unexpected right brace." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			}
		}
		return result;
	}

	private TaskTree makeTreePostorderLRI( String treePattern ) throws MalformedAddressException, IllegalAddressException, RemoteException {
		String myAddress;
		TaskEntry te;
		TokenData data;
		TaskTree result;

		data = new TokenData( treePattern );
		result = new TaskTree();
		nextToken( data );

		while ( data.type != TokenType.END ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					nextToken( data );
					makeTreePostorderLRI( myAddress, data, result );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					result.addNode( result.addressFromPath( myAddress ) );
					result.addNode( result.addressFromPath( myAddress ) );
					if ( ( result.addressFromPath( myAddress ).longHashCode() & 1L ) != 0L ) {
						result.addNode( result.addressFromPath( myAddress ) );
					}
					break;
				case NAME:
				case END:
					te = new TaskEntry( myAddress.replace( '/', '-' ) );
					result.addLeaf( result.addressFromPath( myAddress ), te );
					result.addLeaf( result.addressFromPath( myAddress ), te );
					if ( ( result.addressFromPath( myAddress ).longHashCode() & 1L ) == 0L ) {
						result.addLeaf( result.addressFromPath( myAddress ), te );
					}
					break;
				case RIGHT_BRACE:
					throw new IllegalStateException( "Unexpected right brace." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			}
		}
		return result;
	}

	private void makeTreePreorderLRI( String address, TokenData data, TaskTree tree ) throws IllegalAddressException, MalformedAddressException, RemoteException {
		String myAddress;
		TaskEntry te;

		while ( data.type != TokenType.RIGHT_BRACE ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = address + '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					tree.addNode( tree.addressFromPath( myAddress ) );
					tree.addNode( tree.addressFromPath( myAddress ) );
					if ( ( tree.addressFromPath( myAddress ).longHashCode() & 1L ) != 0L ) {
						tree.addNode( tree.addressFromPath( myAddress ) );
					}
					nextToken( data );
					makeTreePreorderLRI( myAddress, data, tree );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					break;
				case NAME:
				case RIGHT_BRACE:
					te = new TaskEntry( myAddress.replace( '/', '-' ) );
					tree.addLeaf( tree.addressFromPath( myAddress ), te );
					tree.addLeaf( tree.addressFromPath( myAddress ), te );
					if ( ( tree.addressFromPath( myAddress ).longHashCode() & 1L ) == 0L ) {
						tree.addLeaf( tree.addressFromPath( myAddress ), te );
					}
					break;
				case END:
					throw new IllegalStateException( "Tree pattern ended prematurely." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			} 
		}
	}

	private void makeTreePostorderLRI( String address, TokenData data, TaskTree tree ) throws MalformedAddressException, IllegalAddressException, RemoteException {
		String myAddress;
		TaskEntry te;
		
		while ( data.type != TokenType.RIGHT_BRACE ) {
			if ( data.type != TokenType.NAME ) {
				throw new IllegalStateException( "Name expected." );
			}
			myAddress = address + '/' + data.token;
			nextToken( data );
			switch ( data.type ) {
				case LEFT_BRACE:
					nextToken( data );
					makeTreePostorderLRI( myAddress, data, tree );
					if ( data.type != TokenType.RIGHT_BRACE ) {
						throw new IllegalStateException( "Missing right brace." );
					}
					nextToken( data );
					tree.addNode( tree.addressFromPath( myAddress ) );
					tree.addNode( tree.addressFromPath( myAddress ) );
					if ( ( tree.addressFromPath( myAddress ).longHashCode() & 1L ) != 0L ) {
						tree.addNode( tree.addressFromPath( myAddress ) );
					}
					break;
				case NAME:
				case RIGHT_BRACE:
					te = new TaskEntry( myAddress.replace( '/', '-' ) );
					tree.addLeaf( tree.addressFromPath( myAddress ), te );
					tree.addLeaf( tree.addressFromPath( myAddress ), te );
					if ( ( tree.addressFromPath( myAddress ).longHashCode() & 1L ) == 0L ) {
						tree.addLeaf( tree.addressFromPath( myAddress ), te );
					}
					break;
				case END:
					throw new IllegalStateException( "The pattern ended prematurely." );
				default:
					throw new IllegalStateException( "The Impossible has happened." );
			}
		}
	}
	
	private enum TokenType {
		LEFT_BRACE,
		RIGHT_BRACE,
		NAME,
		END;
	}

	private class TokenData {
		final String pattern;
		TokenType type;
		String token;
		int next;

		TokenData( String pattern ) {
			this.pattern = pattern;
		}
	}

	private void nextToken( TokenData data ) {
		int position, i;
		char curchar;
		final String whole;

		position = data.next;
		whole = data.pattern;
		StringCycle:
		for ( i = position; i < whole.length(); ++i ) {												// Skip white space.
			switch ( whole.charAt( i ) ) {
				case ' ':
				case '\t':
					break;
				default:
					break StringCycle;
			}
		}
		position = i;

		if ( position < whole.length() ) {															// Something to parse.
			switch ( whole.charAt( position ) ) {													// Left brace.
				case '(':
					data.type = TokenType.LEFT_BRACE;
					data.token = null;
					data.next = position + 1;
					break;
				case ')':																			// Right brace.
					data.type = TokenType.RIGHT_BRACE;
					data.token = null;
					data.next = position + 1;
					break;
				default:																			// Anything else: A string.
					StringBuilder builder;
					builder = new StringBuilder();
					StringCycle:
					for ( i = position; i < whole.length(); ++i ) {
						switch ( curchar = whole.charAt( i ) ) {
							case '(':																// End of string: left brace.
							case ')':																// End of string: right brace.
							case ' ':																// End of string: space.
							case '\t':																// End of string: tab character.
								break StringCycle;													// Stop that all!
							default:
								builder.append( curchar );											// Read one character.
								break;
						}
					}
					data.type = TokenType.NAME;
					data.token = builder.toString();
					data.next = i;
					break;
			}
		} else {																					// End of string.
			data.type = TokenType.END;
			data.token = null;
			data.next = -1;
		}
	}
	
	private String addressToName( TaskTree tree, TaskTreeAddress address ) {
		String name;

		name = null;
		for ( String segment : tree.getPathAt( address ) ) {
			name = segment;
		}
		return name;
	}
	
	private String treeToString( TaskTree tree ) throws IllegalAddressException, RemoteException {
		StringBuilder builder;
		
		builder = new StringBuilder();
		for ( TaskTreeAddress addr : tree.getChildrenAt( TaskTreeAddressBody.getRootAddress().getTreeAddress() ) ) {
			builder.append( elementToString( tree, addr ) );
		}
		return builder.toString();
	}
	
	private String elementToString( TaskTree tree, TaskTreeAddress address ) throws IllegalAddressException, RemoteException {
		StringBuilder builder;
		TaskTreeRecord triplet;
		
		builder = new StringBuilder();
		builder.append( ' ' ).append( addressToName( tree, address ) );
		triplet = tree.getRecordAt( address, false, true, false );
		switch ( triplet.getType() ) {
			case NODE:
				builder.append( '(' );
				for ( TaskTreeAddress addr : triplet.getChildren() ) {
					builder.append( elementToString( tree, addr ) );
				}
				builder.append( ')' );
				break;
			case LEAF:
				assertEquals( "Illegal leaf content!", TaskTreeAddressBody.segToString( tree.getPathAt( address ) ).replace( '/', '-' ), triplet.getTask().getTaskId() );
				break;
			default:
				throw new IllegalStateException( "The Impossible has happened." );
		}
		builder.append( ' ' );
		return builder.toString();
	}
	
	public static void main( String[] args ) {
		int levelCounter = 0;
		for ( String[][] treeTemplatesAll : TREES ) {
			for ( String[] treeTemplatesBySeed : treeTemplatesAll ) for ( String treeTemplate : treeTemplatesBySeed ) {
				System.out.println( levelCounter + ": " + treeTemplate );
			}
			++levelCounter;
		}
	}
}
