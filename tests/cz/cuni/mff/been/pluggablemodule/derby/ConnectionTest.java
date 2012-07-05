package cz.cuni.mff.been.pluggablemodule.derby;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
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
public class ConnectionTest {
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
	 * Creates a table, inserts two rows in it and runs
	 * a simple query on the table.
	 * @throws Exception
	 */
	@Test
	public void testInsertSelect() throws Exception {
		Connection con = module.getConnection(TESTING_DB_NAME);
		
		
		Statement statement = con.createStatement();
		
		// create table
		statement.execute("create table testtable (num int, addr varchar(40))");
		//System.out.println("created table testtable");
		
		// insert a record
		statement.execute("insert into testtable values (100, 'text1')");
		statement.execute("insert into testtable values (233, 'text2')");
		//System.out.println("inserted 2 testing values");
		
		statement.execute("select num, addr from testtable order by num");
		ResultSet results = statement.getResultSet();
		
		// check that the results are the correct ones
		if (!results.next()) throw new Exception("Result set is empty");
        Assert.assertEquals(100,results.getInt(1));
		
        if (!results.next()) throw new Exception("Missing record in resultset");
        Assert.assertEquals(233,results.getInt(1));
		
		con.close();
	}
	
	/** 
	 * Creates a table, inserts two rows in it and runs
	 * a simple query on the table.
	 * @throws Exception
	 */
	@Test
	public void testDropDatabase() throws Exception {
		Connection con = module.getConnection(TESTING_DB_NAME);
		
		
		Statement statement = con.createStatement();
		
		// create table
		statement.execute("create table testtable (num int, addr varchar(40))");
		
		// insert a record
		statement.execute("insert into testtable values (100, 'text1')");
		statement.execute("insert into testtable values (233, 'text2')");
		
		con.close();
		
		module.dropDatabase(TESTING_DB_NAME);
		
		con = module.getConnection(TESTING_DB_NAME);
		
		Statement statement2 = con.createStatement();
		statement2.execute("create table testtable (num int, addr varchar(40))");
		
		statement2.execute("select num, addr from testtable order by num");
		ResultSet results = statement2.getResultSet();
		
		// check that the results are the correct ones
		if (results.next()) throw new Exception("Result should be empty");
        
		con.close();
	}
	
	/**
	 * Backups a database and then tries to restore it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDatabaseBackup() throws Exception {
		Connection con = module.getConnection(TESTING_DB_NAME);
		
		Statement statement = con.createStatement();
		
		// create table
		statement.execute("create table testtable (num int, addr varchar(40))");
		
		// insert a record
		statement.execute("insert into testtable values (100, 'text1')");
		statement.execute("insert into testtable values (233, 'text2')");
		
		con.close();
		
		// backup database to a file
		File backupFile = new File("tmp/derby-testing-backup.bak");
		module.backupDatabase(TESTING_DB_NAME, backupFile);
		
		// drop database
		module.dropDatabase(TESTING_DB_NAME);
		
		// restore database from a file
		con = module.restoreDatabase(TESTING_DB_NAME, new File(backupFile.getPath() + File.separator + TESTING_DB_NAME));
		
		statement = con.createStatement();
		
		statement.execute("select num, addr from testtable order by num");
		ResultSet results = statement.getResultSet();
		
		// check that the results are the correct ones
		if (!results.next()) throw new Exception("Result set is empty");
        Assert.assertEquals(100,results.getInt(1));
		
        if (!results.next()) throw new Exception("Missing record in resultset");
        Assert.assertEquals(233,results.getInt(1));
		
		con.close();
	}
	
	/** 
	 * Initializes database with a setup script and then 
	 * checks whether database contains correct data.
	 * @throws Exception
	 */
	@Test
	public void testSetupDatabase() throws Exception {
		String setupScript =
			"create table testtable (num int, addr varchar(40));\n" +
			"insert into testtable values (100, 'text1');\n" +
			"insert into testtable values (233, 'text2');\n";
		
		InputStream istream = new ByteArrayInputStream( setupScript.getBytes("UTF-8") );
		
		Connection con = module.setupDatabase(TESTING_DB_NAME, istream);
		
		Statement statement = con.createStatement();
		
		statement.execute("select num, addr from testtable order by num");
		ResultSet results = statement.getResultSet();
		
		// check that the results are the correct ones
		if (!results.next()) throw new Exception("Result set is empty");
        Assert.assertEquals(100,results.getInt(1));
		
        if (!results.next()) throw new Exception("Missing record in resultset");
        Assert.assertEquals(233,results.getInt(1));
		
		con.close();
	}
	

}
