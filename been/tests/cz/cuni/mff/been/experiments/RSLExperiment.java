package cz.cuni.mff.been.experiments;

import java.util.TreeMap;

import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.ContainerProperty;
import cz.cuni.mff.been.common.rsl.LongWithUnit;
import cz.cuni.mff.been.common.rsl.ParseException;
import cz.cuni.mff.been.common.rsl.ParserWrapper;
import cz.cuni.mff.been.common.rsl.Property;
import cz.cuni.mff.been.common.rsl.SimpleProperty;

public final class RSLExperiment {
	
	private interface Node extends Property {
		Node getSubnode( String name ) throws WrongInputException;
		void addProperty( String name, Node value ) throws WrongInputException;
		void ping() throws WrongInputException;
	}
	
	private final class LeafNode implements Node, SimpleProperty {
		private final Object value;
		private final Class< ? > valueClass;
		
		< T > LeafNode( T value ) {
			this.value = value;
			this.valueClass = value.getClass();
		}
		
		@Override
		public Object getValue() {
			return value;
		}
		
		@Override
		public Class< ? > getValueClass() {
			return valueClass;
		}
		
		@Override
		public void addProperty( String name, Node value ) throws WrongInputException {
			throw new WrongInputException( "Leaves don't accept properties." );
		}
		
		@Override
		public Node getSubnode( String name ) throws WrongInputException {
			throw new WrongInputException( "Leaves don't have subnodes." );
		}
		
		@Override
		public void ping() throws WrongInputException {
			throw new WrongInputException( "Attempted to change a leaf." );
		}
	}
	
	private final class InternalNode implements Node, ContainerProperty {
		private final TreeMap< String, Node > properties;

		public InternalNode() {
			properties = new TreeMap< String, Node >();
		}
		
		@Override
		public Property getProperty( String name ) {
			return properties.get( name );
		}
		@Override
		public boolean hasProperty( String name ) {
			return properties.containsKey( name );
		}
		
		@Override
		public void addProperty( String name, Node value ) throws WrongInputException {
			if ( properties.put( name, value ) != null ) {
				throw new WrongInputException( "Property reassignment." );
			}
		}
		
		@Override
		public Node getSubnode( String name ) throws WrongInputException {
			Node result;
			
			if ( ( result = properties.get( name ) ) != null ) {
				result.ping();
			}
			return result;
		}
		
		@Override
		public void ping() {
		}
	}
	
	private final InternalNode root;
	
	private RSLExperiment() {
		root = new InternalNode();
	}
	
	private void addVariable( String variable ) throws WrongInputException {
		String[] splitted;
		String value;
		Node node, subnode;
		LongWithUnit lwu;
		int i;
		
		splitted = variable.split( "=", -1 );
		if ( splitted.length != 2 ) {
			throw new WrongInputException( "Something wrong with '='." );
		}
		value = splitted[ 1 ];
		try {
			lwu = new LongWithUnit( value );
		} catch ( IllegalArgumentException exception ) {
			lwu = null;
		}
		
		splitted = splitted[ 0 ].split( "\\.", -1 );
		if ( splitted.length == 0 ) {
			throw new WrongInputException( "Something wrong with name." );
		}
		node = subnode = root;
		for ( i = 0; i < splitted.length && subnode != null; ++i ) {
			node = subnode;
			subnode = node.getSubnode( splitted[ i ] );
		}
		for ( --i; i < splitted.length - 1; ++i ) {
			node.addProperty( splitted[ i ], node = new InternalNode() );
		}
		node.addProperty( splitted[ i ], new LeafNode( lwu == null ? value : lwu ) );
	}
	
	private boolean evaluate( Condition condition ) {
		return condition.evaluate( root );
	}
	
	public static void main( String[] args ) {
		RSLExperiment experiment;
		Condition condition;
		
		try {
			if ( args.length < 2 ) {
				throw new WrongInputException(
					"Usage: java cz.cuni.mff.been.experiments.RSLExperiment <RSL> <variables>"
				);
			}
			condition = ParserWrapper.parseString( args[ 0 ] );
			experiment = new RSLExperiment();
			for ( int i = 1; i < args.length; ++i ) {
				experiment.addVariable( args[ i ] );
			}
			if ( experiment.evaluate( condition ) ) {
				System.out.println( "Condition is TRUE." );
			} else {
				System.out.println( "Condition is FALSE." );
			}
		} catch ( ParseException exception ) {
			System.err.println( "RSL parser exception." );
			System.err.println( exception.getMessage() );
			System.exit( -1 );
		} catch ( WrongInputException exception ) {
			System.err.println( "Command line parser exception." );
			System.err.println( exception.getMessage() );
			System.exit( -1 );
		}	
	}
}

final class WrongInputException extends Exception {
	private static final long	serialVersionUID	= 7574788663954071402L;

	public WrongInputException( String message ) {
		super( message );
	}
}

