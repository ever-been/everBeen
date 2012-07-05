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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.ValueTypeIncorrectException;

/**
 * Tests of the RSLRestriction class and interaction between RSL and the Host
 * Manager in general. 
 * 
 * @author David Majda
 */
@SuppressWarnings("all")
public class RSLRestrictionTest {
	private String beenHome;
	private HostInfoInterface hostInfo;
	
	@Before
	public void setUp() throws Exception {
		if ((beenHome = System.getenv("BEEN_HOME")) == null) {
			throw new Exception("BEEN_HOME environment variable not defined.");
		}

		hostInfo = HostInfoBuilder.readFromFile(beenHome + File.separator + "resources"
			+ File.separator + "tests" + File.separator + "hostmanager"
			+ File.separator + "test.host");
	}

	/* Helper routines. */
	
	private void assertTestReturnsTrue(String rsl) {
		assertTrue(new RSLRestriction(rsl).test(hostInfo, false));
	}
	
	private void assertTestReturnsFalse(String rsl) {
		assertFalse(new RSLRestriction(rsl).test(hostInfo, false));
	}

	private void assertTestThrows(String rsl, Class exceptionClass,
			String exceptionMessage) {
		try {
			new RSLRestriction(rsl).test(hostInfo, false);
		} catch (Throwable t) {
			assertEquals(exceptionClass, t.getClass());
			assertEquals(exceptionMessage, t.getMessage());
		}
	}

	/* Basic tests. */
	
	@Test
	public void testSimpleTrue() {
		assertTestReturnsTrue("detector == \"hwdet3_windows\"");
	}

	@Test
	public void testSimpleFalse() {
		assertTestReturnsFalse("detector != \"hwdet3_windows\"");
	}

	@Test
	public void testSimpleUnknownProperty() {
		assertTestThrows("unknownProperty == 42", ValueNotFoundException.class,
			"Property \"unknownProperty\" doesn't exist.");
	}

	@Test
	public void testSimpleInvalidProperty() {
		/* "unknownProperty2" is legal property name in RSL but not in the Host
		 * Manager. 
		 */
		assertTestThrows("unknownProperty2 == 42", ValueNotFoundException.class,
			"Property \"unknownProperty2\" doesn't exist.");
	}
	
	@Test
	public void testContainerTrue() {
		assertTestReturnsTrue("application.name == \"Cool Game\"");
	}

	@Test
	public void testContainerFalse() {
		assertTestReturnsFalse("application.name == \"Bad Game\"");
	}

	@Test
	public void testContainerUnknownProperty() {
		assertTestThrows("application.unknownProperty == 42", ValueNotFoundException.class,
			"Property \"application.unknownProperty\" doesn't exist.");
	}
	
	/* Type compatibility evil testsuite - for each of the Host Manager's value
	 * type used in the HostInfo properties test comparison with each RSL type.
	 * 
	 * Host Manager's types:
	 * 
	 * - ValueInteger
	 * - ValueString
	 * - ValueVersion
	 * - ValueList<ValueString>
	 * 
	 * RSL's types:
	 * 
	 * - LongWithUnit
	 * - Version
	 * - Date
	 * - String
	 * - PackageType
	 * - List<String>
	 */
	
	/* ValueInteger */
	
	@Test
	public void testCompareValueIntegerToLongWithUnit() {
		assertTestReturnsTrue("adapters == 1");
	}

	@Test
	public void testCompareValueIntegerToVersion() {
		assertTestThrows("adapters == 4.2", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"adapters\" is "
			+ "\"cz.cuni.mff.been.common.rsl.LongWithUnit\", but the value is "
			+ "\"cz.cuni.mff.been.common.Version\".");
	}

	@Test
	public void testCompareValueIntegerToDate() {
		assertTestThrows("adapters == 2006-05-17", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"adapters\" is "
			+ "\"cz.cuni.mff.been.common.rsl.LongWithUnit\", but the value is "
			+ "\"java.util.Date\".");
	}
	
	@Test
	public void testCompareValueIntegerToString() {
		assertTestThrows("adapters == \"Lorem ipsum\"", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"adapters\" is "
			+ "\"cz.cuni.mff.been.common.rsl.LongWithUnit\", but the value is "
			+ "\"java.lang.String\".");
	}

	@Test
	public void testCompareValueIntegerToPackageType() {
		assertTestThrows("adapters == source", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"adapters\" is "
			+ "\"cz.cuni.mff.been.common.rsl.LongWithUnit\", but the value is "
			+ "\"cz.cuni.mff.been.softwarerepository.PackageType\".");
	}
	
	@Test
	public void testCompareValueIntegerToList() {
		assertTestThrows("adapters contains \"Lorem ipsum\"", ValueTypeIncorrectException.class,
			"Invalid operator applied to property \"adapters\" as it's class "
			+ "\"cz.cuni.mff.been.common.rsl.LongWithUnit\" doesn't implement interface "
			+ "\"java.util.List\".");
	}

	/* ValueString */
	
	@Test
	public void testCompareValueStringToLongWithUnit() {
		assertTestThrows("detector == 42", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"detector\" is "
			+ "\"java.lang.String\", but the value is "
			+ "\"cz.cuni.mff.been.common.rsl.LongWithUnit\".");
	}

	@Test
	public void testCompareValueStringToVersion() {
		assertTestThrows("detector == 4.2", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"detector\" is "
			+ "\"java.lang.String\", but the value is "
			+ "\"cz.cuni.mff.been.common.Version\".");
	}

	@Test
	public void testCompareValueStringToDate() {
		assertTestThrows("detector == 2006-05-17", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"detector\" is "
			+ "\"java.lang.String\", but the value is "
			+ "\"java.util.Date\".");
	}
	
	@Test
	public void testCompareValueStringToString() {
		assertTestReturnsTrue("detector == \"hwdet3_windows\"");
	}

	@Test
	public void testCompareValueStringToPackageType() {
		assertTestThrows("detector == source", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"detector\" is "
			+ "\"java.lang.String\", but the value is "
			+ "\"cz.cuni.mff.been.softwarerepository.PackageType\".");
	}

	@Test
	public void testCompareValueStringToList() {
		assertTestThrows("detector contains \"Lorem ipsum\"", ValueTypeIncorrectException.class,
			"Invalid operator applied to property \"detector\" as it's class "
			+ "\"java.lang.String\" doesn't implement interface "
			+ "\"java.util.List\".");
	}

	/* ValueVersion */
	
	@Test
	public void testCompareValueVersionToLongWithUnit() {
		assertTestThrows("java.version == 42", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"java.version\" is "
			+ "\"cz.cuni.mff.been.common.Version\", but the value is "
			+ "\"cz.cuni.mff.been.common.rsl.LongWithUnit\".");
	}

	@Test
	public void testCompareValueVersionToVersion() {
		assertTestReturnsTrue("java.version == 1.5.0_05");
	}

	@Test
	public void testCompareValueVersionToDate() {
		assertTestThrows("java.version == 2006-05-17", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"java.version\" is "
			+ "\"cz.cuni.mff.been.common.Version\", but the value is "
			+ "\"java.util.Date\".");
	}
	
	@Test
	public void testCompareValueVersionToString() {
		assertTestThrows("java.version == \"Lorem ipsum\"", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"java.version\" is "
			+ "\"cz.cuni.mff.been.common.Version\", but the value is "
			+ "\"java.lang.String\".");
	}

	@Test
	public void testCompareValueVersionToPackageType() {
		assertTestThrows("java.version == source", ValueTypeIncorrectException.class,
			"Invalid value type specified: Property \"java.version\" is "
			+ "\"cz.cuni.mff.been.common.Version\", but the value is "
			+ "\"cz.cuni.mff.been.softwarerepository.PackageType\".");
	}
	
	@Test
	public void testCompareValueVersionToList() {
		assertTestThrows("java.version contains \"Lorem ipsum\"", ValueTypeIncorrectException.class,
			"Invalid operator applied to property \"java.version\" as it's class "
			+ "\"cz.cuni.mff.been.common.Version\" doesn't implement interface "
			+ "\"java.util.List\".");
	}

	/* ValueList<ValueString> */
	
	@Test
	public void testCompareValueListToLongWithUnit() {
		assertTestThrows("memberof == 42", ValueTypeIncorrectException.class,
			"Invalid operator applied to property \"memberof\" as it's class "
			+ "\"java.util.List\" implements interface "
			+ "\"java.util.List\".");
	}

	@Test
	public void testCompareValueListToVersion() {
		assertTestThrows("memberof == 4.2", ValueTypeIncorrectException.class,
			"Invalid operator applied to property \"memberof\" as it's class "
			+ "\"java.util.List\" implements interface "
			+ "\"java.util.List\".");
	}

	@Test
	public void testCompareValueListToDate() {
		assertTestThrows("memberof == 2006-05-17", ValueTypeIncorrectException.class,
			"Invalid operator applied to property \"memberof\" as it's class "
			+ "\"java.util.List\" implements interface "
			+ "\"java.util.List\".");
	}
	
	@Test
	public void testCompareValueListToString() {
		assertTestThrows("memberof == \"Lorem ipsum\"", ValueTypeIncorrectException.class,
			"Invalid operator applied to property \"memberof\" as it's class "
			+ "\"java.util.List\" implements interface "
			+ "\"java.util.List\".");
	}

	@Test
	public void testCompareValueListToPackageType() {
		assertTestThrows("memberof == source", ValueTypeIncorrectException.class,
			"Invalid operator applied to property \"memberof\" as it's class "
			+ "\"java.util.List\" implements interface "
			+ "\"java.util.List\".");
	}

	@Test
	public void testCompareValueListToList() {
		assertTestReturnsTrue("memberof contains \"" + HostGroup.DEFAULT_GROUP_NAME + "\"");
	}

}
