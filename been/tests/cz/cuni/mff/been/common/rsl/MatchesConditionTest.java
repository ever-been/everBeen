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

public class MatchesConditionTest {
	private ContainerProperty context = new DummyContext();
	
	/* LongWithUnit */

	@Test
	public void testLongWithUnitParse() throws ParseException {
		assertEquals(
			new MatchesCondition<LongWithUnit>("a", new LongWithUnit(42, 'k', "Hz")),
			ParserWrapper.parseString("a =~ 42kHz")
		);
	}

	/* String */

	@Test
	public void testStringParse() throws ParseException {
		assertEquals(
			new MatchesCondition<String>("a", "42"),
			ParserWrapper.parseString("a =~ \"42\"")
		);
	}

	/* Version */

	@Test
	public void testVersionParse() throws ParseException {
		assertEquals(
			new MatchesCondition<Version>("a", new Version("42")),
			ParserWrapper.parseString("a =~ 42.0")
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
			new MatchesCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a =~ 2006-05-17T23:30:42Z")
		);
	}

	/* PackageType */

	@Test
	public void testPackageTypeParse() throws ParseException {
		assertEquals(
			new MatchesCondition<PackageType>("a", PackageType.realValueOf("source")),
			ParserWrapper.parseString("a =~ source")
		);
	}
	
	/* Pattern */

	@Test
	public void testPatternParse() throws ParseException {
		/* Pattern class doesn't override equals method, so we have to compare
		 * manually.
		 */
		Condition expected = new MatchesCondition<Pattern>("a",
			Pattern.compile("pattern"));
		Condition actual = ParserWrapper.parseString("a =~ /pattern/");
		
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
			Utils.eval("longProperty =~ 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty =~ \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty =~ 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty =~ 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty =~ source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}
	
	@Test
	public void testLongWithUnitPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty =~ /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty =~ 42", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty =~ \"42\"", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty =~ 42.0", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty =~ 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty =~ source", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringPatternCaseSensitiveTrue() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("stringForPatternProperty =~ /StRiNg/", context));
	}

	@Test
	public void testStringPatternCaseSensitiveFalse1() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("stringForPatternProperty =~ /STRING/", context));
	}
	
	@Test
	public void testStringPatternCaseSensitiveFalse2() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("stringForPatternProperty =~ /string/", context));
	}

	@Test
	public void testStringPatternCaseSensitiveFalse3() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("stringForPatternProperty =~ /no-match/", context));
	}

	@Test
	public void testStringPatternCaseInsensitiveTrue1() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("stringForPatternProperty =~ /StRiNg/i", context));
	}

	@Test
	public void testStringPatternCaseInsensitiveTrue2() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("stringForPatternProperty =~ /STRING/i", context));
	}
	
	@Test
	public void testStringPatternCaseInsensitiveTrue3() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("stringForPatternProperty =~ /string/i", context));
	}

	@Test
	public void testStringPatternCaseInsensitiveFalse() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("stringForPatternProperty =~ /no-match/i", context));
	}

	@Test
	public void testVersionLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty =~ 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty =~ \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty =~ 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty =~ 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty =~ source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty =~ /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty =~ 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty =~ \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty =~ 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty =~ 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDatePackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty =~ source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDatePattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty =~ /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty =~ 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty =~ \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty =~ 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty =~ 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypePackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty =~ source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypePattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty =~ /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty =~ 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListStringTrue() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty =~ \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty =~ 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty =~ 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty =~ source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}	

	@Test
	public void testListPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty =~ /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}	

	/* Tests of toString method. */
	
	@Test
	public void testToString() throws ParseException {
		assertEquals(
			"a =~ 42",
			ParserWrapper.parseString("a =~ 42").toString()
		);
	}
}
