package cz.cuni.mff.been.taskmanager.tasktree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.rmi.RemoteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskTreeNodeTest extends TaskTreeItemTest {

	private TaskTreeAddressBody address1, address2, address3, address4, address5;

	@Override @Before
	public void setUp() throws Exception {
		super.setUp();
		address1 = new TaskTreeAddressBody( "/one/ONE" );
		address2 = new TaskTreeAddressBody( "/one/TWO" );
		address3 = new TaskTreeAddressBody( "/one/THREE" );
		address4 = new TaskTreeAddressBody( "/one/FOUR" );
		address5 = new TaskTreeAddressBody( "/one/FIVE" );
	}

	@Test
	public void testNodeConstructor() {
		TaskTreeNode node;

		node = new TaskTreeNode( tree, ONE, ROOT );
		try {
			node.getTask();
			fail( "A task descriptor returned from a node." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong address instance returned.", ONE, node.getAddress() );
		assertSame( "Wrong parent address instance returned.", ROOT, node.getParentAddress() );
		try {
			node.getTask();
			fail( "A task descriptor returned from a node, second attempt." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong address instance returned, second invocation.", ONE, node.getAddress() );
		assertSame( "Wrong parent address instance returned, second invocation.", ROOT, node.getParentAddress() );

		node = new TaskTreeNode( tree, ONE_TWO, ONE );
		assertSame( "Wrong address instance returned", ONE_TWO, node.getAddress() );
		assertSame( "Wrong parent address instance returned.", ONE, node.getParentAddress() );
		try {
			node.getTask();
			fail( "A task descriptor returned from a node." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong address instance returned, second invocation.", ONE_TWO, node.getAddress() );
		assertSame( "Wrong parent address instance returned, second invocation.", ONE, node.getParentAddress() );
		try {
			node.getTask();
			fail( "A task descriptor returned from a node, second attempt." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
	}

	@Test
	public void testNodeConstructorEfficientNoChecks() {
		TaskTreeNode node;

		// No sanity checks inside the package, so this should pass, too.
		// This is a nonsense that will never occur... Just documenting this class' behavior.

		node = new TaskTreeNode( tree, ROOT, ONE );
		assertSame( "Wrong address instance returned.", ROOT, node.getAddress() );
		assertSame( "Wrong parent address instance returned.", ONE, node.getParentAddress() );
		try {
			node.getTask();
			fail( "A task descriptor returned from a node." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong address instance returned, second invocation.", ROOT, node.getAddress() );
		assertSame( "Wrong parent address instance returned, second invocation.", ONE, node.getParentAddress() );
		try {
			node.getTask();
			fail( "A task descriptor returned from a node, second attempt." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}

		node = new TaskTreeNode( tree, ONE, ONE_TWO );
		try {
			node.getTask();
			fail( "A task descriptor returned from a node." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong address instance returned.", ONE, node.getAddress() );
		assertSame( "Wrong parent address instance returned.", ONE_TWO, node.getParentAddress() );
		try {
			node.getTask();
			fail( "A task descriptor returned from a node, second attempt." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong address instance returned, second invocation.", ONE, node.getAddress() );
		assertSame( "Wrong parent address instance returned, second invocation.", ONE_TWO, node.getParentAddress() );
	}

	@Test
	public void testWhoAreYou() {
		TaskTreeNode node;

		node = new TaskTreeNode( tree, ONE, ROOT );
		assertEquals( "Wrong item type returned, first atempt.", TaskTreeBasic.Type.NODE, node.getType() );
		assertEquals( "Wrong item type returned, second attempt.", TaskTreeBasic.Type.NODE, node.getType() );

		// Again, this will never happen, but must not change item type anyway. ;-) 
		node = new TaskTreeNode( tree, ONE, ONE_TWO );
		assertEquals( "Wrong item type returned, first atempt.", TaskTreeBasic.Type.NODE, node.getType() );
		assertEquals( "Wrong item type returned, second attempt.", TaskTreeBasic.Type.NODE, node.getType() );
	}

	/*
	 * NOTE: Illegal (misplaced) child addresses are neither expected to be checked nor tested!
	 * This is not a public class, so there's no reason to test these pathological cases. 
	 */

	@Test
	public void testAddChild() throws RemoteException {
		TaskTreeNode node;

		node = new TaskTreeNode( tree, ONE, ROOT );
		assertContains( node );
		node.addChild( address1 );
		assertContains( node, address1 );
		node.addChild( address2 );
		assertContains( node, address1, address2 );
		node.addChild( address3 );
		assertContains( node, address1, address2, address3 );
		assertContains( node, address1, address2, address3 );
		node.addChild( address4 );
		assertContains( node, address1, address2, address3, address4 );
		node.addChild( address5 );
		assertContains( node, address1, address2, address3, address4, address5 );
		assertContains( node, address1, address2, address3, address4, address5 );

		node = new TaskTreeNode( tree, ONE, ROOT );
		node.addChild( address5 );
		node.addChild( address4 );
		assertContains( node, address5, address4 );
		assertContains( node, address5, address4 );
		assertContains( node, address5, address4 );
		node.addChild( address3 );
		node.addChild( address2	);
		assertContains( node, address5, address4, address3, address2 );
		assertContains( node, address5, address4, address3, address2 );
		node.addChild( address1 );
		assertContains( node, address5, address4, address3, address2, address1 );
		assertContains( node, address5, address4, address3, address2, address1 );
		assertContains( node, address5, address4, address3, address2, address1 );		
	}

	@Test
	public void testAddChild_duplicate() throws RemoteException {
		TaskTreeNode node;

		// One duplicate child
		node = new TaskTreeNode( tree, ONE, ROOT ); 
		node.addChild( address3 );
		assertContains( node, address3 );
		node.addChild( address3 );
		assertContains( node, address3 );
		assertContains( node, address3 );
		node.addChild( address3 );
		assertContains( node, address3 );

		// Two children
		node.addChild( address2 );
		assertContains( node, address3, address2 );
		node.addChild( address2 );
		assertContains( node, address3, address2 );
		node.addChild( address3 );
		assertContains( node, address2, address3 );
		assertContains( node, address2, address3 );
		node.addChild( address3 );
		assertContains( node, address2, address3 );
		node.addChild( address2 );
		assertContains( node, address3, address2 );
		assertContains( node, address3, address2 );

		node = new TaskTreeNode( tree, ONE, ROOT );
		node.addChild( address1 );
		node.addChild( address5 );
		node.addChild( address1 );
		assertContains( node, address5, address1 );
		node.addChild( address5 );
		assertContains( node, address1, address5 );

		// Multiple children.
		TaskTreeAddressBody[] addrs;
		node = new TaskTreeNode( tree, ONE, ROOT );

		addrs = new TaskTreeAddressBody[] { address5, address4, address3, address2, address1 };
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		assertContains( node, addrs );
		for ( int i = 0; i < 2; ++i ) {
			node.addChild( address3 ); // Middle to end.
			assertContains( node, address5, address4, address2, address1, address3 );
		}
		for ( int i = 0; i < 3; ++i ) {
			node.addChild( address4 ); // Second to end. 
			assertContains( node, address5, address2, address1, address3, address4 );
		}
		for ( int i = 0; i < 1; ++i ) {
			node.addChild( address3 ); // Fourth to end.
			assertContains( node, address5, address2, address1, address4, address3 );
		}
		for ( int i = 0; i < 3; ++i ) {
			node.addChild( address5 ); // First to end.
			assertContains( node, address2, address1, address4, address3, address5 );
		}

		addrs = new TaskTreeAddressBody[] { address1, address2, address3, address4, address5 };	
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }		
		assertContains( node, addrs );
		assertContains( node, addrs );
		assertContains( node, addrs );
	}

	@Test
	public void testRemoveChild_nonexistent() throws IllegalAddressException, RemoteException {
		TaskTreeNode node;

		// Remove from nothing.
		node = new TaskTreeNode( tree, ONE_TWO, ONE );
		try {
			node.removeChild( address3 );
			fail( "Removal from an empty node succeeded." );
		} catch ( IllegalAddressException exception ) {
			// This is what we want.
		}

		// Remove one child
		node = new TaskTreeNode( tree, ONE_TWO, ONE );
		node.addChild( address5 );
		node.removeChild( address5 );
		assertContains( node );
		try {
			node.removeChild( address5 );
			fail( "Removal from an empty node succeeded" );
		} catch ( IllegalAddressException exception ) {
			// This is what we want.
		}

		// Remove, add and a few checks
		node = new TaskTreeNode( tree, ONE_TWO, ONE );
		try {
			node.removeChild( address3 );
			fail( "Removal from an empty node succeeded." );
		} catch ( IllegalAddressException exception ) {
			// This is what we want.
		}
		assertContains( node );
		node.addChild( address3 );
		try {
			node.removeChild( address5 );
			fail( "Removal of a nonexistent child succeeded" );
		} catch ( IllegalAddressException exception ) {
			// This is what we want.
		}
		node.addChild( address5 );
		assertContains( node, address3, address5 );
		node.removeChild( address3 );
		assertContains( node, address5 );
		node.removeChild( address5 );
		assertContains( node );

		// Multiple elements
		node = new TaskTreeNode( tree, ONE_TWO, ONE );
		node.addChild( address5 );
		node.addChild( address3 );
		node.addChild( address1 );
		try {
			node.removeChild( address2 );
			fail( "Nonexistent child removal succeeded." );
		} catch ( IllegalAddressException exception ) {
			// This is what we want.
		}
		assertContains( node, address5, address3, address1 );
		try {
			node.removeChild( address4 );
			fail( "Nonexistent child removal succeeded" );
		} catch ( IllegalAddressException exception ) {
			// This is what we want.
		}
		try {
			node.removeChild( address2 );
			fail( "Nonexistent child removal succeeded on second attempt." );
		} catch ( IllegalAddressException exception ) {
			// This is what we want.
		}
		assertContains( node, address5, address3, address1 );
		node.addChild( address2 );
		node.addChild( address4 );
		node.removeChild( address3 );
		try {
			node.removeChild( address3 );
			fail( "Nonexistent child removal succeeded after manipulation." );
		} catch ( IllegalAddressException exception ) {
			// This is what we want.
		}
		assertContains( node, address5, address1, address2, address4 );
		node.removeChild( address1 );
		assertContains( node, address5, address2, address4 );
		node.removeChild( address5 );
		assertContains( node, address2, address4 );
		node.addChild( address2 );
		assertContains( node, address4, address2 );
		for ( int i = 0; i < 3; ++i ) {
			for ( TaskTreeAddressBody addr : new TaskTreeAddressBody[] { address1, address3, address5 } ) {
				try {
					node.removeChild( addr );
					fail( "Succeeded in attempt " + i + " and address " + addr );
				} catch ( IllegalAddressException exception ) {
					// This is what we want.
				}
			}
		}
		node.removeChild( address2 );
		assertContains( node, address4 );
		node.removeChild( address4 );
		assertContains( node );
		for ( int i = 0; i < 2; ++i ) {
			for ( TaskTreeAddressBody addr : new TaskTreeAddressBody[] { address2, address1, address4 } ) {
				try {
					node.removeChild( addr );
					fail( "Succeeded in attempt " + i + " and address " + addr );
				} catch ( IllegalAddressException exception ) {
					// This is what we want.
				}
			}
		}
		assertContains( node );
	}

	@Test
	public void testRemoveChild() throws IllegalAddressException, RemoteException {
		// Testing only valid removals here, pathological cases tested above.
		TaskTreeNode node;
		
		// One child...
		node = new TaskTreeNode( tree, ONE, ROOT );
		node.addChild( address1 );
		node.removeChild( address1 );
		assertContains( node );
		node.addChild( address1 );
		node.removeChild( address1 );
		assertContains( node );
		node.addChild( address5 );
		assertContains( node, address5 );
		node.removeChild( address5 );
		node.addChild( address5 );
		node.removeChild( address5 );
		assertContains( node );
		
		// Two children
		node = new TaskTreeNode( tree, ONE, ROOT );
		node.addChild( address2 );
		node.addChild( address1 );
		node.removeChild( address2 );
		assertContains( node, address1 );
		node.removeChild( address1 );
		assertContains( node );
		node.addChild( address1 );
		node.addChild( address2 );
		assertContains( node, address1, address2 );
		node.removeChild( address2 );
		assertContains( node, address1 );
		node.removeChild( address1 );
		assertContains( node );
		
		// Three children, three removal orders
		TaskTreeAddressBody[] addrs;
		addrs = new TaskTreeAddressBody[] { address2, address1, address3 };
		node = new TaskTreeNode( tree, ONE, ROOT );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		for ( TaskTreeAddressBody addr : addrs ) { node.removeChild( addr ); }
		assertContains( node );
		node = new TaskTreeNode( tree, ONE, ROOT );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		node.removeChild( address1 );
		assertContains( node, address2, address3 );
		node.removeChild( address3 );
		assertContains( node, address2 );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		node.removeChild( address3 );
		assertContains( node, address2, address1 );
		node.removeChild( address2 );
		assertContains( node, address1 );
		
		// Multiple children
		addrs = new TaskTreeAddressBody[] { address1, address2, address3, address4, address5 };
		node = new TaskTreeNode( tree, ONE, ROOT );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		node.removeChild( address1 );
		node.removeChild( address2 );
		assertContains( node, address3, address4, address5 );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		node.removeChild( address2 );
		node.removeChild( address3 );
		assertContains( node, address1, address4, address5 );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		node.removeChild( address3 );
		node.removeChild( address4 );
		assertContains( node, address1, address2, address5 );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		node.removeChild( address4 );
		node.removeChild( address5 );
		assertContains( node, address1, address2, address3 );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		assertContains( node, address1, address2, address3, address4, address5 );
		node.removeChild( address5 );
		node.removeChild( address4 );
		assertContains( node, address1, address2, address3 );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		node.removeChild( address4 );
		node.removeChild( address3 );
		assertContains( node, address1, address2, address5 );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		node.removeChild( address3 );
		node.removeChild( address2 );
		assertContains( node, address1, address4, address5 );
		for ( TaskTreeAddressBody addr : addrs ) { node.addChild( addr ); }
		node.removeChild( address2 );
		node.removeChild( address1 );
		assertContains( node, address3, address4, address5 );
		node.removeChild( address4 );
		assertContains( node, address3, address5 );
		node.removeChild( address5 );
		assertContains( node, address3 );
		node.removeChild( address3 );
		assertContains( node );
	}

	private void assertContains( TaskTreeNode node, TaskTreeAddressBody ... children ) {
		TaskTreeAddressBody[] localChildren;
		int i;

		localChildren = node.getChildren().toArray( new TaskTreeAddressBody[ node.getChildren().size() ] );
		i = 0;
		for ( TaskTreeAddressBody child : children ) {
			assertTrue( "Too few children!", i < localChildren.length );
			assertSame( "Wrong child returned", child, localChildren[ i++ ] );
		}
		assertFalse( "Too many children!", i < localChildren.length );
	}

	@Override @After
	public void tearDown() throws Exception {
		address1 = address2 = address3 = address4 = address5 = null;
		super.tearDown();
	}
}
