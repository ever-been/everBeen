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

package cz.cuni.mff.been.pluggablemodule.derby.client.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import cz.cuni.mff.been.pluggablemodule.derby.client.DerbyClient;


/**
 * Derby network connection factory. 
 * 
 * Simplifies connecting to Derby network server.
 * 
 * @author Jan Tattermusch
 */
public class DerbyClientImpl implements DerbyClient {
	
	/* URL to connect to */
	private String url;
	
	/** 
	 * Creates new instance of Derby client which can provide network connections
	 * to a given running instance of Derby server.
	 * 
	 * Obtain URL by calling getNetworkServerURL method of derby pluggable module.
	 * 
	 * @param url JDBC URL for running instance of Derby server. 
	 */
	public DerbyClientImpl(String url) {
		this.url = url;
	}
	
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.client.implementation.DerbyClient#getConnection(java.lang.String)
	 */
	public Connection getConnection(String databaseName) throws SQLException {
		return DriverManager.getConnection(this.url + databaseName
				+ ";create=true");
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.pluggablemodule.derby.client.implementation.DerbyClient#getConnection(java.lang.String, java.util.Properties)
	 */
	public Connection getConnection(String databaseName, Properties properties)
			throws SQLException {
		return DriverManager.getConnection(this.url + databaseName
				+ ";create=true", properties);
	}
	
}
