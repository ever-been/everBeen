package cz.cuni.mff.been.taskmanager.tasktree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.been.taskmanager.data.TaskEntry;

public class TaskTreeLeafTest extends TaskTreeItemTest {
	
	private TaskEntry ENTRY_ONE;
	private TaskEntry ENTRY_TWO;
	private TaskEntry ENTRY_ONE_TWO;

	@Override @Before
	public void setUp() throws Exception {
		super.setUp();
		ENTRY_ONE = new TaskEntry( "one" );
		ENTRY_TWO = new TaskEntry( "two" );
		ENTRY_ONE_TWO = new TaskEntry( "three" );
	}

	@Test
	public void testNodeConstructor() {
		TaskTreeLeaf leaf;
		
		leaf = new TaskTreeLeaf( tree, ONE, ROOT, ENTRY_ONE );
		try {
			leaf.getChildren();
			fail( "Subnodes returned from a leaf." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong task descriptor returned, first attempt.", ENTRY_ONE, leaf.getTask() );
		assertSame( "Wrong address instance returned.", ONE, leaf.getAddress() );
		assertSame( "Wrong task descriptor returned, second attempt.", ENTRY_ONE, leaf.getTask() );
		assertSame( "Wrong parent address instance returned.", ROOT, leaf.getParentAddress() );
		assertSame( "Wrong task descriptor returned, third attempt.", ENTRY_ONE, leaf.getTask() );
		try {
			leaf.getChildren();
			fail( "Subnodes returned from a leaf on second attempt." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong address instance returned, second invocation.", ONE, leaf.getAddress() );
		assertSame( "Wrong parent address instance returned, second invocation.", ROOT, leaf.getParentAddress() );

		leaf = new TaskTreeLeaf( tree, ONE_TWO, ROOT, ENTRY_ONE_TWO );
		assertSame( "Wrong address instance returned", ONE_TWO, leaf.getAddress() );
		assertSame( "Wrong parent address instance returned.", ROOT, leaf.getParentAddress() );
		try {
			leaf.getChildren();
			fail( "Subnodes returned from a leaf." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong task descriptor returned, first attempt.", ENTRY_ONE_TWO, leaf.getTask() );
		assertSame( "Wrong address instance returned, second invocation.", ONE_TWO, leaf.getAddress() );
		assertSame( "Wrong task descriptor returned, second attempt.", ENTRY_ONE_TWO, leaf.getTask() );
		assertSame( "Wrong parent address instance returned, second invocation.", ROOT, leaf.getParentAddress() );
		assertSame( "Wrong task descriptor returned, third attempt.", ENTRY_ONE_TWO, leaf.getTask() );
		try {
			leaf.getChildren();
			fail( "Subnodes returned from a leaf on second attempt." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
	}
	
	@Test
	public void testLeafConstructorEfficientNoChecks() {
		TaskTreeLeaf leaf;
		
		// No sanity checks inside the package, so this should pass, too.
		// This is a nonsense that will never occur... Just documenting this class' behavior.
		
		leaf = new TaskTreeLeaf( tree, ROOT, ONE, ENTRY_TWO );
		assertSame( "Wrong address instance returned.", ROOT, leaf.getAddress() );
		assertSame( "Wrong parent address instance returned.", ONE, leaf.getParentAddress() );
		try {
			leaf.getChildren();
			fail( "Subnodes returned from a leaf." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong task descriptor returned, first attempt.", ENTRY_TWO, leaf.getTask() );
		assertSame( "Wrong address instance returned, second invocation.", ROOT, leaf.getAddress() );
		assertSame( "Wrong task descriptor returned, second attempt.", ENTRY_TWO, leaf.getTask() );
		assertSame( "Wrong parent address instance returned, second invocation.", ONE, leaf.getParentAddress() );
		assertSame( "Wrong task descriptor returned, third attempt.", ENTRY_TWO, leaf.getTask() );
		try {
			leaf.getChildren();
			fail( "Subnodes returned from a leaf on second attempt." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}

		leaf = new TaskTreeLeaf( tree, ONE, ONE_TWO, ENTRY_ONE_TWO );
		try {
			leaf.getChildren();
			fail( "Subnodes returned from a leaf." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong task descriptor returned, first attempt.", ENTRY_ONE_TWO, leaf.getTask() );
		assertSame( "Wrong address instance returned.", ONE, leaf.getAddress() );
		assertSame( "Wrong task descriptor returned, second attempt.", ENTRY_ONE_TWO, leaf.getTask() );
		assertSame( "Wrong parent address instance returned.", ONE_TWO, leaf.getParentAddress() );
		assertSame( "Wrong task descriptor returned, third attempt.", ENTRY_ONE_TWO, leaf.getTask() );
		try {
			leaf.getChildren();
			fail( "Subnodes returned from a leaf on second attempt." );
		} catch ( IllegalAddressException exception ) {
			// That's what we want.
		}
		assertSame( "Wrong address instance returned, second invocation.", ONE, leaf.getAddress() );
		assertSame( "Wrong parent address instance returned, second invocation.", ONE_TWO, leaf.getParentAddress() );
	}
	
	@Test
	public void testWhoAreYou() {
		TaskTreeLeaf leaf;

		leaf = new TaskTreeLeaf( tree, ONE, ROOT, ENTRY_ONE );
		assertEquals( "Wrong item type returned, first atempt.", TaskTreeBasic.Type.LEAF, leaf.getType() );
		assertEquals( "Wrong item type returned, second attempt.", TaskTreeBasic.Type.LEAF, leaf.getType() );

		// Again, this will never happen, but must not change item type anyway. ;-) 
		leaf = new TaskTreeLeaf( tree, ONE, ONE_TWO, ENTRY_ONE_TWO );
		assertEquals( "Wrong item type returned, first atempt.", TaskTreeBasic.Type.LEAF, leaf.getType() );
		assertEquals( "Wrong item type returned, second attempt.", TaskTreeBasic.Type.LEAF, leaf.getType() );
	}
	
	@Override @After
	public void tearDown() throws Exception {
		ENTRY_ONE = null;
		ENTRY_TWO = null;
		ENTRY_ONE_TWO = null;
		super.tearDown();
	}
}
