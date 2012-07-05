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

package cz.cuni.mff.been.hostmanager.value;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.database.AlternativeRestriction;
import cz.cuni.mff.been.hostmanager.database.NameValuePair;
import cz.cuni.mff.been.hostmanager.database.ObjectRestriction;
import cz.cuni.mff.been.hostmanager.util.XMLHelper;

/**
 * This will test XML serialization and deserialization routines of ValueXXX and XXXRestriction classes.
 * 
 * Note: If the VM terminates unexpectedly, some files may be left over in current directory (all such
 * files will have testfile extension). Please delete those files.
 * 
 * OK, now I really have no idea why some of the files are not sometimes not deleted...
 *
 * @author Branislav Repcek
 */
public class TestValueSerialization {
	
	private ValueBoolean []origBoolean;
	private ValueInteger origInteger;
	private ValueDouble origDouble;
	private ValueString origString;
	private ValueRegexp []origRegexp;
	private ValueVersion origVersion;

	private ValueRange< ValueInteger > []origRangeInteger;
	private ValueRange< ValueBoolean > origRangeBoolean;
	private ValueRange< ValueDouble > origRangeDouble;
	private ValueRange< ValueString > origRangeString;
	private ValueRange< ValueRegexp > origRangeRegexp;
	private ValueRange< ValueVersion > origRangeVersion;
	
	private ValueList< ValueBoolean > origListBoolean;
	private ValueList< ValueInteger > origListInteger;
	private ValueList< ValueDouble > origListDouble;
	private ValueList< ValueString > origListString;
	private ValueList< ValueRegexp > origListRegexp;
	private ValueList< ValueVersion > origListVersion;
	
	private ValueBoolean []readBoolean;
	private ValueInteger readInteger;
	private ValueDouble readDouble;
	private ValueString readString;
	private ValueRegexp []readRegexp;
	private ValueVersion readVersion;

	private ValueRange< ValueInteger > []readRangeInteger;
	private ValueRange< ValueBoolean > readRangeBoolean;
	private ValueRange< ValueDouble > readRangeDouble;
	private ValueRange< ValueString > readRangeString;
	private ValueRange< ValueRegexp > readRangeRegexp;
	private ValueRange< ValueVersion > readRangeVersion;
	
	private ValueList< ValueBoolean > readListBoolean;
	private ValueList< ValueInteger > readListInteger;
	private ValueList< ValueDouble > readListDouble;
	private ValueList< ValueString > readListString;
	private ValueList< ValueRegexp > readListRegexp;
	private ValueList< ValueVersion > readListVersion;

	private NameValuePair []origNVP;
	private ObjectRestriction []origObjectRestrictions;
	private AlternativeRestriction origAlternative;
	
	private NameValuePair []readNVP;
	private ObjectRestriction []readObjectRestrictions;
	private AlternativeRestriction readAlternative;
	
	/**
	 * Initialize tests.
	 */
	@Before
	public void setUp() throws Exception {
		
		createValues();
		writeToFile();
		readFromFile();
	}

	/**
	 * Generate random integer from given interval.
	 * 
	 * @param a Minimum allowed value.
	 * @param b Maximum allowed value.
	 * 
	 * @return Random integer from given range.
	 */
	private int randomInt(int a, int b) {
		
		return a + (int) ((b - a) * Math.random()); 
	}

	/**
	 * Create test data.
	 * 
	 * @throws Exception If an error occured.
	 */
	@SuppressWarnings("unchecked")
	private void createValues() throws Exception {

		origBoolean = new ValueBoolean[] {
				new ValueBoolean(true),
				new ValueBoolean(false)
		};
		
		origInteger = new ValueInteger((int) (Math.random() * 100000));
		
		origDouble = new ValueDouble(2.58);
		
		origString = new ValueString("abcdefghijklmnopqrstuvxyz");
		
		origRegexp = new ValueRegexp[] {
				new ValueRegexp("[1-9]*.*", true),
				new ValueRegexp("[1-9]*.*", false)
		};
		
		origVersion = new ValueVersion("1.5.7-beta2");
		
		origRangeInteger = new ValueRange[] {
				new ValueRange< ValueInteger >(new ValueInteger(1), new ValueInteger(101), true, true, ValueType.INTEGER),
				new ValueRange< ValueInteger >(new ValueInteger(2), new ValueInteger(102), false, true, ValueType.INTEGER),
				new ValueRange< ValueInteger >(new ValueInteger(3), new ValueInteger(103), true, false, ValueType.INTEGER),
				new ValueRange< ValueInteger >(new ValueInteger(4), new ValueInteger(104), false, false, ValueType.INTEGER),
				new ValueRange< ValueInteger >(null, new ValueInteger(5), false, false, ValueType.INTEGER),
				new ValueRange< ValueInteger >(null, new ValueInteger(6), false, true, ValueType.INTEGER),
				new ValueRange< ValueInteger >(new ValueInteger(7), null, true, false, ValueType.INTEGER),
				new ValueRange< ValueInteger >(null, null, ValueType.INTEGER),
				new ValueRange< ValueInteger >(new ValueInteger(8), null, false, false, ValueType.INTEGER)				
		};
		
		origRangeBoolean = new ValueRange< ValueBoolean >(new ValueBoolean(false), new ValueBoolean(true), ValueType.BOOLEAN);
		
		origRangeDouble = new ValueRange< ValueDouble >(new ValueDouble(1.25), new ValueDouble(10.76), ValueType.DOUBLE);
		
		origRangeString = new ValueRange< ValueString >(new ValueString("abc"), new ValueString("def"), ValueType.STRING);
		
		origRangeRegexp = new ValueRange< ValueRegexp >(new ValueRegexp("abc"), new ValueRegexp("def"), ValueType.REGEXP);
		
		origRangeVersion = new ValueRange< ValueVersion >(new ValueVersion("1.5"), new ValueVersion("2.0"), true, true, ValueType.VERSION);
		
		origListBoolean = new ValueList< ValueBoolean >(ValueType.BOOLEAN);
		for (int i = 0; i < randomInt(5, 15); ++i) {
			origListBoolean.add(new ValueBoolean(Math.random() > 0.5));
		}
		
		origListInteger = new ValueList< ValueInteger >(ValueType.INTEGER);
		for (int i = 0; i < randomInt(5, 15); ++i) {
			origListInteger.add(new ValueInteger(randomInt(-1000, 1000)));
		}
		
		{
			ValueDouble []doubles = {
					new ValueDouble(-1235.39847),
					new ValueDouble(37381.4737),
					new ValueDouble(111111.11111),
					new ValueDouble(0)
			};
			
			origListDouble = new ValueList< ValueDouble >(doubles, ValueType.DOUBLE);
		}

		{
			ValueString []strings = new ValueString[] {
					new ValueString("abc"), 
					new ValueString("def"), 
					new ValueString("zyz"), 
					new ValueString("yyyy"), 
					new ValueString("fff")
			};
			origListString = new ValueList< ValueString >(strings, ValueType.STRING);
		}
		
		{
			ValueRegexp []regs = new ValueRegexp[] {
					new ValueRegexp("aaaa"),
					new ValueRegexp(".*"),
					new ValueRegexp("[0-9]*")
			};
			origListRegexp = new ValueList< ValueRegexp >(regs, ValueType.REGEXP);
		}

		origListVersion = new ValueList< ValueVersion >(ValueType.VERSION);
		for (int i = 0; i < randomInt(5, 15); ++i) {
			origListVersion.add(new ValueVersion(randomInt(1, 4), randomInt(0, 20), randomInt(0, 3000)));
		}

		ValueInteger []ints = new ValueInteger[] {
				new ValueInteger(100),
				new ValueInteger(200),
				new ValueInteger(300),
				new ValueInteger(400)
		};
		
		origNVP = new NameValuePair[] {
			new NameValuePair("meno0", new ValueBoolean(true)),
			new NameValuePair("meno1", new ValueInteger(10)),
			new NameValuePair("meno2", new ValueString("ahoj")),
			new NameValuePair("meno3", new ValueRegexp("x.*")),
			new NameValuePair("meno4", new ValueRange< ValueDouble >(new ValueDouble(10.5), 
					new ValueDouble(88.3), true, false, ValueType.DOUBLE)),
			new NameValuePair("meno5", new ValueList< ValueInteger >(ints, ValueType.INTEGER)),
			new NameValuePair("meno6", new ValueDouble(3.14156))
		};
		
		origObjectRestrictions = new ObjectRestriction[] {
				new ObjectRestriction("object0", origNVP[0]),
				new ObjectRestriction("object1", origNVP[1]),
				new ObjectRestriction("object2", origNVP[2]),
				new ObjectRestriction("object3", origNVP[3]),
				new ObjectRestriction("object4", origNVP[4]),
				new ObjectRestriction("object5", origNVP[5]),
				new ObjectRestriction("object6", origNVP[6]),
				new ObjectRestriction("object7", origNVP)
		};
		
		origAlternative = new AlternativeRestriction(origObjectRestrictions);
	}

	/**
	 * Delete file with specified name on VM termination.
	 * 
	 * @param name Name of the file.
	 */
	private static void deleteFile(String name) {
		
		File f = new File(name);
		
		f.deleteOnExit();
	}
	
	/**
	 * Write all data to test file.
	 * 
	 * @throws Exception If an error occured.
	 */
	public void writeToFile() throws Exception {

		{
			Document doc = XMLHelper.createDocument();
			Element root = doc.createElement("root");
			doc.appendChild(root);
			
			root.appendChild(origBoolean[0].exportAsElement(doc));
			root.appendChild(origBoolean[1].exportAsElement(doc));
			root.appendChild(origInteger.exportAsElement(doc));
			root.appendChild(origDouble.exportAsElement(doc));
			root.appendChild(origString.exportAsElement(doc));
			root.appendChild(origRegexp[0].exportAsElement(doc));
			root.appendChild(origRegexp[1].exportAsElement(doc));
			root.appendChild(origVersion.exportAsElement(doc));
			
			XMLHelper.saveDocument(doc, "simple.testfile", true, "UTF-16");
		}		

		{
			Document doc = XMLHelper.createDocument();
			Element root = doc.createElement("root");
			doc.appendChild(root);

			root.appendChild(origRangeBoolean.exportAsElement(doc));
			for (int i = 0; i < origRangeInteger.length; ++i) {
				root.appendChild(origRangeInteger[i].exportAsElement(doc));
			}
			root.appendChild(origRangeDouble.exportAsElement(doc));
			root.appendChild(origRangeString.exportAsElement(doc));
			root.appendChild(origRangeRegexp.exportAsElement(doc));
			root.appendChild(origRangeVersion.exportAsElement(doc));

			XMLHelper.saveDocument(doc, "range.testfile", true, "UTF-16");
		}		

		{
			Document doc = XMLHelper.createDocument();
			Element root = doc.createElement("root");
			doc.appendChild(root);

			root.appendChild(origListBoolean.exportAsElement(doc));
			root.appendChild(origListInteger.exportAsElement(doc));
			root.appendChild(origListDouble.exportAsElement(doc));
			root.appendChild(origListString.exportAsElement(doc));
			root.appendChild(origListRegexp.exportAsElement(doc));
			root.appendChild(origListVersion.exportAsElement(doc));

			XMLHelper.saveDocument(doc, "list.testfile", true, "UTF-16");
		}
		
		{
			Document doc = XMLHelper.createDocument();
			Element root = doc.createElement("root");
			doc.appendChild(root);
			
			for (NameValuePair current: origNVP) {
				root.appendChild(current.exportAsElement(doc));
			}
			
			XMLHelper.saveDocument(doc, "nvps.testfile");
		}		
		
		{
			Document doc = XMLHelper.createDocument();
			Element root = doc.createElement("root");
			doc.appendChild(root);
			
			for (ObjectRestriction current: origObjectRestrictions) {
				root.appendChild(current.exportAsElement(doc));
			}
			
			XMLHelper.saveDocument(doc, "restr.testfile");
		}

		{
			Document doc = XMLHelper.createDocument();
			Element root = doc.createElement("root");
			doc.appendChild(root);
			
			root.appendChild(origAlternative.exportAsElement(doc));
			
			XMLHelper.saveDocument(doc, "alt.testfile");
		}
	}		

	@SuppressWarnings("unchecked")
	private void readFromFile() throws Exception {

		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File("simple.testfile"));
			Node root = XMLHelper.getSubNodeByName("root", doc);
			
			ArrayList< Node > boolNodes = XMLHelper.getChildNodesByName("boolean", root);
			readBoolean = new ValueBoolean[2];
			readBoolean[0] = new ValueBoolean(boolNodes.get(0));
			readBoolean[1] = new ValueBoolean(boolNodes.get(1));
			
			readInteger = new ValueInteger(XMLHelper.getSubNodeByName("integer", root));
			
			readDouble = new ValueDouble(XMLHelper.getSubNodeByName("double", root));
			
			readString = new ValueString(XMLHelper.getSubNodeByName("string", root));
			
			ArrayList< Node > regNodes = XMLHelper.getChildNodesByName("regexp", root);
			readRegexp = new ValueRegexp[2];
			readRegexp[0] = new ValueRegexp(regNodes.get(0));
			readRegexp[1] = new ValueRegexp(regNodes.get(1));
			
			readVersion = new ValueVersion(XMLHelper.getSubNodeByName("version", root));
		}

		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File("range.testfile"));
			Node root = XMLHelper.getSubNodeByName("root", doc);
			
			ArrayList< Node > rangeNodes = XMLHelper.getChildNodesByName("range", root);
			int i = 0;
			readRangeBoolean = new ValueRange(rangeNodes.get(i++));
			
			readRangeInteger = new ValueRange[origRangeInteger.length];
			for (int j = 0; j < readRangeInteger.length; ++i, ++j) {
				readRangeInteger[j] = new ValueRange(rangeNodes.get(i));
			}
			
			readRangeDouble = new ValueRange(rangeNodes.get(i++));
			readRangeString = new ValueRange(rangeNodes.get(i++));
			readRangeRegexp = new ValueRange(rangeNodes.get(i++));
			readRangeVersion = new ValueRange(rangeNodes.get(i++));
		}

		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File("list.testfile"));
			Node root = XMLHelper.getSubNodeByName("root", doc);
			
			ArrayList< Node > listNodes = XMLHelper.getChildNodesByName("list", root);
			
			readListBoolean = new ValueList(listNodes.get(0));
			readListInteger = new ValueList(listNodes.get(1));
			readListDouble = new ValueList(listNodes.get(2));
			readListString = new ValueList(listNodes.get(3));
			readListRegexp = new ValueList(listNodes.get(4));
			readListVersion = new ValueList(listNodes.get(5));
		}
		
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File("nvps.testfile"));
			Node root = XMLHelper.getSubNodeByName("root", doc);
			
			ArrayList< Node > nodes = XMLHelper.getChildNodesByName("namevalue", root);
			readNVP = new NameValuePair[origNVP.length];
			
			int i = 0;
			for (Node n: nodes) {
				readNVP[i] = new NameValuePair(n);
				i += 1;
			}
		}
		
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File("restr.testfile"));
			Node root = XMLHelper.getSubNodeByName("root", doc);
			
			ArrayList< Node > nodes = XMLHelper.getChildNodesByName("restriction", root);
			readObjectRestrictions = new ObjectRestriction[origObjectRestrictions.length];
			
			int i = 0;
			
			for (Node n: nodes) {
				readObjectRestrictions[i] = new ObjectRestriction(n);
				i += 1;
			}
		}

		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new File("alt.testfile"));
			Node root = XMLHelper.getSubNodeByName("root", doc);
			
			Node node = XMLHelper.getSubNodeByName("alternative", root);
			
			readAlternative = new AlternativeRestriction(node);
		}
	}
	
	/**
	 * Test <code>ValueBoolean</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testBoolean() throws Exception {

		assertTrue(origBoolean[0].equals(readBoolean[0]));
		assertTrue(origBoolean[1].equals(readBoolean[1]));		
	}
	
	/**
	 * Test <code>ValueInteger</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testInteger() throws Exception {
		assertTrue(origInteger.equals(readInteger));
	}
	
	/**
	 * Test <code>Value</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testDouble() throws Exception {
		
		assertTrue(origDouble.equals(readDouble));
	}
	
	/**
	 * Test <code>ValueString</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testString() throws Exception {
		assertTrue(origString.equals(readString));
	}
	
	/**
	 * Test <code>ValueRegexp</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testRegexp() throws Exception {
		assertTrue(origRegexp[0].equals(readRegexp[0]));
		assertTrue(origRegexp[1].equals(readRegexp[1]));
	}
	
	/**
	 * Test <code>ValueVerions</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testVersion() throws Exception {
		assertTrue(origVersion.equals(readVersion));
	}
	
	/**
	 * Test <code>ValueRange</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testRangeInteger() throws Exception {
		for (int i = 0; i < origRangeInteger.length; ++i) {
			assertTrue(origRangeInteger[i].equals(readRangeInteger[i]));
		}
	}
	
	/**
	 * Test <code>ValueRange</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testRangeBoolean() throws Exception {
		assertTrue(origRangeBoolean.equals(readRangeBoolean));
	}
	
	/**
	 * Test <code>ValueRange</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testRangeDouble() throws Exception {
		assertTrue(origRangeDouble.equals(readRangeDouble));
	}
	
	/**
	 * Test <code>ValueRange</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testRangeString() throws Exception {
		assertTrue(origRangeString.equals(readRangeString));
	}
	
	/**
	 * Test <code>ValueRange</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testRangeRegexp() throws Exception {
		assertTrue(origRangeRegexp.equals(readRangeRegexp));
	}
	
	/**
	 * Test <code>ValueRange</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testRangeVersion() throws Exception {
		assertTrue(origRangeVersion.equals(readRangeVersion));
	}
	
	/**
	 * Test <code>ValueList</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testListBoolean() throws Exception {
		assertTrue(origListBoolean.equals(readListBoolean));
	}
	
	/**
	 * Test <code>ValueList</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testListInteger() throws Exception {
		assertTrue(origListInteger.equals(readListInteger));
	}
	
	/**
	 * Test <code>ValueList</code> equals method. 
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testListDouble() throws Exception {
		assertTrue(origListDouble.equals(readListDouble));
	}
	
	/**
	 * Test  <code>ValueList</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testListString() throws Exception {
		assertTrue(origListString.equals(readListString));
	}
	
	/**
	 * Test  <code>ValueList</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testListRegexp() throws Exception {
		assertTrue(origListRegexp.equals(readListRegexp));
	}
	
	/**
	 * Test <code>ValueList</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testListVersion() throws Exception {
		assertTrue(origListVersion.equals(readListVersion));
	}
	
	/**
	 * Test <code>NameValuePair</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testNameValuePair() throws Exception {
		for (int i = 0; i < origNVP.length; ++i) {
			assertTrue(origNVP[i].equals(readNVP[i]));
		}
	}
	
	/**
	 * Test <code>ObjectRestriction</code> equals method.
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testRestriction() throws Exception {
		for (int i = 0; i < origObjectRestrictions.length; ++i) {		
			assertTrue(origObjectRestrictions[i].equals(readObjectRestrictions[i]));
		}
	}
	
	/**
	 * Test <code>AlternativeRestriction</code> equals method. 
	 * 
	 * @throws Exception If some error occured.
	 */
	@Test
	public void testAlternative() throws Exception {
		assertTrue(origAlternative.equals(readAlternative));
	}
	
	/*
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	public void tearDown() {
		
		deleteFile("simple.testfile");
		deleteFile("range.testfile");
		deleteFile("list.testfile");
		deleteFile("nvps.testfile");
		deleteFile("restr.testfile");
		deleteFile("alt.testfile");
	}
}
