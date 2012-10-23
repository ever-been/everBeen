/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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

package cz.cuni.mff.been.pluggablemodule.derby.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.derby.drda.NetworkServerControl;
import org.apache.derby.tools.ij;
import org.apache.log4j.Logger;

import cz.cuni.mff.been.core.utils.FileUtils;
import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModule;

/**
 * BEEN's pluggable module which provides Derby functionality (starting and
 * stopping database engine, connecting to databases and performing backup and
 * setup steps)
 * 
 * @author Jan Tattermusch
 */
public class DerbyPluggableModuleImpl extends PluggableModule implements DerbyPluggableModule {

	public static final int DERBY_PORT_NUMBER = 1527;

	/** Sets up a class logger */
	private Logger logger = Logger.getLogger("task.derbypluggablemodule." + this.getClass().getCanonicalName());

	/** Properties of running Derby instance */
	private DerbyProperties properties = null;

	/**
	 * Name of Derby's embedded driver
	 */
	private static final String driverName = "org.apache.derby.jdbc.EmbeddedDriver";

	/**
	 * Name of protocol used by Derby
	 */
	private static final String derbyProtocol = "jdbc:derby:";

	/**
	 * Creates new instance of derby pluggable module.
	 * 
	 * @param manager
	 *          pluggable module manager used for loading this pluggable module.
	 */
	public DerbyPluggableModuleImpl(PluggableModuleManager manager) {
		super(manager);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#startEngine(DerbyProperties)
	 */
	public void startEngine(DerbyProperties derbyProperties) throws DerbyPluggableModuleException {
		/* set derby home directory */
		System.setProperty("derby.system.home", derbyProperties.getHome());

		/* force locking database to prevent other JVMs interfere */
		//System.setProperty("derby.database.forceDatabaseLock", "true");

		/* set whether derby should be network accessible */
		System.setProperty("derby.drda.startNetworkServer", (derbyProperties.isNetworkAccessible()
				? "true" : "false"));

		/* set network port */
		System.setProperty("derby.drda.portNumber", Integer.toString(derbyProperties.getNetworkPort()));

		/* this option prevents derby from getting deadlocks and lock timeouts all the time */
		System.setProperty("derby.storage.rowLocking", "false");

		/* redirect derby's error output */
		//System
		//		.setProperty("derby.stream.error.method",
		//		DerbyOutputStream.class.getCanonicalName() + ".getInstance");
		try {
			loadDriver();
		} catch (Exception ex) {
			throw new DerbyPluggableModuleException("Could not load Derby embedded driver", ex);
		}
		logger.info("Derby embedded driver loaded.");

		if (derbyProperties.isNetworkAccessible()) {
			try {
				waitForStart();
				logger.debug("Derby network server started.");
			} catch (Exception ex) {
				throw new DerbyPluggableModuleException("Attempt to ping Derby network server failed", ex);
			}
		}
		this.properties = derbyProperties;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#startEngine(String, boolean)
	 */
	public void startEngine(String home, boolean networkAccessible) throws DerbyPluggableModuleException {

		startEngine(new DerbyProperties(home, networkAccessible));
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#startEngine(String, boolean, int)
	 */
	public void startEngine(String home, boolean networkAccessible,
			int networkPort) throws DerbyPluggableModuleException {

		startEngine(new DerbyProperties(home, networkAccessible, networkPort));
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#stopEngine()
	 */
	public void stopEngine() throws DerbyPluggableModuleException {
		try {
			shutdownDerby();
			this.properties = null;
			logger.info("Derby engine successfully stopped.");
		} catch (SQLException ex) {
			throw new DerbyPluggableModuleException("Failed shutting down Derby.", ex);
		}

	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#getConnection(java.lang.String)
	 */
	public Connection getConnection(String databaseName) throws SQLException {
		return DriverManager.getConnection(derbyProtocol + databaseName + ";create=true");
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#getConnection(java.lang.String, java.util.Properties)
	 */
	public Connection getConnection(String databaseName, Properties properties) throws SQLException {
		return DriverManager.getConnection(derbyProtocol + databaseName + ";create=true", properties);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#getNetworkServerURL(java.lang.String)
	 */
	public String getNetworkServerURL(String hostname) {
		if (properties != null && properties.isNetworkAccessible()) {

			String portString = (new Integer(DERBY_PORT_NUMBER)).toString();
			return derbyProtocol + "//" + hostname + ":" + portString + "/";

		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#setupDatabase(java.lang.String, java.io.InputStream)
	 */
	public Connection setupDatabase(String databaseName,
			InputStream setupScriptStream) throws DerbyPluggableModuleException {

		dropDatabase(databaseName);

		Connection connection = null;
		try {
			connection = getConnection(databaseName);
		} catch (SQLException ex) {
			throw new DerbyPluggableModuleException("Cannot connect to database \"" + databaseName + "\"", ex);
		}

		String inputEncoding = "UTF-8";
		String outputEncoding = "UTF-8";

		try {
			logger.debug("Launching setup script for database \"" + databaseName + "\"");
			OutputStream ijOutput = new IjOutputStream();
			int sqlExceptionCount = ij.runScript(connection, setupScriptStream, inputEncoding, ijOutput, outputEncoding);
			if (sqlExceptionCount == 0) {
				logger.debug("Setup for database \"" + databaseName + "\" finished successfully.");
				return connection;
			} else {
				throw new DerbyPluggableModuleException(sqlExceptionCount + " SQL exceptions occured when running database setup script for DB \"" + databaseName + "\"");
			}

		} catch (UnsupportedEncodingException ex) {
			throw new DerbyPluggableModuleException("Unsupported encoding of setup script or error output.", ex);
		}

	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#setupDatabase(java.lang.String, java.io.File)
	 */
	public Connection setupDatabase(String databaseName, File setupScriptFile) throws FileNotFoundException, DerbyPluggableModuleException {
		InputStream setupScriptStream = new FileInputStream(setupScriptFile);
		return setupDatabase(databaseName, setupScriptStream);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#restoreDatabase(java.lang.String, java.io.File)
	 */
	public java.sql.Connection restoreDatabase(String databaseName,
			File backupFile) throws DerbyPluggableModuleException {

		dropDatabase(databaseName);
		try {
			Connection connection = DriverManager.getConnection(derbyProtocol + databaseName + ";restoreFrom=" + backupFile.getPath());
			logger.debug("Database \"" + databaseName + "\" successfully restored.");
			return connection;
		} catch (SQLException ex) {
			throw new DerbyPluggableModuleException("Failed to restore database \"" + databaseName + "\"", ex);
		}

	}

	/**
	 * Backups database to a directory.
	 * 
	 * Relatives path are resolved based on current user directory user.dir.
	 * According to derby's documentation file should be given in an absolute
	 * path.
	 * 
	 * @param databaseName
	 *          name of database to restore.
	 * @param backupDirectory
	 *          Derby's backup file to backup to.
	 */
	public void backupDatabase(String databaseName, File backupDirectory) throws DerbyPluggableModuleException {
		try {
			Connection conn = getConnection(databaseName);
			CallableStatement cs = conn.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
			cs.setString(1, backupDirectory.getAbsolutePath());
			cs.execute();
			cs.close();
			conn.close();
			logger.debug("State of database \"" + databaseName + "\" successfully stored to directory \"" + backupDirectory.getAbsolutePath() + "\"");
		} catch (SQLException ex) {
			throw new DerbyPluggableModuleException("Error occured when trying to backup database \"" + databaseName + "\"", ex);
		}
	}

	/**
	 * Loads the appropriate JDBC driver for embedded framework (
	 * <code>org.apache.derby.jdbc.EmbeddedDriver</code>).
	 */
	private void loadDriver() throws DerbyPluggableModuleException {
		/*
		 * The JDBC driver is loaded by loading its class. If you are using JDBC
		 * 4.0 (Java SE 6) or newer, JDBC drivers may be automatically loaded,
		 * making this code optional.
		 * 
		 * Any static Derby system properties must be set before loading the
		 * driver to take effect.
		 */
		try {
			this.getPluggableModuleManager().getClassLoader().loadClass(driverName).newInstance();

		} catch (ClassNotFoundException cnfe) {
			throw new DerbyPluggableModuleException("Unable to load the JDBC driver " + driverName);
		} catch (InstantiationException ie) {
			throw new DerbyPluggableModuleException("Unable to instantiate the JDBC driver " + driverName);
		} catch (IllegalAccessException iae) {
			throw new DerbyPluggableModuleException("Unable to access the JDBC driver " + driverName);
		}
	}

	/**
	 * Tries to check if the Network Server is up and running by calling ping If
	 * successful, then it returns else tries for 50 seconds before giving up and
	 * throwing an exception.
	 * 
	 * @throws Exception
	 *           when there is a problem with testing if the Network Server is up
	 *           and running
	 */
	private static void waitForStart() throws Exception {
		// Server instance for testing connection
		NetworkServerControl server = null;

		// Use NetworkServerControl.ping() to wait for
		// NetworkServer to come up. We could have used
		// NetworkServerControl to start the server but the property is
		// easier.
		server = new NetworkServerControl();

		for (int i = 0; i < 20; i++) {
			try {
				Thread.sleep(2500);
				server.ping();
				break;
			} catch (Exception e) {

				if (i == 9) {
					throw e;
				}
			}
		}

	}

	/**
	 * Shuts down Derby.
	 * 
	 * @throws java.sql.SQLException
	 *           if shutdown fails.
	 */
	private void shutdownDerby() throws SQLException {
		try {
			/* the shutdown=true attribute shuts down Derby */
			DriverManager.getConnection("jdbc:derby:;shutdown=true");

			// To shut down a specific database only, but keeep the
			// engine running (for example for connecting to other
			// databases), specify a database in the connection URL:
			// DriverManager.getConnection("jdbc:derby:" + dbName +
			// ";shutdown=true");
		} catch (SQLException se) {
			if (((se.getErrorCode() == 50000) && ("XJ015".equals(se.getSQLState())))) {
				// we got the expected exception
			} else {
				throw se;
			}
		}
	}

	/**
	 * Shuts down database of given name. Other derby databases will be kept
	 * running.
	 * 
	 * @param databaseName
	 *          name of datbase to shutdown.
	 * @throws java.sql.SQLException
	 *           if shutdown fails.
	 */
	private void shutdownDatabase(String databaseName) throws SQLException {
		try {
			/* the shutdown=true attribute shuts down Derby */
			DriverManager.getConnection("jdbc:derby:" + databaseName + ";shutdown=true");

			// To shut down a specific database only, but keeep the
			// engine running (for example for connecting to other
			// databases), specify a database in the connection URL:
			// DriverManager.getConnection("jdbc:derby:" + dbName +
			// ";shutdown=true");
		} catch (SQLException se) {
			if (((se.getErrorCode() == 45000) && ("08006".equals(se.getSQLState())))) {
				// we got the expected exception for single database shutdown
			} else {
				throw se;
			}
		}
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModuleInterface#dropDatabase(java.lang.String)
	 */
	public void dropDatabase(String databaseName) throws DerbyPluggableModuleException {
		/*
		 * Derby has no drop database command, database must be deleted by
		 * removing its directory from filesystem. Database must not be booted
		 * at that time.
		 */

		try {
			shutdownDatabase(databaseName);
			logger.debug("Database \"" + databaseName + "\" shutdown succeeded.");
		} catch (SQLException ex) {
			if (((ex.getErrorCode() == 40000) && ("XJ004".equals(ex.getSQLState())))) {
				// database is not booted and that is okay
			} else {
				throw new DerbyPluggableModuleException("Failed to drop database \"" + databaseName + "\"", ex);
			}
		}

		try {
			deleteDatabaseDirectory(databaseName);
			logger.debug("Database \"" + databaseName + "\" successfully deleted.");
		} catch (IOException ex) {
			throw new DerbyPluggableModuleException("Failed to delete database \"" + databaseName + "\"", ex);
		}

		logger.debug("Database \"" + databaseName + "\" successfully deleted.");
	}

	// /**
	// * Returns true if database of given name is booted
	// * @param databaseName database name
	// * @return true if database is booted
	// * @throws SQLException
	// */
	// private boolean databaseBooted(String databaseName) throws SQLException {
	// boolean result = false;
	// return true;

	// DriverPropertyInfo[] propertyInfoArray =
	// java.sql.DriverManager.getDriver("jdbc:derby:").getPropertyInfo(derbyProtocol
	// + databaseName + ";", null);

	// System.out.println(propertyInfoArray.length);
	// if (propertyInfoArray.length == 0) return true;
	// return false;

	// for (DriverPropertyInfo propertyInfo : propertyInfoArray) {
	// String name = propertyInfo.name;
	// String value = propertyInfo.value;
	// if (name.equals("databaseName")) {
	// if (value.equals(databaseName)) {
	// result = true;
	// break;
	// }
	// }
	// }
	// return result;
	// }

	/**
	 * Deletes database directory if it exists. Does nothing otherwise.
	 * 
	 * @param databaseName
	 *          name of database
	 */
	private void deleteDatabaseDirectory(String databaseName) throws IOException {
		String path = getDatabasePath(databaseName);
		File dir = new File(path);
		if (dir.exists()) {
			if (dir.isDirectory()) {
				try {
					FileUtils.deleteDirectory(new File(path));
					logger.debug("Directory \"" + path + "\" was successfully deleted");
				} catch (IOException ex) {
					IOException ioex = new IOException("Could not delete directory of database \"databaseName\"");
					ioex.initCause(ex);
					throw ioex;
				}
			} else {
				throw new IOException("Given database name \"" + databaseName + "\"references file, not directory.");
			}

		}

	}

	/**
	 * Builds path from databaseName
	 * 
	 * @param databaseName
	 *          name of database
	 * @return path of database's directory
	 */
	private String getDatabasePath(String databaseName) {
		String home = properties.getHome();
		if (home != null && !home.equals("")) {
			return properties.getHome() + File.separator + databaseName;
		} else {
			return databaseName;
		}
	}

	/**
	 * @return Properties of running derby instance or null, if Derby was stopped
	 *         or it wasn't started at all.
	 */
	public DerbyProperties getProperties() {
		return properties;
	}

}
