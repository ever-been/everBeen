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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.junit.Test;

import cz.cuni.mff.been.common.Version;

public class SimpleConditionTest {
	private ContainerProperty context = new DummyContext();

	/* Simple and non-exhaustive parsing tests. */
	
	@Test
	public void testSimpleCondition1() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>("a", new LongWithUnit(5, 'k', "Hz")),
			ParserWrapper.parseString("a==5kHz")
		);
	}
	
	@Test
	public void testSimpleCondition2() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>("a", new LongWithUnit(5, 'k', "Hz")),
			ParserWrapper.parseString("a == 5kHz")
		);
	}
	
	@Test
	public void testSimpleCondition3() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>("a", new LongWithUnit(5, 'k', "Hz")),
			ParserWrapper.parseString("a   ==   5kHz")
		);
	}
		
	@Test
	public void testSimpleCondition4() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>("a.b.c", new LongWithUnit(5, 'k', "Hz")),
			ParserWrapper.parseString("a.b.c == 5kHz")
		);
	}
	
	@Test
	public void testSimpleCondition5() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"aaa.aaa.aaa",
				new LongWithUnit(5, 'k', "Hz")
			),
			ParserWrapper.parseString("aaa.aaa.aaa == 5kHz")
		);
	}
	
	@Test
	public void testSimpleCondition6() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a_01A.b_01B",
				new LongWithUnit(5, 'k', "Hz")
			),
			ParserWrapper.parseString("a_01A.b_01B == 5kHz")
		);
	}
		
	/* LongWithUnit parsing tests */

	@Test
	public void testLongWithUnitParse() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(55555, 'k', "Hz")
			),
			ParserWrapper.parseString("a == 55555kHz")
		);
	}

	@Test
	public void testLongWithUnitParseB() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42, LongWithUnit.NO_UNIT_PREFIX, "B")
			),
			ParserWrapper.parseString("a == 42B")
		);
	}
	
	@Test
	public void testLongWithUnitParsek() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42L, 'k', null)
			),
			ParserWrapper.parseString("a == 42k")
		);
	}

	@Test
	public void testLongWithUnitParsekB() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42L, 'k', "B")
			),
			ParserWrapper.parseString("a == 42kB")
		);
	}

	@Test
	public void testLongWithUnitParseM() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42L, 'M', null)
			),
			ParserWrapper.parseString("a == 42M")
		);
	}

	@Test
	public void testLongWithUnitParseMB() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42L, 'M', "B")
			),
			ParserWrapper.parseString("a == 42MB")
		);
	}
	
	@Test
	public void testLongWithUnitParseG() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42L, 'G', null)
			),
			ParserWrapper.parseString("a == 42G")
		);
	}

	@Test
	public void testLongWithUnitParseGB() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42L, 'G', "B")
			),
			ParserWrapper.parseString("a == 42GB")
		);
	}

	@Test
	public void testLongWithUnitParseT() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42L, 'T', null)
			),
			ParserWrapper.parseString("a == 42T")
		);
	}

	@Test
	public void testLongWithUnitParseTB() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42L, 'T', "B")
			),
			ParserWrapper.parseString("a == 42TB")
		);
	}

	@Test
	public void testLongWithUnitParseP() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42L, 'P', null)
			),
			ParserWrapper.parseString("a == 42P")
		);
	}

	@Test
	public void testLongWithUnitParsePB() throws ParseException {
		assertEquals(
			new EqualsCondition<LongWithUnit>(
				"a",
				new LongWithUnit(42L, 'P', "B")
			),
			ParserWrapper.parseString("a == 42PB")
		);
	}
		
	/* Version parsing tests */
	
	@Test
	public void testVersionParse() throws ParseException {
		assertEquals(
			new EqualsCondition<Version>("a", new Version("42")),
			ParserWrapper.parseString("a == 42.0")
		);
	}
		
	/* String parsing tests */

	@Test
	public void testStringParse() throws ParseException {
		assertEquals(
			new EqualsCondition<String>("a", "String\"s"),
			ParserWrapper.parseString("a == \"String\\\"s\"")
		);
	}

	/* Date parsing tests */

	@Test
	public void testDateParseyyyyMM() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2006, 4, 0, 0, 0, 0);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05")
		);
	}

	@Test
	public void testDateParseyyyyMMdd() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2006, 4, 17, 0, 0, 0);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05-17")
		);
	}

	@Test
	public void testDateParseyyyyMMddTHHmmZ() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.set(2006, 4, 17, 23, 30, 0);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05-17T23:30Z")
		);
	}

	@Test
	public void testDateParseyyyyMMddTHHmmssZ() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.set(2006, 4, 17, 23, 30, 42);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05-17T23:30:42Z")
		);
	}

	@Test
	public void testDateParseyyyyMMddTHHmmssSZ() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.set(2006, 4, 17, 23, 30, 42);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000 + 100);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05-17T23:30:42.1Z")
		);
	}
	
	@Test
	public void testDateParseyyyyMMddTHHmmPlus300() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
		calendar.set(2006, 4, 17, 23, 30, 0);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05-17T23:30+03:00")
		);
	}

	@Test
	public void testDateParseyyyyMMddTHHmmssPlus300() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
		calendar.set(2006, 4, 17, 23, 30, 42);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05-17T23:30:42+03:00")
		);
	}

	@Test
	public void testDateParseyyyyMMddTHHmmssSPlus300() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
		calendar.set(2006, 4, 17, 23, 30, 42);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000 + 100);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05-17T23:30:42.1+03:00")
		);
	}

	@Test
	public void testDateParseyyyyMMddTHHmmMinus300() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
		calendar.set(2006, 4, 17, 23, 30, 0);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05-17T23:30-03:00")
		);
	}

	@Test
	public void testDateParseyyyyMMddTHHmmssMinus300() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
		calendar.set(2006, 4, 17, 23, 30, 42);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05-17T23:30:42-03:00")
		);
	}

	@Test
	public void testDateParseyyyyMMddTHHmmssSMinus300() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
		calendar.set(2006, 4, 17, 23, 30, 42);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000 + 100);
		assertEquals(
			new EqualsCondition<Date>("a", calendar.getTime()),
			ParserWrapper.parseString("a == 2006-05-17T23:30:42.1-03:00")
		);
	}

	/* Pattern parsing tests */

	@Test
	public void testPatternParse1() throws ParseException {
		/* Pattern class doesn't override equals method, so we have to compare
		 * manually.
		 */
		Condition expected = new EqualsCondition<Pattern>("a",
			Pattern.compile("pattern/pattern"));
		Condition actual = ParserWrapper.parseString("a == /pattern\\/pattern/");
		
		Pattern expectedPattern = (Pattern) ((SimpleCondition< ? >) expected).value;
		Pattern actualPattern = (Pattern) ((SimpleCondition< ? >) actual).value;
		
		assertTrue(
			expectedPattern.pattern().equals(actualPattern.pattern())
			&& expectedPattern.flags() == actualPattern.flags()
		);
	}

	@Test
	public void testPatternParse2() throws ParseException {
		/* Pattern class doesn't override equals method, so we have to compare
		 * manually.
		 */
		Condition expected = new EqualsCondition<Pattern>("a",
			Pattern.compile("pattern/pattern", Pattern.CASE_INSENSITIVE));
		Condition actual = ParserWrapper.parseString("a == /pattern\\/pattern/i");
		
		Pattern expectedPattern = (Pattern) ((SimpleCondition< ? >) expected).value;
		Pattern actualPattern = (Pattern) ((SimpleCondition< ? >) actual).value;
		
		assertTrue(
			expectedPattern.pattern().equals(actualPattern.pattern())
			&& expectedPattern.flags() == actualPattern.flags()
		);
	}

	/* Test invalid property handling. */
	
	@Test
	public void testInvalidPropertyException() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("invalidProperty == 42", context);
			fail();
		} catch (InvalidPropertyException e) {
			/* Eat it. */
		}
	}
	
	/* Test all property and value class combintions. */

	@Test
	public void testLongWithUnitLongWithUnit() throws ParseException, RSLSemanticException {
		Utils.eval("longProperty == 42", context);
	}

	@Test
	public void testLongWithUnitString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty == \"42\"", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty == 42.0", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty == 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testLongWithUnitPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty == source", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}
	
	@Test
	public void testLongWithUnitPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty == /pattern/", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty == 42", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringString() throws ParseException, RSLSemanticException {
		Utils.eval("stringProperty == \"42\"", context);
	}

	@Test
	public void testStringVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty == 42.0", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty == 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty == source", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testStringPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("stringProperty == /pattern/", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty == 42", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty == \"42\"", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionVersion() throws ParseException, RSLSemanticException {
		Utils.eval("versionProperty == 42.0", context);
	}

	@Test
	public void testVersionDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty == 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty == source", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testVersionPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("versionProperty == /pattern/", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty == 42", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty == \"42\"", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty == 42.0", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDateDate() throws ParseException, RSLSemanticException {
			Utils.eval("dateProperty == 2006-05-17T23:30:42.1Z", context);
	}

	@Test
	public void testDatePackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty == source", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testDatePattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("dateProperty == /pattern/", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty == 42", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty == \"42\"", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty == 42.0", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypeDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty == 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testPackageTypePackageType() throws ParseException, RSLSemanticException {
		Utils.eval("packageTypeProperty == source", context);
	}

	@Test
	public void testPackageTypePattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("packageTypeProperty == /pattern/", context);
			fail();
		} catch (InvalidValueTypeException e) {
			/* Eat it. */
		}
	}
	
	@Test
	public void testListLongWithUnit() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty == 42", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListString() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty == \"42\"", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListVersion() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty == 42.0", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListDate() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty == 2006-05-17T23:30:42.1Z", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListPackageType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty == source", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testListPattern() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("listProperty == /pattern/", context);
			fail();
		} catch (InvalidOperatorException e) {
			/* Eat it. */
		}
	}

	/* Tests of LongWithUnit handling in the SimpleCondition.check method */ 

	@Test
	public void testCheckLongWithUnit1() throws ParseException, RSLSemanticException {
		Utils.eval("longProperty == 42", context);
	}
	
	@Test
	public void testCheckLongWithUnit2() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty == 42Hz", context);
			fail();
		} catch (InvalidValueUnitException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testCheckLongWithUnit3() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty == 42foo", context);
			fail();
		} catch (InvalidValueUnitException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testCheckLongWithUnit4() throws ParseException, RSLSemanticException {
		Utils.eval("longPropertyMHz == 42", context);
	}
	
	@Test
	public void testCheckLongWithUnit5() throws ParseException, RSLSemanticException {
		Utils.eval("longPropertyMHz == 42Hz", context);
	}

	@Test
	public void testCheckLongWithUnit6() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longPropertyMHz == 42foo", context);
			fail();
		} catch (InvalidValueUnitException e) {
			/* Eat it. */
		}
	}

	/* Tests of toString method. */
	
	@Test
	public void testToStringLongWithUnit() throws ParseException {
		assertEquals(
			"a == 42",
			ParserWrapper.parseString("a == 42").toString()
		);
	}

	@Test
	public void testToStringLongWithUnitkB() throws ParseException {
		assertEquals(
			"a == 42kB",
			ParserWrapper.parseString("a == 42kB").toString()
		);
	}
		
	@Test
	public void testToStringLongWithUnitMB() throws ParseException {
		assertEquals(
			"a == 42MB",
			ParserWrapper.parseString("a == 42MB").toString()
		);
	}
	
	@Test
	public void testToStringLongWithUnitGB() throws ParseException {
		assertEquals(
			"a == 42GB",
			ParserWrapper.parseString("a == 42GB").toString()
		);
	}

	@Test
	public void testToStringLongWithUnitTB() throws ParseException {
		assertEquals(
			"a == 42TB",
			ParserWrapper.parseString("a == 42TB").toString()
		);
	}

	@Test
	public void testToStringLongWithUnitPB() throws ParseException {
		assertEquals(
			"a == 42PB",
			ParserWrapper.parseString("a == 42PB").toString()
		);
	}

	@Test
	public void testToStringLongWithUnitNoSuitableUnit() throws ParseException {
		assertEquals(
			"a == 123456789",
			ParserWrapper.parseString("a == 123456789").toString()
		);
	}

	@Test
	public void testToStringString1() throws ParseException {
		assertEquals(
			"a == \"42\"",
			ParserWrapper.parseString("a == \"42\"").toString()
		);
	}

	@Test
	public void testToStringString2() throws ParseException {
		assertEquals(
			"a == \"4\\\"2\"",
			ParserWrapper.parseString("a == \"4\\\"2\"").toString()
		);
	}

	@Test
	public void testToStringVersion() throws ParseException {
		assertEquals(
			"a == 42.0",
			ParserWrapper.parseString("a == 42.0").toString()
		);
	}
	
	@Test
	public void testToStringVersion2() throws ParseException {
		assertEquals(
			"a == 42.43.441_RC-45",
			ParserWrapper.parseString("a == 42.43.441_RC-45").toString()
		);
	}

	@Test
	public void testToStringDate1() throws ParseException {
		assertEquals(
			"a == 2006-04-30T00:00:00.0+0200",
			ParserWrapper.parseString("a == 2006-05").toString()
		);
	}
	
	@Test
	public void testToStringDate2() throws ParseException {
		assertEquals(
			"a == 2006-05-17T00:00:00.0+0200",
			ParserWrapper.parseString("a == 2006-05-17").toString()
		);
	}

	@Test
	public void testToStringDate3() throws ParseException {
		assertEquals(
			"a == 2006-05-18T01:30:00.0+0200",
			ParserWrapper.parseString("a == 2006-05-17T23:30Z").toString()
		);
	}
	
	@Test
	public void testToStringDate4() throws ParseException {
		assertEquals(
			"a == 2006-05-18T01:30:42.0+0200",
			ParserWrapper.parseString("a == 2006-05-17T23:30:42Z").toString()
		);
	}
	
	@Test
	public void testToStringDate5() throws ParseException {
		assertEquals(
			"a == 2006-05-18T01:30:42.100+0200",
			ParserWrapper.parseString("a == 2006-05-17T23:30:42.1Z").toString()
		);
	}

	@Test
	public void testToStringPackageTypeSource() throws ParseException {
		assertEquals(
			"a == source",
			ParserWrapper.parseString("a == source").toString()
		);
	}

	@Test
	public void testToStringPackageTypeBinary() throws ParseException {
		assertEquals(
			"a == binary",
			ParserWrapper.parseString("a == binary").toString()
		);
	}
	
	@Test
	public void testToStringPackageTypeTask() throws ParseException {
		assertEquals(
			"a == task",
			ParserWrapper.parseString("a == task").toString()
		);
	}
	
	@Test
	public void testToStringPackageTypeData() throws ParseException {
		assertEquals(
			"a == data",
			ParserWrapper.parseString("a == data").toString()
		);
	}

	@Test
	public void testToStringPattern() throws ParseException {
		assertEquals(
			"a == /pattern\\/pattern/",
			ParserWrapper.parseString("a == /pattern\\/pattern/").toString()
		);
	}
}
