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
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.junit.Test;

import cz.cuni.mff.been.common.Version;
import cz.cuni.mff.been.softwarerepository.PackageType;

public class ContainsConditionTest {
	private ContainerProperty context = new DummyContext();
	
	/* LongWithUnit */

	@Test
	public void testLongWithUnitParse() throws ParseException {
		assertEquals(
			new ContainsCondition<LongWithUnit>("a", new LongWithUnit(42, 'k', "Hz")),
			ParserWrapper.parseString("a contains 42kHz")
		);
	}

	/* String */

	@Test
	public void testStringParse() throws ParseException {
		assertEquals(
			new ContainsCondition<String>("a", "42"),
			ParserWrapper.parseString("a contains \"42\"")
		);
	}

	/* Version */

	@Test
	public void testVersionParse() throws ParseException {
		assertEquals(
			new ContainsCondition<Version>("a", new Version("42")),
			ParserWrapper.parseString("a contains 42.0")
		);
	}

	/* Date */

	@Test
	public void testDateParse() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.set(2006, 4, 17, 23, 30, 42);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		assertEquals(
			new ContainsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a contains 2006-05-17T23:30:42Z")
		);
	}

	/* PackageType */

	@Test
	public void testPackageTypeParse() throws ParseException {
		assertEquals(
			new ContainsCondition<PackageType>("a", PackageType.realValueOf("source")),
			ParserWrapper.parseString("a contains source")
		);
	}
	
	/* Pattern */

	@Test
	public void testPatternParse() throws ParseException {
		/* Pattern class doesn't override equals method, so we have to compare
		 * manually.
		 */
		Condition expected = new ContainsCondition<Pattern>("a",
			Pattern.compile("pattern"));
		Condition actual = ParserWrapper.parseString("a contains /pattern/");
		
		Pattern expectedPattern = (Pattern) ((SimpleCondition< ? >) expected).value;
		Pattern actualPattern = (Pattern) ((SimpleCondition< ? >) actual).value;
		
		assertTrue(
			expectedPattern.pattern().equals(actualPattern.pattern())
			&& expectedPattern.flags() == actualPattern.flags()
		);
	}

	/* Test all property and value class combintions. */

	@Test
	public void testLongWithUnitLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty contains 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty contains \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty contains 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty contains 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty contains source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}
	
	@Test
	public void testLongWithUnitPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty contains /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty contains 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty contains \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty contains 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty contains 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty contains source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty contains /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty contains 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty contains \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty contains 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty contains 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty contains source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty contains /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty contains 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty contains \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty contains 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty contains 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDatePackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty contains source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDatePattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty contains /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty contains 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty contains \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty contains 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty contains 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypePackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty contains source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypePattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty contains /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty contains 42", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListStringTrue() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("listProperty contains \"42\"", context));
	}
	
	@Test
	public void testListStringFalse() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("listProperty contains \"43\"", context));
	}

	@Test
	public void testListVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty contains 42.0", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty contains 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty contains source", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}	

	@Test
	public void testListPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty contains /pattern/", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}	

	/* Tests of toString method. */
	
	@Test
	public void testToString() throws ParseException {
		assertEquals(
			"a contains 42",
			ParserWrapper.parseString("a contains 42").toString()
		);
	}
}
