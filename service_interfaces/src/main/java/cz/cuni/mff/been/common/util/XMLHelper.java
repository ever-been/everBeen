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

package cz.cuni.mff.been.common.util;

import java.io.File;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.cuni.mff.been.hostmanager.InputParseException;

import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;

/**
 * Class with static methods to simplify work with XML files (using DOM model).
 *
 * @author Branislav Repcek
 *
 */
public abstract class XMLHelper {
	
	/**
	 * Create new XML document.
	 * 
	 * @return Instance of the Document interface.
	 * 
	 * @throws ParserConfigurationException If parser configuration error occurred.
	 */
	public static Document createDocument() throws ParserConfigurationException {
		
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}
	
	/**
	 * Save DOM document tree into a file.
	 * 
	 * @param doc Document tree to save.
	 * @param fileName Name of the output file.
	 * @param indent If <code>true</code> nodes in output file will be indented for easier reading.
	 * @param encoding Name of the encoding of the output file. If <code>null</code> default encoding
	 *        is used.
	 *        
	 * @throws TransformerConfigurationException If there was an error when creating document Transformer.
	 * @throws TransformerException If there was an error writing output file.
	 */
	public static void saveDocument(Document doc, String fileName, boolean indent, String encoding) 
		throws TransformerConfigurationException, TransformerException {
		
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		
		transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
		
		if (encoding != null) {
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		}
		
		transformer.transform(new DOMSource(doc), new StreamResult(new File(fileName)));
	}
	
	/**
	 * Save DOM document tree into a file with UTF-16 encoding.
	 * 
	 * @param doc Document to save.
	 * @param fileName Name of the output file.
	 * 
	 * @throws TransformerConfigurationException If there was an error when creating document Transformer.
	 * @throws TransformerException If there was an error writing output file.
	 */
	public static void saveDocument(Document doc, String fileName) 
		throws TransformerConfigurationException, TransformerException {
		
		saveDocument(doc, fileName, true, "UTF-16");
	}
	
	/**
	 * Save object which implements XMLSerializableInterface to the XML file. Root node of the file will
	 * be node created by the given object.
	 * 
	 * @param data Object to save.
	 * @param fileName Name of output file.
	 * @param indent If <code>true</code> nodes in output file will be indented for easier reading.
	 * @param encoding Name of the encoding of the output file. If <code>null</code> default encoding
	 *        is used.
	 *        
	 * @throws ParserConfigurationException If there was an error creating DocumentBuilder.
	 * @throws TransformerConfigurationException If there was an error when creating document Transformer.
	 * @throws TransformerException If there was an error writing output file.
	 */
	public static void saveXMLSerializable(XMLSerializableInterface data, String fileName, boolean indent, String encoding) 
		throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
		document.appendChild(data.exportAsElement(document));
		
		saveDocument(document, fileName, indent, encoding);
	}
	
	/**
	 * Get list of child nodes with given name.
	 * 
	 * @param name Name of nodes to retrieve.
	 * @param node Parent node.
	 * 
	 * @return <code>ArrayList</code> containing nodes with given name.
	 */
	public static ArrayList< Node > getChildNodesByName(String name, Node node) {
	
		ArrayList< Node > res = new ArrayList< Node >();
		NodeList nl = node.getChildNodes();
		
		for (int i = 0; i < nl.getLength(); ++i) {
			
			if (nl.item(i).getNodeName().equals(name)) {
				res.add(nl.item(i));
			}
		}
		
		return res;
	}
	
	/**
	 * Get value of given node. For element nodes get value of first child node (it assumes no comments in file).
	 * 
	 * @param node Node to get value from.
	 * @return String containing value of node.
	 * 
	 * @throws InputParseException when unsupported node is passed as parameter.
	 */
	public static String getNodeValue(Node node) throws InputParseException {
		
		if (node == null) {
			return "";
		}
		
		switch (node.getNodeType()) {
			case Node.CDATA_SECTION_NODE:
			case Node.TEXT_NODE:
				return node.getNodeValue();
			
			case Node.ELEMENT_NODE:
				if (node.getChildNodes().getLength() != 0) {
					return node.getFirstChild().getNodeValue();
				} else {
					return "";
				}
			
			case Node.ATTRIBUTE_NODE:
				return node.getNodeValue();
				
			default:
				throw new InputParseException("Unsupported node type (node \"" + node.getNodeName() + "\").");
		}
	}
	
	/**
	 * Get first sub-node with given name.
	 * 
	 * @param name Name of node to retrieve.
	 * @param node Parent node.
	 * @return Requested node.
	 * 
	 * @throws InputParseException if node with given name is not sub-node of <code>node</code>.
	 */
	public static Node getSubNodeByName(String name, Node node) throws InputParseException {
		
		NodeList nl = node.getChildNodes();
		
		for (int i = 0; i < nl.getLength(); ++i) {
			if (nl.item(i).getNodeName().equals(name)) {
				return nl.item(i);
			}
		}
		
		throw new InputParseException("Node \"" + name + "\" is not sub-node of \"" + node.getNodeName() + "\".");
	}
	
	/**
	 * Get first sub-node of document with given name.
	 * 
	 * @param name Name of sub-node to retrieve.
	 * @param doc Document from which to retrieve node.
	 * @return Requested sub-node or <code>null</code> if node has not been found.
	 * 
	 * @throws InputParseException if node with given name is not sub-node of document.
	 */
	public static Node getSubNodeByName(String name, Document doc) throws InputParseException {
		
		NodeList nl = doc.getChildNodes();
		
		for (int i = 0; i < nl.getLength(); ++i) {
			if (nl.item(i).getNodeName().equals(name)) {
				return nl.item(i);
			}
		}
		
		throw new InputParseException("Node \"" + name + "\" is not sub-node of document.");
	}
	

	/**
	 * Get value of first sub-node with given name.
	 * 
	 * @param name Name of sub-node.
	 * @param node Parent node.
	 * @return Value of sub-node.
	 * 
	 * @throws InputParseException if sub-node with given name hasn't been found or is of unsupported type.
	 */
	public static String getSubNodeValueByName(String name, Node node) throws InputParseException {
		
		NodeList nl = node.getChildNodes();
		
		for (int i = 0; i < nl.getLength(); ++i) {
			
			if (nl.item(i).getNodeName().equals(name)) {
				return getNodeValue(nl.item(i));
			}
		}
		
		throw new InputParseException("Node \"" + name + "\" is not sub-node of \"" + node.getNodeName() + "\".");
	}
	
	/**
	 * Test if node has sub-node with specified name.
	 * 
	 * @param name Name of sub-node to look for.
	 * @param node Node in which we are searching.
	 * 
	 * @return <code>true</code> if given node has specified sub-node as a direct sub-node, 
	 *         <code>false</code> otherwise.
	 */
	public static boolean hasSubNode(String name, Node node) {
	
		NodeList subs = node.getChildNodes();
		
		for (int i = 0; i < subs.getLength(); ++i) {
			if (subs.item(i).getNodeName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get value of attribute with specified name from given node.
	 *  
	 * @param attrName Name of the attribute. 
	 * @param node Node containing the attribute. 
	 * @return Value of the attribute as String.
	 * 
	 * @throws InputParseException If attribute with given name was not found.
	 */
	public static String getAttributeValueByName(String attrName, Node node) throws InputParseException {
		
		Node item = node.getAttributes().getNamedItem(attrName);
		
		if (item == null) {
			throw new InputParseException("Attribute \"" + attrName + "\" not found.");
		} else {
			return item.getNodeValue();
		}
	}
	
	/**
	 * Test if given node contains attribute with specified name.
	 * 
	 * @param attributeName Name of the attribute to look for.
	 * @param node Node in which attribute is to be looked for.'
	 *  
	 * @return <code>true</code> if node contains given attribute, <code>false</code> if attribute 
	 *         with given name has not been found.
	 */
	public static boolean hasAttribute(String attributeName, Node node) {
		
		Node item = node.getAttributes().getNamedItem(attributeName);
		
		return (item != null);
	}
	
	/**
	 * Escape special characters in string so it can be written directly to XML file. It will substitute
	 * '<', '>', '&', ''', '"' with their escape sequences.
	 * 
	 * @param s original string.
	 * @return string with XML special characters escaped.
	 */
	public static String xMLize(String s) {
		
		String result = s;
		
		result.replaceAll("&", "&amp;");
		result.replaceAll("\'", "&apos;");
		result.replaceAll("\"", "&quot;");
		result.replaceAll("<", "&lt;");
		result.replaceAll(">", "&gt;");

		return result;
	}
	
	/**
	 * Create Element node containing specified value.
	 * 
	 * @param <T> Type of value to put to the Element.
	 * @param doc Document which will contain Element node.
	 * @param value Value to write to the node.
	 * @param name Name of the Element node.
	 * 
	 * @return Element node with specified name containing given value.
	 */
	public static < T > Element writeValueToElement(Document doc, T value, String name) {
		
		Element elem = doc.createElement(name);
		
		elem.appendChild(doc.createTextNode(String.valueOf(value)));
		
		return elem;		
	}
	
	/**
	 * Does nothing.
	 */
	private XMLHelper() {
	}
}
