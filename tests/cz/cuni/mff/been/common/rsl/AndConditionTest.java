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
import org.junit.Before;
import org.junit.Test;

public class AndConditionTest {
	private Condition[] subConditionsAB;
	private Condition[] subConditionsABCD;
	private Condition[] subConditionsXAYB;

	@Before
	public void setUp() throws Exception {
		subConditionsAB = new Condition[] {
				new EqualsCondition<LongWithUnit>("a", new LongWithUnit("5")),
				new EqualsCondition<LongWithUnit>("b", new LongWithUnit("6"))
		};
		subConditionsABCD = new Condition[] {
				new EqualsCondition<LongWithUnit>("a", new LongWithUnit("5")),
				new EqualsCondition<LongWithUnit>("b", new LongWithUnit("6")),
				new EqualsCondition<LongWithUnit>("c", new LongWithUnit("7")),
				new EqualsCondition<LongWithUnit>("d", new LongWithUnit("8"))
		};
		subConditionsXAYB = new Condition[] {
				new QualifiedCondition(
					"x",
					new EqualsCondition<LongWithUnit>("a", new LongWithUnit("5"))
				),
				new QualifiedCondition(
					"y",
					new EqualsCondition<LongWithUnit>("b", new LongWithUnit("6"))
				)
		};
	}

	@Test
	public void testAndCondition1() throws ParseException  {
		assertEquals(
			new AndCondition(subConditionsAB),
			ParserWrapper.parseString("a == 5 && b == 6")
		);
	}
	
	@Test
	public void testAndCondition2() throws ParseException {
		assertEquals(
			new AndCondition(subConditionsABCD),
			ParserWrapper.parseString("a == 5 && b == 6 && c == 7 && d == 8")
		);
	}	

	@Test
	public void testAndCondition3() throws ParseException {
		assertEquals(
			new AndCondition(subConditionsAB),
			ParserWrapper.parseString("(a == 5) && (b == 6)")
		);
	}
	
	@Test
	public void testAndCondition4() throws ParseException {
		assertEquals(
			new AndCondition(subConditionsXAYB),
			ParserWrapper.parseString("x { a == 5 } && y { b == 6 }")
		);
	}
	
	/* Tests of toString method. */
	
	@Test
	public void testToString1() throws ParseException {
		assertEquals(
			"(a == 5) && (b == 6)",
			ParserWrapper.parseString("a == 5 && b == 6").toString()
		);
	}

	@Test
	public void testToString2() throws ParseException {
		assertEquals(
			"(a == 5) && (b == 6) && (c == 7) && (d == 8)",
			ParserWrapper.parseString("a == 5 && b == 6 && c == 7 && d == 8").toString()
		);
	}
}
