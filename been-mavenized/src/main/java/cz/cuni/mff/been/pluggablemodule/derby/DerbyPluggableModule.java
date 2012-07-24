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

package cz.cuni.mff.been.pluggablemodule.derby;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;

/**
 * Derby pluggable module interface
 * @author Jan Tattermusch
 *
 */
public interface DerbyPluggableModule {

	/**
	 * Runs embedded instance of Derby. This instance is accessible locally
	 * (from the same JVM) and optionally also over network.
	 * 
	 * @param home
	 *            Derby's home folder (where derby stores its databases)
	 *            
	 * @param networkAccessible
	 * 			  Whether Derby engine will be accessible over network.          
	 * 
	 * @throws PluggableModuleException
	 *             if something goes wrong.
	 */
	public abstract void startEngine(String home, boolean networkAccessible)
			throws PluggableModuleException;
	
	/**
	 * Runs embedded instance of Derby. This instance is accessible locally
	 * (from the same JVM) and optionally also over network.
	 * 
	 * @param home
	 *            Derby's home folder (where derby stores its databases)
	 *            
	 * @param networkAccessible
	 * 			  Whether Derby engine will be accessible over network.          
	 * 
	 *  @param networkPort
	 * 			  Port on which derby network server will be accessible.
	 * 
	 * @throws PluggableModuleException
	 *             if something goes wrong.
	 */
	public abstract void startEngine(String home, boolean networkAccessible, int networkPort)
			throws PluggableModuleException;

	/**
	 * Stops embedded instance of Derby (clean shutdown). This method should be
	 * called each time you are not going to use database any more.
	 * 
	 * If you won't call this method when closing this JVM, Derby's shutdown
	 * checkpoint won't be reached and Derby will run in recovery mode next time
	 * opening your database.
	 * 
	 * @throws ScriptException
	 *             when something goes wrong.
	 */
	public abstract void stopEngine() throws PluggableModuleException;

	/**
	 * Opens new connection to running local instance of Derby.
	 * 
	 * If the database does not exist, it is created.
	 * 
	 * @param databaseName
	 *            name of database to connect to.
	 * @return SQL connection
	 */
	public abstract Connection getConnection(String databaseName)
			throws SQLException;

	/**
	 * Opens new connection to running local instance of Derby.
	 *  * If the database does not exist, it is created.
	 * 
	 * @param databaseName
	 *            name of database to connect to.
	 * @param properties
	 *            SQL connection properties.
	 * @return SQL connection
	 */
	public abstract Connection getConnection(String databaseName,
			Properties properties) throws SQLException;

	/**
	 * Returns JDBC URL using which local instance of Derby can be accessed over
	 * network.
	 * 
	 * @param hostname
	 *            hostname which will be used to reference this computer
	 * @return network server URL or null if Derby is not accessible over
	 *         network or Derby is not running at all.
	 */
	public abstract String getNetworkServerURL(String hostname);

	/**
	 * Sets up database of given name using given initialization SQL script.
	 * Return connection to that database.
	 * 
	 * If database of given name already exists, it is first dropped and then
	 * recreated.
	 * 
	 * @param databaseName
	 *            name of database to connect to.
	 * @param setupScriptStream
	 *            stream from which a setup SQL script should be read.
	 * @return SQL connection to initialized database.
	 * 
	 * @throws ScriptException
	 *             when something goes wrong.
	 */
	public abstract Connection setupDatabase(String databaseName,
			InputStream setupScriptStream) throws PluggableModuleException;

	/**
	 * Sets up database of given name using given initialization SQL script.
	 * Return connection to that database.
	 * 
	 * If database of given name already exists, it is first dropped and then
	 * recreated.
	 * 
	 * @param databaseName
	 *            name of database to connect to.
	 * @param setupScriptFile
	 *            file from which SQL script should be read.
	 * @return SQL connection to initialized database.
	 * 
	 * @throws FileNotFoundException
	 *             when setup script file does not exist.
	 * @throws ScriptException
	 *             when something goes wrong.
	 */
	public abstract Connection setupDatabase(String databaseName,
			File setupScriptFile) throws FileNotFoundException,
			PluggableModuleException;

	/**
	 * Restores a database from Derby's backup file.
	 * 
	 * Relative paths are resolved based on current user directory. Use absolute
	 * paths to avoid confusion.
	 * 
	 * Remember that if you backup a database of name YYY to a directory XXX,
	 * you must restore it from path XXX/YYY (which is the place where Derby
	 * saves its backup files).
	 * 
	 * If database of given name exists, it is first dropped.
	 * 
	 * @param databaseName
	 *            name of database to restore.
	 * @param backupFile
	 *            Derby's backup file.
	 * @return connection to just restored database.
	 */
	public abstract java.sql.Connection restoreDatabase(String databaseName,
			File backupFile) throws PluggableModuleException;

	/**
	 * Drops database of given name
	 * 
	 * @param databaseName
	 *            database name
	 */
	public abstract void dropDatabase(String databaseName)
			throws PluggableModuleException;

}