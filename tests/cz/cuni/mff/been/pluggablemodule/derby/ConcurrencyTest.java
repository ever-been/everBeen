package cz.cuni.mff.been.pluggablemodule.derby;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.mff.been.pluggablemodule.MockPluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.implementation.DerbyPluggableModuleImpl;
import cz.cuni.mff.been.pluggablemodule.derby.implementation.DerbyProperties;

/**
 * Testing of derby connection by executing sql queries.
 * @author Jan Tattermusch
 *
 */
public class ConcurrencyTest {
	private static DerbyPluggableModuleImpl module;
	
	/* testing database name */
	private final static String TESTING_DB_NAME = "testdb";
	
	/** 
	 * Returns pluggable module manager's mock
	 * @return pluggable module manager.
	 */
	private static PluggableModuleManager getPluggableModuleManager() {
		return new MockPluggableModuleManager();
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		module = new DerbyPluggableModuleImpl( getPluggableModuleManager() );

        // start derby with network access option
        DerbyProperties derbyProperties = new DerbyProperties("tmp", true);
        module.startEngine(derbyProperties);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		module.dropDatabase(TESTING_DB_NAME);
		module.stopEngine();
	}

	@Before
	public void setUp() throws Exception {
		module.dropDatabase(TESTING_DB_NAME);
	}

	/** 
	 * Drop the database we've been working with.
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		
	}
	
	/** 
	 * Test whether underlying database supports transaction concurrency correctly.
	 * @throws Exception
	 */
	@Test
	public void testInsertSelect() throws Exception {
		Connection con = module.getConnection(TESTING_DB_NAME);
		Connection con1 = module.getConnection(TESTING_DB_NAME);
		Connection con2 = module.getConnection(TESTING_DB_NAME);
		
		con1.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		con2.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		con1.setAutoCommit(false);
		con2.setAutoCommit(false);
		
		Statement statement = con.createStatement();
		statement.execute("create table testtable (num int, addr varchar(40))");
		con.close();
		
		
		TestThread thread1 = new TestThread(con1, "THREAD1");
		TestThread thread2 = new TestThread(con2, "THREAD2");
		
		thread1.start();
		thread2.start();
		
		thread1.join();
		thread2.join();
		
		Assert.assertTrue( thread1.isSucceeded());
		Assert.assertTrue( thread2.isSucceeded());
		
	}
	
	class TestThread extends Thread {
		
		private Connection connection;
		private String threadName;
		
		private boolean succeeded = false;
		
		public TestThread(Connection connection, String threadName) {
			super();
			this.connection = connection;
			this.threadName = threadName;
		}

		@Override
		public void run() {
			try {
				
				msg("thread started");
				
				for (int i = 0; i < 20; i++) {
					msg("loop " + i);
					
					Statement statement = connection.createStatement();
					ResultSet results;
					int c;
					
					statement.execute("insert into testtable values (100, 'text1')");
					msg("inserted data1");
					
					Thread.sleep(100);

					statement.execute("select num, addr from testtable order by num");
					results = statement.getResultSet();
					
					c = 0;
					while(results.next()) {
						c++;
					}
					msg(c +" rows visible");
					
					Thread.sleep(100);
					
					statement.execute("insert into testtable values (200, 'text2')");
					msg("inserted data1");
					
					Thread.sleep(100);

					statement.execute("select num, addr from testtable order by num");
					results = statement.getResultSet();
					
					c = 0;
					while(results.next()) {
						c++;
					}
					msg(c +" rows visible");
					
					Thread.sleep(100);
					
					connection.commit();
					msg("data commited");

				}		
				connection.close();
				msg("thread suceeded");
				succeeded = true;
				msg("finishing");

			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		private void msg(String s) {
			System.err.println(threadName + ": " + s);
		}

		public boolean isSucceeded() {
			return succeeded;
		}
	};

}
