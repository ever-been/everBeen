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

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cz.cuni.mff.been.hostmanager.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.value.ValueBoolean;
import cz.cuni.mff.been.hostmanager.value.ValueDouble;
import cz.cuni.mff.been.hostmanager.value.ValueInteger;
import cz.cuni.mff.been.hostmanager.value.ValueList;
import cz.cuni.mff.been.hostmanager.value.ValueRange;
import cz.cuni.mff.been.hostmanager.value.ValueRegexp;
import cz.cuni.mff.been.hostmanager.value.ValueString;
import cz.cuni.mff.been.hostmanager.value.ValueType;
import cz.cuni.mff.been.hostmanager.value.ValueVersion;

/**
 * This class test serialization routines of PropertyTree class.
 *
 * @author Branislav Repcek
 */
public class TestPropertyTreeSerialization {

	private PropertyTree tree;
	
	/*
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		tree = new PropertyTree("root");
		
		PropertyTree nodeSimple = new PropertyTree("nodeSimple", tree);
		PropertyTree nodeRange = new PropertyTree("nodeRange", tree);
		PropertyTree nodeLists = new PropertyTree("nodeLists", tree);
		PropertyTree nodeLists1 = new PropertyTree("lists", nodeLists);
		PropertyTree nodeLists2 = new PropertyTree("lists", nodeLists);
		
		nodeSimple.addProperty("boolean", new ValueBoolean(false));
		nodeSimple.addProperty("integer", new ValueInteger(800));
		nodeSimple.addProperty("double", new ValueDouble(5.89));
		nodeSimple.addProperty("string", new ValueString("zelenina je zdrava"));
		nodeSimple.addProperty("regexp", new ValueString(".*\\[a-du-z]+"));
		nodeSimple.addProperty("version", new ValueVersion("3.14.159.26"));
		
		nodeRange.addProperty("boolean", new ValueRange< ValueBoolean >(new ValueBoolean(true), null, ValueType.BOOLEAN));
		nodeRange.addProperty("integer", 
				new ValueRange< ValueInteger >(new ValueInteger(-100), new ValueInteger(400), false, true, ValueType.INTEGER));
		nodeRange.addProperty("double", 
				new ValueRange< ValueDouble >(new ValueDouble(10.8), new ValueDouble(8908.387), ValueType.DOUBLE));
		nodeRange.addProperty("string",
				new ValueRange< ValueString >(new ValueString("aaa"), new ValueString("zzz"), ValueType.STRING));
		nodeRange.addProperty("regexp",
				new ValueRange< ValueRegexp >(new ValueRegexp(".*"), new ValueRegexp("abc"), ValueType.REGEXP));
		nodeRange.addProperty("version",
				new ValueRange< ValueVersion >(new ValueVersion("1.0"), new ValueVersion("2.0"), ValueType.VERSION));
		
		{
			ValueBoolean []vb = {new ValueBoolean(false), new ValueBoolean(true) };
			ValueInteger []vi = {new ValueInteger(10), new ValueInteger(20), new ValueInteger(30) };
			ValueDouble []vd = {new ValueDouble(1.2), new ValueDouble(9.0)};
			ValueInteger []vi2 = {};
			ValueString []vs = {new ValueString("qwerty"), new ValueString("uiop")};
			ValueRegexp []vr = {new ValueRegexp(".*"), new ValueRegexp("a+")};
			ValueVersion []vv = {new ValueVersion("1.9"), new ValueVersion("4.5")};
			
			nodeLists1.addProperty("boolean", new ValueList< ValueBoolean >(vb, ValueType.BOOLEAN));
			nodeLists1.addProperty("integer", new ValueList< ValueInteger >(vi, ValueType.INTEGER));
			nodeLists1.addProperty("double", new ValueList< ValueDouble >(vd, ValueType.DOUBLE));
			nodeLists1.addProperty("string", new ValueList< ValueString >(vs, ValueType.STRING));
			
			nodeLists2.addProperty("integer", new ValueList< ValueInteger >(vi2, ValueType.INTEGER));
			nodeLists2.addProperty("regexp", new ValueList< ValueRegexp >(vr, ValueType.REGEXP));
			nodeLists2.addProperty("version", new ValueList< ValueVersion >(vv, ValueType.VERSION));
		}
	}

	/**
	 * Test serialization/deserialization and equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void test() throws Exception {

		Document document = XMLHelper.createDocument();
		Element element = tree.exportAsElement(document);
		
		PropertyTree read = new PropertyTree(element);
		
		assertTrue(tree.equals(read));
	}
}
