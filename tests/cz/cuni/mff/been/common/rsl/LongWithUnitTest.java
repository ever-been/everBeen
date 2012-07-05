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

public class LongWithUnitTest {
	@Test
	public void testFirstConstructorOK() {
		LongWithUnit lu = new LongWithUnit(42, 'k', "Hz");
		assertEquals(lu.getValue(), 42);
		assertEquals(lu.getUnitPrefix(), 'k');
		assertEquals(lu.getUnitName(), "Hz");
	}

	@Test
	public void testFirstConstructorInvalidUnitPrefix() {
		try {
			new LongWithUnit(42, 'K', "Hz");
			fail();
		} catch (IllegalArgumentException e) {
			/* Eat it. */
		}
	}
	
	@Test
	public void testSecondConstructorPlainNumber() {
		LongWithUnit lu = new LongWithUnit("42");
		assertEquals(lu.getValue(), 42);
		assertEquals(lu.getUnitPrefix(), LongWithUnit.NO_UNIT_PREFIX);
		assertEquals(lu.getUnitName(), null);
	}

	@Test
	public void testSecondConstructorNumberWithUnit() {
		LongWithUnit lu = new LongWithUnit("42Hz");
		assertEquals(lu.getValue(), 42);
		assertEquals(lu.getUnitPrefix(), LongWithUnit.NO_UNIT_PREFIX);
		assertEquals(lu.getUnitName(), "Hz");
	}

	@Test
	public void testSecondConstructorNumberWithPrefix() {
		LongWithUnit lu = new LongWithUnit("42k");
		assertEquals(lu.getValue(), 42);
		assertEquals(lu.getUnitPrefix(), 'k');
		assertEquals(lu.getUnitName(), null);
	}

	@Test
	public void testSecondConstructorNumberWithPrefixedUnit() {
		LongWithUnit lu = new LongWithUnit("42kHz");
		assertEquals(lu.getValue(), 42);
		assertEquals(lu.getUnitPrefix(), 'k');
		assertEquals(lu.getUnitName(), "Hz");
	}

	@Test
	public void testSecondConstructorNull() {
		try {
			new LongWithUnit(null);
			fail();
		} catch (NullPointerException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testSecondConstructorInvalidNumber() {
		try {
			new LongWithUnit("%kHz");
			fail();
		} catch (IllegalArgumentException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testSecondConstructorInvalidUnitPrefix() {
		try {
			new LongWithUnit("42%Hz");
			fail();
		} catch (IllegalArgumentException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testSecondConstructorInvalidUnitName() {
		try {
			new LongWithUnit("42k%");
			fail();
		} catch (IllegalArgumentException e) {
			/* Eat it. */
		}
	}
	
	@Test
	public void testGetValueWithAppliedPrefix() {
		assertEquals(
			new LongWithUnit("42").getValueWithAppliedPrefix(),
			42L
		);
		assertEquals(
			new LongWithUnit("42k").getValueWithAppliedPrefix(),
			42L * 1024
		);
		assertEquals(
			new LongWithUnit("42M").getValueWithAppliedPrefix(),
			42L * 1024 * 1024
		);
		assertEquals(
			new LongWithUnit("42G").getValueWithAppliedPrefix(),
			42L * 1024 * 1024 * 1024
		);
		assertEquals(
			new LongWithUnit("42T").getValueWithAppliedPrefix(),
			42L * 1024 * 1024 * 1024 * 1024
		);
		assertEquals(
			new LongWithUnit("42P").getValueWithAppliedPrefix(),
			42L * 1024 * 1024 * 1024 * 1024 * 1024
		);
	}
	
	@Test
	public void testCompareTo() {
		/* Units do matter when comparing. */
		
		assertTrue(new LongWithUnit("42").compareTo(new LongWithUnit("42")) == 0);
		assertTrue(new LongWithUnit("42foo").compareTo(new LongWithUnit("42")) == 0);
		assertTrue(new LongWithUnit("42").compareTo(new LongWithUnit("42bar")) == 0);
		assertTrue(new LongWithUnit("42foo").compareTo(new LongWithUnit("42foo")) == 0);
		try {
			new LongWithUnit("42foo").compareTo(new LongWithUnit("42bar"));	
			fail();
		} catch (IllegalArgumentException e) {
			/* Eat it. */
		}

		/* Unit prefixes multiply correctly. */
		
		assertTrue(new LongWithUnit("1024Hz").compareTo(new LongWithUnit("1kHz")) == 0);
		assertTrue(new LongWithUnit("1024kHz").compareTo(new LongWithUnit("1MHz")) == 0);
		assertTrue(new LongWithUnit("1024MHz").compareTo(new LongWithUnit("1GHz")) == 0);
		assertTrue(new LongWithUnit("1024GHz").compareTo(new LongWithUnit("1THz")) == 0);
		assertTrue(new LongWithUnit("1024THz").compareTo(new LongWithUnit("1PHz")) == 0);

		/* All unit prefix cobinations compare correctly. */
		
		assertTrue(new LongWithUnit("42Hz").compareTo(new LongWithUnit("42Hz")) == 0);
		assertTrue(new LongWithUnit("42Hz").compareTo(new LongWithUnit("42kHz")) == -1);
		assertTrue(new LongWithUnit("42Hz").compareTo(new LongWithUnit("42MHz")) == -1);
		assertTrue(new LongWithUnit("42Hz").compareTo(new LongWithUnit("42GHz")) == -1);
		assertTrue(new LongWithUnit("42Hz").compareTo(new LongWithUnit("42THz")) == -1);
		assertTrue(new LongWithUnit("42Hz").compareTo(new LongWithUnit("42PHz")) == -1);

		assertTrue(new LongWithUnit("42kHz").compareTo(new LongWithUnit("42Hz")) == 1);
		assertTrue(new LongWithUnit("42kHz").compareTo(new LongWithUnit("42kHz")) == 0);
		assertTrue(new LongWithUnit("42kHz").compareTo(new LongWithUnit("42MHz")) == -1);
		assertTrue(new LongWithUnit("42kHz").compareTo(new LongWithUnit("42GHz")) == -1);
		assertTrue(new LongWithUnit("42kHz").compareTo(new LongWithUnit("42THz")) == -1);
		assertTrue(new LongWithUnit("42kHz").compareTo(new LongWithUnit("42PHz")) == -1);

		assertTrue(new LongWithUnit("42MHz").compareTo(new LongWithUnit("42Hz")) == 1);
		assertTrue(new LongWithUnit("42MHz").compareTo(new LongWithUnit("42kHz")) == 1);
		assertTrue(new LongWithUnit("42MHz").compareTo(new LongWithUnit("42MHz")) == 0);
		assertTrue(new LongWithUnit("42MHz").compareTo(new LongWithUnit("42GHz")) == -1);
		assertTrue(new LongWithUnit("42MHz").compareTo(new LongWithUnit("42THz")) == -1);
		assertTrue(new LongWithUnit("42MHz").compareTo(new LongWithUnit("42PHz")) == -1);

		assertTrue(new LongWithUnit("42GHz").compareTo(new LongWithUnit("42Hz")) == 1);
		assertTrue(new LongWithUnit("42GHz").compareTo(new LongWithUnit("42kHz")) == 1);
		assertTrue(new LongWithUnit("42GHz").compareTo(new LongWithUnit("42MHz")) == 1);
		assertTrue(new LongWithUnit("42GHz").compareTo(new LongWithUnit("42GHz")) == 0);
		assertTrue(new LongWithUnit("42GHz").compareTo(new LongWithUnit("42THz")) == -1);
		assertTrue(new LongWithUnit("42GHz").compareTo(new LongWithUnit("42PHz")) == -1);

		assertTrue(new LongWithUnit("42THz").compareTo(new LongWithUnit("42Hz")) == 1);
		assertTrue(new LongWithUnit("42THz").compareTo(new LongWithUnit("42kHz")) == 1);
		assertTrue(new LongWithUnit("42THz").compareTo(new LongWithUnit("42MHz")) == 1);
		assertTrue(new LongWithUnit("42THz").compareTo(new LongWithUnit("42GHz")) == 1);
		assertTrue(new LongWithUnit("42THz").compareTo(new LongWithUnit("42THz")) == 0);
		assertTrue(new LongWithUnit("42THz").compareTo(new LongWithUnit("42PHz")) == -1);

		assertTrue(new LongWithUnit("42PHz").compareTo(new LongWithUnit("42Hz")) == 1);
		assertTrue(new LongWithUnit("42PHz").compareTo(new LongWithUnit("42kHz")) == 1);
		assertTrue(new LongWithUnit("42PHz").compareTo(new LongWithUnit("42MHz")) == 1);
		assertTrue(new LongWithUnit("42PHz").compareTo(new LongWithUnit("42GHz")) == 1);
		assertTrue(new LongWithUnit("42PHz").compareTo(new LongWithUnit("42THz")) == 1);
		assertTrue(new LongWithUnit("42PHz").compareTo(new LongWithUnit("42PHz")) == 0);
	}

	@Test
	public void testEquals() {
		/* Units do matter when testing equality equality. */
		
		assertEquals(new LongWithUnit("42"), new LongWithUnit("42"));
		assertEquals(new LongWithUnit("42foo"), new LongWithUnit("42"));
		assertEquals(new LongWithUnit("42"), new LongWithUnit("42bar"));
		assertEquals(new LongWithUnit("42foo"), new LongWithUnit("42foo"));
		assertFalse(new LongWithUnit("42foo").equals(new LongWithUnit("42bar")));	

		/* Unit prefixes multiply correctly. */
		
		assertEquals(new LongWithUnit("1024Hz"), new LongWithUnit("1kHz"));
		assertEquals(new LongWithUnit("1024kHz"), new LongWithUnit("1MHz"));
		assertEquals(new LongWithUnit("1024MHz"), new LongWithUnit("1GHz"));
		assertEquals(new LongWithUnit("1024GHz"), new LongWithUnit("1THz"));
		assertEquals(new LongWithUnit("1024THz"), new LongWithUnit("1PHz"));

		/* All unit prefix cobinations compare correctly. */
		
		assertEquals(new LongWithUnit("42Hz"), new LongWithUnit("42Hz"));
		assertFalse(new LongWithUnit("42Hz").equals(new LongWithUnit("42kHz")));
		assertFalse(new LongWithUnit("42Hz").equals(new LongWithUnit("42MHz")));
		assertFalse(new LongWithUnit("42Hz").equals(new LongWithUnit("42GHz")));
		assertFalse(new LongWithUnit("42Hz").equals(new LongWithUnit("42THz")));
		assertFalse(new LongWithUnit("42Hz").equals(new LongWithUnit("42PHz")));

		assertFalse(new LongWithUnit("42kHz").equals(new LongWithUnit("42Hz")));
		assertEquals(new LongWithUnit("42kHz"), new LongWithUnit("42kHz"));
		assertFalse(new LongWithUnit("42kHz").equals(new LongWithUnit("42MHz")));
		assertFalse(new LongWithUnit("42kHz").equals(new LongWithUnit("42GHz")));
		assertFalse(new LongWithUnit("42kHz").equals(new LongWithUnit("42THz")));
		assertFalse(new LongWithUnit("42kHz").equals(new LongWithUnit("42PHz")));

		assertFalse(new LongWithUnit("42MHz").equals(new LongWithUnit("42Hz")));
		assertFalse(new LongWithUnit("42MHz").equals(new LongWithUnit("42kHz")));
		assertEquals(new LongWithUnit("42MHz"), new LongWithUnit("42MHz"));
		assertFalse(new LongWithUnit("42MHz").equals(new LongWithUnit("42GHz")));
		assertFalse(new LongWithUnit("42MHz").equals(new LongWithUnit("42THz")));
		assertFalse(new LongWithUnit("42MHz").equals(new LongWithUnit("42PHz")));

		assertFalse(new LongWithUnit("42GHz").equals(new LongWithUnit("42Hz")));
		assertFalse(new LongWithUnit("42GHz").equals(new LongWithUnit("42kHz")));
		assertFalse(new LongWithUnit("42GHz").equals(new LongWithUnit("42MHz")));
		assertEquals(new LongWithUnit("42GHz"), new LongWithUnit("42GHz"));
		assertFalse(new LongWithUnit("42GHz").equals(new LongWithUnit("42THz")));
		assertFalse(new LongWithUnit("42GHz").equals(new LongWithUnit("42PHz")));

		assertFalse(new LongWithUnit("42THz").equals(new LongWithUnit("42Hz")));
		assertFalse(new LongWithUnit("42THz").equals(new LongWithUnit("42kHz")));
		assertFalse(new LongWithUnit("42THz").equals(new LongWithUnit("42MHz")));
		assertFalse(new LongWithUnit("42THz").equals(new LongWithUnit("42GHz")));
		assertEquals(new LongWithUnit("42THz"), new LongWithUnit("42THz"));
		assertFalse(new LongWithUnit("42THz").equals(new LongWithUnit("42PHz")));

		assertFalse(new LongWithUnit("42PHz").equals(new LongWithUnit("42Hz")));
		assertFalse(new LongWithUnit("42PHz").equals(new LongWithUnit("42kHz")));
		assertFalse(new LongWithUnit("42PHz").equals(new LongWithUnit("42MHz")));
		assertFalse(new LongWithUnit("42PHz").equals(new LongWithUnit("42GHz")));
		assertFalse(new LongWithUnit("42PHz").equals(new LongWithUnit("42THz")));
		assertEquals(new LongWithUnit("42PHz"), new LongWithUnit("42PHz"));
	}
	
	@Test
	public void testToStringPlainNumber() {
		assertEquals(
			new LongWithUnit(42, LongWithUnit.NO_UNIT_PREFIX, null).toString(),
			"42"
		);
	}

	@Test
	public void testToStringNumberWithUnit() {
		assertEquals(
			new LongWithUnit(42, LongWithUnit.NO_UNIT_PREFIX, "Hz").toString(),
			"42Hz"
		);
	}

	@Test
	public void testToStringNumberWithPrefix() {
		assertEquals(
			new LongWithUnit(42, 'k', null).toString(),
			"42k"
		);
	}

	@Test
	public void testToStringNumberWithPrefixedUnit() {
		assertEquals(
			new LongWithUnit(42, 'k', "Hz").toString(),
			"42kHz"
		);
	}
}
