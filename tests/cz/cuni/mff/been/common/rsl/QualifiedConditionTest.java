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

import org.junit.Test;

public class QualifiedConditionTest {
	private ContainerProperty context = new DummyContext();

	@Test
	public void testQualifiedCondition1() throws ParseException {
		assertEquals(
			new QualifiedCondition(
				"x",
				new EqualsCondition<LongWithUnit>("a", new LongWithUnit("5"))
			),
			ParserWrapper.parseString("x { a == 5 }")
		);
	}
	
	@Test
	public void testQualifiedCondition2() throws ParseException {
		assertEquals(
			new QualifiedCondition(
				"x.y.z",
				new EqualsCondition<LongWithUnit>("a", new LongWithUnit("5"))
			),
			ParserWrapper.parseString("x.y.z { a == 5 }")
		);
	}	

	/* Tests of toString method. */
	
	@Test
	public void testToString() throws ParseException {
		assertEquals(
			"x { a == 5 }",
			ParserWrapper.parseString("x { a == 5 }").toString()
		);
	}
	
	/* Tests of evaluation. */
	
	@Test
	public void testInvalidProperty() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("invalidProperty { subproperty == 42 }", context);
			fail();
		} catch (InvalidPropertyException e) {
			/* Eat it. */
		}
	}
	
	@Test
	public void testInvalidPropertyType() throws ParseException, RSLSemanticException {
		try {
			Utils.eval("longProperty { subproperty == 42 }", context);
			fail();
		} catch (InvalidPropertyException e) {
			/* Eat it. */
		}
	}
	
	@Test
	public void testContainerPropertyTrue() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("mother { age == 30 } ", context));
	}

	@Test
	public void testContainerPropertyFalse() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("mother { age > 30 } ", context));
	}

	@Test
	public void testArrayPropertyTrue() throws ParseException, RSLSemanticException {
		assertTrue(Utils.eval("children { age == 12 } ", context));
	}

	@Test
	public void testArrayPropertyFalse() throws ParseException, RSLSemanticException {
		assertFalse(Utils.eval("children { age < 12 } ", context));
	}

	@Test
	public void testArrayPropertyFalseSpecial() throws ParseException, RSLSemanticException {
		/* Tests if the expression in {...} is really evaluated in context of one
		 * child, not both.
		 */ 
		assertFalse(Utils.eval("children { name == \"name1\" && age == 13 } ", context));
	}
}
