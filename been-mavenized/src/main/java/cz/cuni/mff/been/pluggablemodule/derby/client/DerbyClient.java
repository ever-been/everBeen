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

package cz.cuni.mff.been.pluggablemodule.derby.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Derby client pluggable module interface
 * @author Jan Tattermusch
 *
 */
public interface DerbyClient {

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

}