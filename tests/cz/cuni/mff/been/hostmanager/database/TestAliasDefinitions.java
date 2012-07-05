/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test syntax chceks that are performed when creating alias definitions.
 *
 * @author Branislav Repcek
 */
public class TestAliasDefinitions {
	
	private boolean wasExceptionOnCreate(String aliasName, String resultName, 
			String resultVendor, String resultVersion) {
		
		try {
			@SuppressWarnings("unused")
			SoftwareAliasDefinition definition = 
				new SoftwareAliasDefinition(aliasName, 
				                            resultName, 
				                            resultVendor, 
				                            resultVersion,
				                            new RSLRestriction("family == \"Windows\""), 
				                            new RSLRestriction("name =~ //"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return true;
		}
		
		return false;
	}
	
	/**
	 * Try to create valid alias.
	 * Output: String "OK" (this is just so every method writes something to the output).
	 */
	@Test
	public void testValidAlias() {
	
		assertFalse(wasExceptionOnCreate("${name} and ${version} and ${vendor} \\$\\{not.variable\\}", 
		                                 "${name} and ${version} and ${vendor}", 
		                                 "${name} and ${version} and ${vendor}", 
		                                 "${name} and ${version} and ${vendor}"));
		
		System.out.println("OK");
	}

	/**
	 * Try to create alias with invalid alias name.
	 * Output: exception message about unknown variable "unknown_variable".
	 */
	@Test
	public void testInvalidAliasNameUnknownVariable() {
		
		assertTrue(wasExceptionOnCreate("this is ok: ${version}, this is not: ${unknown_variable}", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}"));
	}

	/**
	 * Try to create alias with invalid alias name.
	 * Output: exception message about invalid variable declaration "invalid.variable.name".
	 */
	@Test
	public void testInvalidAliasNameInvalidVariableName() {
		
		assertTrue(wasExceptionOnCreate("ohlala: ${invalid.variable.name} and more text", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}"));
	}

	/**
	 * Try to create invalid alias.
	 * Output: exception message "enexpected ned of input, missing }".
	 */
	@Test
	public void testInvalidAliasNameMissingClosingBrace() {
		
		assertTrue(wasExceptionOnCreate("${name", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}"));
	}

	/**
	 * Try to create alias with invalid alias name.
	 * Output: exception message about unexpected token "}".
	 */
	@Test
	public void testInvalidAliasNameUnexpectedToken() {
		
		assertTrue(wasExceptionOnCreate("wow $} this is sooo cool", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}"));
	}
	
	/**
	 * Try to create alias with invalid alias name.
	 * Output: exception message about variable name being empty.
	 */
	@Test
	public void testInvalidAliasNameEmptyVariableName() {
		
		assertTrue(wasExceptionOnCreate("${name} next variable is empty${}", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}"));
	}

	/**
	 * Try to create alias with invalid result name.
	 * Output: exception message about unexpected end of input.
	 */
	@Test
	public void testInvalidResultName() {
		
		assertTrue(wasExceptionOnCreate("${name} and ${version} and ${vendor}", 
		                                "woohoo ${name", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}"));
	}

	/**
	 * Try to create alias with invalid result vendor.
	 * Output: exception message about unexpected end of input.
	 */
	@Test
	public void testInvalidResultVendor() {
		
		assertTrue(wasExceptionOnCreate("${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}", 
		                                "vendor iz ${name", 
		                                "${name} and ${version} and ${vendor}"));
	}

	/**
	 * Try to create alias with invalid result version.
	 * Output: exception message about unexpected end of input.
	 */
	@Test
	public void testInvalidResultVersion() {
		
		assertTrue(wasExceptionOnCreate("${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}", 
		                                "${name} and ${version} and ${vendor}", 
		                                "we are soo screwed ${version"));
	}
}
