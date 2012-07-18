package cz.cuni.mff.been.pluggablemodule.derby;


import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.mff.been.pluggablemodule.MockPluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.implementation.DerbyPluggableModuleImpl;
import cz.cuni.mff.been.pluggablemodule.derby.implementation.DerbyProperties;

/**
 * Tests whether derby obeys derby.system.home property
 * @author Jan Tattermusch
 *
 */
public class DerbyHomeTest {
	private static DerbyPluggableModuleImpl module;
	
	/* testing database name */
	private final static String TESTING_DB_NAME = "testdb_kpaqwgixzl";
	
	/* testing derby.system.home property */
	private final static String TESTING_DB_HOME = "tmp/test_kpaqwgixzl";
	
	/** 
	 * Returns pluggable module manager's mock
	 * @return pluggable module manager.
	 */
	private static PluggableModuleManager getPluggableModuleManager() {
		return new MockPluggableModuleManager();
	}

	
	
	/** 
	 * Starts derby with given home property,
	 * creates a database, inserts some data into 
	 * it and checks whether directory with created
	 * database really exists.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDerbyHomeProperty() throws Exception {
		module = new DerbyPluggableModuleImpl( getPluggableModuleManager() );

		// start derby with given home property
        DerbyProperties derbyProperties = new DerbyProperties(TESTING_DB_HOME, false);
        module.startEngine(derbyProperties);
        
        module.dropDatabase(TESTING_DB_NAME);
		
		/* insert some data into database */ 
		Connection con = module.getConnection(TESTING_DB_NAME);
		Statement statement = con.createStatement();		
		// create table
		statement.execute("create table testtable (num int, addr varchar(40))");
		// insert a record
		statement.execute("insert into testtable values (100, 'text1')");
		con.close();
		
		module.stopEngine();
		
		File dir = new File(TESTING_DB_HOME);
		Assert.assertEquals(true, dir.exists());
		
	}
	
	
	
	

}
