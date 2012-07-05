/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.common.rsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import cz.cuni.mff.been.common.Version;
import cz.cuni.mff.been.softwarerepository.PackageType;

public class EqualsConditionTest {
	private ContainerProperty context = new DummyContext();
	
	/* LongWithUnit */

	@Test
	public void testLongWithUnitParse() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>("a", new LongWithUnit(42, 'k', "Hz")),
			ParserWrapper.parseString("a == 42kHz")
		);
	}

	@Test
	public void testLongWithUnitTrue() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("longProperty == 42", context));
	}

	@Test
	public void testLongWithUnitFalse() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("longProperty == 43", context));
	}

	/* String */
	
	@Test
	public void testStringParse() throws ParseException {
		assertEquals(
			new EqualsCondition<String>("a", "42"),
			ParserWrapper.parseString("a == \"42\"")
		);
	}

	@Test
	public void testStringTrue() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("stringProperty == \"42\"", context));
	}

	@Test
	public void testStringFalse() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("stringProperty == \"43\"", context));
	}

	/* Version */

	@Test
	public void testVersionParse() throws ParseException {
		assertEquals(
			new EqualsCondition<Version>("a", new Version("42")),
			ParserWrapper.parseString("a == 42.0")
		);
	}

	@Test
	public void testVersionTrue() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("versionProperty == 42.0", context));
	}

	@Test
	public void testVersionFalse() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("versionProperty == 43.0", context));
	}

	/* Date */

	@Test
	public void testDateParse() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.set(2006, 4, 17, 23, 30, 42);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		assertEquals(
			new EqualsCondition<Date>("dateProperty", calendar.getTime()),
			ParserWrapper.parseString("dateProperty == 2006-05-17T23:30:42Z")
		);
	}

	@Test
	public void testDateTrue() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("dateProperty == 2006-05-17T23:30:42Z", context));
	}

	@Test
	public void testDateFalse() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("dateProperty == 2006-05-17T23:30:43Z", context));
	}

	/* PackageType */
	
	@Test
	public void testPackageTypeParse() throws ParseException {
		assertEquals(
			new EqualsCondition<PackageType>("a", PackageType.realValueOf("source")),
			ParserWrapper.parseString("a == source")
		);
	}

	@Test
	public void testPackageTypeTrue() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("packageTypeProperty == source", context));
	}

	@Test
	public void testPackageTypeFalse() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("packageTypeProperty == binary", context));
	}

	/* Tests of toString method. */
	
	@Test
	public void testToString() throws ParseException {
		assertEquals(
			"a == 42",
			ParserWrapper.parseString("a == 42").toString()
		);
	}
}
