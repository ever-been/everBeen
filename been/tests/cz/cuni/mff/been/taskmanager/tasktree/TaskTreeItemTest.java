package cz.cuni.mff.been.taskmanager.tasktree;

import static org.junit.Assert.assertSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskTreeItemTest {
	
	protected TaskTreeAddressBody ROOT;
	protected TaskTreeAddressBody ONE;
	protected TaskTreeAddressBody ONE_TWO;
	protected TaskTree tree;

	@Before
	public void setUp() throws Exception {
		ROOT = TaskTreeAddressBody.getRootAddress();
		ONE = new TaskTreeAddressBody( "/one" );
		ONE_TWO = new TaskTreeAddressBody( "/one/two" );
		tree = new TaskTree();
	}
	
	@Test
	public void testConstructor() {
		TaskTreeItemSpy spy;
		
		spy = new TaskTreeItemSpy( tree, ONE, ROOT );
		assertSame( "Wrong address instance returned.", ONE, spy.getAddress() );
		assertSame( "Wrong parent address instance returned.", ROOT, spy.getParentAddress() );
		assertSame( "Wrong address instance returned, second invocation.", ONE, spy.getAddress() );
		assertSame( "Wrong parent address instance returned, second invocation.", ROOT, spy.getParentAddress() );

		spy = new TaskTreeItemSpy( tree, ONE_TWO, ONE );
		assertSame( "Wrong address instance returned", ONE_TWO, spy.getAddress() );
		assertSame( "Wrong parent address instance returned.", ONE, spy.getParentAddress() );
		assertSame( "Wrong address instance returned, second invocation.", ONE_TWO, spy.getAddress() );
		assertSame( "Wrong parent address instance returned, second invocation.", ONE, spy.getParentAddress() );
	}
	
	@Test
	public void testConstructorEfficientNoChecks() {
		TaskTreeItemSpy spy;
		
		// No sanity checks inside the package, so this should pass, too.
		// This is a nonsense that will never occur... Just documenting this class' behavior.
		
		spy = new TaskTreeItemSpy( tree, ROOT, ONE );
		assertSame( "Wrong address instance returned.", ROOT, spy.getAddress() );
		assertSame( "Wrong parent address instance returned.", ONE, spy.getParentAddress() );
		assertSame( "Wrong address instance returned, second invocation.", ROOT, spy.getAddress() );
		assertSame( "Wrong parent address instance returned, second invocation.", ONE, spy.getParentAddress() );

		spy = new TaskTreeItemSpy( tree, ONE, ONE_TWO );
		assertSame( "Wrong address instance returned.", ONE, spy.getAddress() );
		assertSame( "Wrong parent address instance returned.", ONE_TWO, spy.getParentAddress() );
		assertSame( "Wrong address instance returned, second invocation.", ONE, spy.getAddress() );
		assertSame( "Wrong parent address instance returned, second invocation.", ONE_TWO, spy.getParentAddress() );
	}

	@After
	public void tearDown() throws Exception {
		tree = null;
		ROOT = null;
		ONE = null;
		ONE_TWO = null;
	}
}
