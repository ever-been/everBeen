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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.ValueTypeIncorrectException;
import cz.cuni.mff.been.hostmanager.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.value.ValueBasicInterface;
import cz.cuni.mff.been.hostmanager.value.ValueCommonInterface;
import cz.cuni.mff.been.hostmanager.value.ValueList;
import cz.cuni.mff.been.hostmanager.value.ValueRange;
import cz.cuni.mff.been.hostmanager.value.ValueRegexp;

/**
 * Class which represent on node in tree-like hierarchy. For more information about terminology 
 * see comments for the PropertyTreeInterface interface.
 *
 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface
 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeReadInterface
 *
 * @author Branislav Repcek
 */
class PropertyTree 
implements Serializable, PropertyTreeInterface, ModifiableInterface, XMLSerializableInterface {

	private static final long	serialVersionUID	= -5461995517625855458L;

	/**
	 * Name of the XML node.
	 */
	public static final String XML_NODE_NAME = "propertyTree";
	
	/**
	 * List of local properties.
	 */
	private TreeMap< String, ValueCommonInterface > localProperties;
	
	/**
	 * List of local child objects.
	 */
	private TreeMap< String, ArrayList< PropertyTreeInterface > > childObjects;
	
	/**
	 * Name of this node.
	 */
	private String objectName;
	
	/**
	 * Index of this object if multiple objects of the same type are child nodes of the same parent.
	 */
	private int objectIndex;
	
	/**
	 * Parent node.
	 */
	private PropertyTreeInterface parent;

	/**
	 * Number of modification of this instance.
	 */
	private int modificationCount;
	
	/**
	 * Modification count on last reset.
	 */
	private int resetModCount;
	
	/**
	 * Cache for hash code.
	 */
	private int hashCache;
	
	/**
	 * Modification count of hash code cache.
	 */
	private int hashCacheModCount;
	
	/**
	 * Create new object in hierarchy.
	 * 
	 * @param name Name of object. It can't contain dot, question mark, number or whitespace characters.
	 * @param parentObj Parent object in the hierarchy. <code>null</code> for root node.
	 */
	public PropertyTree(String name, PropertyTreeInterface parentObj) {
		
		objectName = name;
		localProperties = new TreeMap< String, ValueCommonInterface >();
		childObjects = new TreeMap< String, ArrayList< PropertyTreeInterface > >();
		objectIndex = 0;
		
		if (parentObj != null) {
			parentObj.addObject(this);
		}
		
		this.parent = parentObj;
		
		modificationCount = 1;
		resetModCount = 0;
		hashCacheModCount = 0;
		
		rehash();
	}

	/**
	 * Create root object of the hierarchy.
	 * 
	 * @param name Name of the object to create.
	 */
	public PropertyTree(String name) {

		objectName = name;
		localProperties = new TreeMap< String, ValueCommonInterface >();
		childObjects = new TreeMap< String, ArrayList< PropertyTreeInterface > >();
		objectIndex = 0;
		parent = null;
		
		modificationCount = 1;
		resetModCount = 0;
		hashCacheModCount = 0;
		
		rehash();
	}
	
	/**
	 * Create new PropertyTree instance from XML file node.
	 * 
	 * @param node Node to parse.
	 * 
	 * @throws InputParseException If an error occurred while parsing input data.
	 */
	public PropertyTree(Node node) throws InputParseException {
		
		localProperties = new TreeMap< String, ValueCommonInterface >();
		childObjects = new TreeMap< String, ArrayList< PropertyTreeInterface > >();

		modificationCount = 1;
		resetModCount = 0;
		hashCacheModCount = 0;

		parseXMLNode(node);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(Node)
	 */
	public void parseXMLNode(Node node) throws InputParseException {
		
		localProperties.clear();
		childObjects.clear();

		objectName = XMLHelper.getAttributeValueByName("typeName", node);
		objectIndex = -1;
		modify();
		parent = null;
		
		Node objects = null;
		
		try {
			objects = XMLHelper.getSubNodeByName("objects", node);
		} catch (InputParseException e) {
			// do nothing, if such node does not exist, we have no sub-objects.
		}
		
		if (objects != null) {
			// now parse all child objects
			ArrayList< Node > subnodes = XMLHelper.getChildNodesByName(XML_NODE_NAME, objects);
			
			for (Node n: subnodes) {
				try {
					PropertyTree pt = new PropertyTree(n);
				
					addObject(pt);
				} catch (InputParseException e) {
					throw new InputParseException("Error parsing property tree data.", e);
				}
			}
		}
		
		Node properties = null;
		
		try {
			properties = XMLHelper.getSubNodeByName("properties", node);
		} catch (InputParseException e) {
			// do nothing, we have no local properties.
		}
		
		if (properties != null) {
			// parse all properties
			ArrayList< Node > subnodes = XMLHelper.getChildNodesByName(NameValuePair.XML_NODE_NAME, properties);
			
			for (Node n: subnodes) {
				try {
					NameValuePair p = new NameValuePair(n);
					
					addProperty(p);
				} catch (Exception e) {
					throw new InputParseException("Error parsing PropertyTree object \""
							+ objectName + "\".", e);
				}
			}
		}
		
		rehash();
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(Document)
	 */
	public Element exportAsElement(Document document) {
		
		Element element = document.createElement(XML_NODE_NAME);

		element.setAttribute("typeName", getTypeName());
		
		if (getObjectCount() > 0) {
			Element objects = document.createElement("objects");
			
			element.appendChild(objects);
		
			for ( PropertyTreeReadInterface object : getObjects() ) {
				
				objects.appendChild( object.exportAsElement( document ) );
			}
		}
		
		if (this.localProperties.size() > 0) {
			Element properties = document.createElement("properties");
			
			element.appendChild(properties);
			
			for ( NameValuePair property : getProperties() ) {
				
				properties.appendChild( property.exportAsElement( document ) );
			}
		}
		
		return element;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	public String getXMLNodeName() {
		
		return XML_NODE_NAME;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.ModifiableInterface#isModified()
	 */
	public boolean isModified() {
		
		if (modificationCount != resetModCount) {
			return true;
		}

		for ( PropertyTreeReadInterface object : getObjects() ) {
			PropertyTree c = (PropertyTree) object;
			
			if (c.isModified()) {
				return true;
			}
		}
		
		return false;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.ModifiableInterface#isModified(boolean)
	 */
	public boolean isModified(boolean reset) {
		
		// not the best possible way to do this, but it works...
		
		boolean result = isModified();
		
		if (result && reset) {
			for ( PropertyTreeReadInterface object : getObjects() ) {
				PropertyTree c = (PropertyTree) object;

				c.reset();
			}
		}
		
		return result;
	}
	
	/**
	 * Reset modification flags.
	 * 
	 * @param recursive If set to <code>true</code> modification flags will also be reset in all
	 *        child objects. If set to <code>false</code> modification flags will be reset only in 
	 *        this instance.
	 */
	public void reset(boolean recursive) {
		
		reset();
		
		if (recursive) {
			for ( PropertyTreeReadInterface object : getObjects() ) {
				PropertyTree c = (PropertyTree) object;

				c.reset(true);
			}
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getPropertyNames()
	 */
	public String[] getPropertyNames() {

		String []result = new String[localProperties.keySet().size()];
		
		return localProperties.keySet().toArray(result);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getObjectNames()
	 */
	public String[] getObjectNames() {
		
		String []result = new String[childObjects.keySet().size()];
		
		return childObjects.keySet().toArray(result);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getPropertyValue(java.lang.String)
	 */
	public ValueCommonInterface getPropertyValue(String propertyName) 
		throws ValueNotFoundException, InvalidArgumentException {
		
		if (!isValidTypeName(propertyName)) {
			throw new InvalidArgumentException("Invalid property name \"" + propertyName + "\"");
		}
		
		if (localProperties.containsKey(propertyName)) {
			return localProperties.get(propertyName);
		} else {
			throw new ValueNotFoundException("Property \"" + propertyName + "\" not found in \""
					+ getName(true) + "\".");
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getPropertyValue(java.lang.String, java.lang.String)
	 */
	public ValueCommonInterface getPropertyValue(String objectPath, String propertyName) 
		throws ValueNotFoundException, InvalidArgumentException {
		
		if (!isValidObjectPath(objectPath)) {
			throw new InvalidArgumentException("Invalid object path \"" + objectPath + "\"");
		}
		
		// return local property if path is empty
		if (objectPath.length() == 0) {
			return getPropertyValue(propertyName);
		}
		
		return getObject(objectPath).getPropertyValue(propertyName);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#setPropertyValue(java.lang.String, cz.cuni.mff.been.hostmanager.value.ValueBasicInterface)
	 */
	public ValueCommonInterface setPropertyValue(String propertyName, ValueCommonInterface newValue)
		throws ValueNotFoundException, InvalidArgumentException {

		if (hasProperty(propertyName)) {
			modify();
			
			return localProperties.put(propertyName, newValue);
		} else {
			throw new ValueNotFoundException("Property \"" + propertyName + "\" not found in \""
					+ getName(true) + "\".");
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#setPropertyValue(cz.cuni.mff.been.hostmanager.database.NameValuePair)
	 */
	public ValueCommonInterface setPropertyValue(NameValuePair property) 
		throws ValueNotFoundException, InvalidArgumentException {
		
		return setPropertyValue(property.getName(), property.getValue());
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#addProperty(java.lang.String, cz.cuni.mff.been.hostmanager.value.ValueBasicInterface)
	 */
	public void addProperty(String propertyName, ValueCommonInterface propValue) 
		throws InvalidArgumentException {
		
		if (!isValidTypeName(propertyName)) {
			throw new InvalidArgumentException("Invalid property name \"" + propertyName + "\".");
		}
		
		if (hasProperty(propertyName)) {
			throw new InvalidArgumentException("Property \"" + propertyName + "\" already exists.");
		}
		
		localProperties.put(propertyName, propValue);
		
		modify();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#addProperty(NameValuePair)
	 */
	public void addProperty(NameValuePair property) throws InvalidArgumentException {
		
		addProperty(property.getName(), property.getValue());
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#putProperty(String, ValueCommonInterface)
	 */
	public ValueCommonInterface putProperty(String propertyName, ValueCommonInterface value) 
		throws InvalidArgumentException {
		
		if (!isValidTypeName(propertyName)) {
			throw new InvalidArgumentException("Invalid property name \"" + propertyName + "\".");
		}
		
		modify();
		
		return localProperties.put(propertyName, value);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#putProperty(NameValuePair)
	 */
	public ValueCommonInterface putProperty(NameValuePair property) throws InvalidArgumentException {
		
		return putProperty(property.getName(), property.getValue());
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#removeProperty(java.lang.String)
	 */
	public void removeProperty(String propertyName) 
		throws ValueNotFoundException, InvalidArgumentException {
		
		if (hasProperty(propertyName)) {
			localProperties.remove(propertyName);
			modify();
		} else {
			throw new ValueNotFoundException("Property \"" + propertyName + "\" not found.");
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#hasProperty(java.lang.String)
	 */
	public boolean hasProperty(String propertyName) throws InvalidArgumentException {
		
		if (!isValidTypeName(propertyName)) {
			throw new InvalidArgumentException("Property name \"" + propertyName + "\" is invalid.");
		}
		
		return localProperties.containsKey(propertyName);
	}

	/**
	 * Get name of the object from given string.
	 * 
	 * NOTE: this method does not check for validity of the input.
	 * 
	 * @param s String with object path.
	 * 
	 * @return Name of the first object on the path. Name will include index number.
	 */
	private String getObjectNameFromString(String s) {
		
		int dotPos = s.indexOf('.');
		
		if (dotPos < 0) {
			return s;
		} else {
			return s.substring(0, dotPos);
		}
	}
	
	/**
	 * Get type name of the object from string.
	 * 
	 * NOTE: this method does not check for validity of the input.
	 *
	 * @param s String with object path.
	 * 
	 * @return Type name of the first object on the path.
	 */
	private String getTypeNameFromString(String s) {

		String obj = getObjectNameFromString(s);
		
		int parPos = obj.indexOf('(');
		
		if (parPos < 0) {
			return obj;
		} else {
			return obj.substring(0, parPos);
		}
	}
	
	/**
	 * Get string containing index of the object.
	 * 
	 * NOTE: this method does not check for validity of the input.
	 * 
	 * @param s String with object path.
	 * @return String containing index of the object. It can be either empty string for unnumbered
	 *         objects, "?" for wild cards or string containing integer for numbered objects.
	 */
	private String getIndexStringFromString(String s) {
		
		String obj = getObjectNameFromString(s);
		
		int lparPos = obj.indexOf('(');
		
		if (lparPos < 0) {
			return null;
		} else {
			int rparPos = obj.indexOf(')');
			
			String indexString = obj.substring(lparPos + 1, rparPos);
			
			return indexString;
		}
	}
	
	/**
	 * Convert string to integer.
	 * 
	 * NOTE: this method does not check for validity of the input.
	 * 
	 * @param s String with integer.
	 * 
	 * @return Integer read from string.
	 */
	private int getIndexNumberFromString(String s) {
		
		if ((s == null) || (s.length() == 0)) {
			return 0;
		}
		
		try {
			return Integer.valueOf(s).intValue();
		} catch (Exception e) {
			// this should never happen, since we assume correct format
			assert false : "Oh man, you forgot to check syntax of the object path.";
			return 0;
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getObject(java.lang.String)
	 */
	public PropertyTreeInterface getObject(String name) 
		throws ValueNotFoundException, InvalidArgumentException {

		if (!isValidObjectPath(name)) {
			throw new InvalidArgumentException("Invalid object path \"" + name + "\".");
		}
		
		int dotPos = name.indexOf('.');
		
		if (dotPos < 0) {
			String typeName = getTypeNameFromString(name);
			String indexStr = getIndexStringFromString(name);
			
			if ((indexStr != null) && indexStr.equals("?")) {
				throw new InvalidArgumentException("Invalid object path \"" + name + "\". "
						+ "Do not use ? to query for specific object.");
			}
			
			int index = getIndexNumberFromString(indexStr);
			
			ArrayList< PropertyTreeInterface > al = childObjects.get(typeName);
			
			if (al == null) {
				throw new ValueNotFoundException("Unable to find object of type \"" + typeName + "\".");
			}
			
			if (index > al.size() - 1) {
				throw new ValueNotFoundException("Unable to find requested object. Index is too big.");
			}
			
			try {
				return al.get(index);
			} catch (Exception e) {
				assert false : "This should never happen.";
				return null;
			}
		}
		
		// Object is deeper in hierarchy
		PropertyTreeInterface localObject = getObject(getObjectNameFromString(name));
			
		return localObject.getObject(name.substring(dotPos + 1));
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getObject(java.lang.String, int)
	 */
	public PropertyTreeInterface getObject(String typeName, int index)
		throws ValueNotFoundException, InvalidArgumentException {
		
		if (!isValidTypeName(typeName)) {
			throw new InvalidArgumentException("Invalid object type name \"" + typeName + "\".");
		}
		
		ArrayList< PropertyTreeInterface > al = childObjects.get(typeName);
		
		if (al == null) {
			throw new ValueNotFoundException("No objects of type \"" + typeName + "\" found.");
		}
		
		if ((index > al.size() - 1) || (index < 0)) {
			throw new ValueNotFoundException("Unable to find requested object. Invalid index.");
		}
	
		try {
			return al.get(index);
		} catch (Exception e) {
			assert false : "This should never happen.";
			return null;
		}
	}

	/**
	 * Set index of the current object.
	 * 
	 * @param index New index of the object.
	 */
	private void setIndex(int index) {
		
		modify();
		
		objectIndex = index;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#addObject(cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface)
	 */
	public void addObject(PropertyTreeInterface object) {

		if (!childObjects.containsKey(object.getTypeName())) {
			ArrayList< PropertyTreeInterface > al = new ArrayList< PropertyTreeInterface >();
			al.add(object);
			childObjects.put(object.getTypeName(), al);
			((PropertyTree) object).setIndex(0);
			((PropertyTree) object).setParent(this);
		} else {
			childObjects.get(object.getTypeName()).add(object);
			((PropertyTree) object).setIndex(childObjects.get(object.getTypeName()).size() - 1);
			((PropertyTree) object).setParent(this);
		}
		
		modify();
	}

	/**
	 * Test if given string contains name of the local property/object. Name is not checked for 
	 * syntax errors.
	 * 
	 * @param s String to test.
	 * 
	 * @return <code>true</code> if string contains local property/object name, <code>false</code>
	 *         otherwise.
	 */
	private boolean isLocalName(String s) {
		
		return s.indexOf('.') == -1;
	}
	
	/**
	 * Test if string contains wild card character ("?"). String is not checked for syntax errors.
	 * 
	 * @param s String to test.
	 * 
	 * @return <code>true</code> if string contains question mark, <code>false</code> otherwise.
	 */
	private boolean isWildCardName(String s) {
		
		return s.contains("?");
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#removeObject(java.lang.String)
	 */
	public void removeObject(String name) throws ValueNotFoundException, InvalidArgumentException {
		
		if (!isLocalName(name)) {
			throw new InvalidArgumentException("Unable to delete object which is not direct child of this.");
		}

		String typeName = getTypeNameFromString(name);
		
		if (!childObjects.containsKey(typeName)) {
			throw new ValueNotFoundException("Unable to delete \"" + name + "\". "
					+ "No objects of such type found.");
		}
		
		String indexString = getIndexStringFromString(name);
		
		if (isWildCardName(name)) {
			throw new InvalidArgumentException("Unable to delete object with wildcard in name.");
		}

		int index = getIndexNumberFromString(indexString);
		ArrayList< PropertyTreeInterface > al = childObjects.get(typeName);
		
		if ((index < 0) || (index > al.size() - 1)) {
			throw new ValueNotFoundException("Unable to delete \"" + name + "\". Invalid index.");
		}
		
		al.remove(index);
		
		if (al.size() == 0) {
			childObjects.remove(typeName);
		} else {
			for (int i = index; i < al.size(); ++i) {
				((PropertyTree) al.get(i)).setIndex(i);
			}
		}
		
		modify();
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#removeObjects(java.lang.String, int)
	 */
	public void removeObject(String typeName, int index) 
		throws ValueNotFoundException, InvalidArgumentException {
		
		if (!isValidTypeName(typeName)) {
			throw new InvalidArgumentException("Invalid type name \"" + typeName + "\".");
		}
		
		if (!childObjects.containsKey(typeName)) {
			throw new ValueNotFoundException("No objects of type \"" + typeName + "\" found.");
		}
		
		ArrayList< PropertyTreeInterface > list = childObjects.get(typeName);
		
		if ((index < 0) || (index > list.size() - 1)) {
			throw new ValueNotFoundException("Unable to delete object. Invalid index.");
		}
		
		((PropertyTree) list.get(index)).setParent(null);
		list.remove(index);
		
		if (list.size() == 0) {
			childObjects.remove(typeName);
		} else {
			for (int i = index; i < list.size(); ++i) {
				((PropertyTree) list.get(i)).setIndex(i);
			}
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#removeAllOfType(java.lang.String)
	 */
	public void removeAllOfType(String type) throws ValueNotFoundException, InvalidArgumentException {
		
		if (!isValidTypeName(type)) {
			throw new InvalidArgumentException("Invalid type name \"" + type + "\".");
		}
		
		if (childObjects.containsKey(type)) {
			ArrayList< PropertyTreeInterface > o = childObjects.get(type);
			
			for (PropertyTreeInterface pt: o) {
				PropertyTree x = (PropertyTree) pt;
				x.setParent(null);
				x.setIndex(-1);
			}
			
			childObjects.remove(type);
			
			modify();
		} else {
			throw new ValueNotFoundException("No object of type \"" + type + "\" found.");
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getName(boolean)
	 */
	public String getName(boolean absolute) {
		
		String local = objectName;
		
		if (objectIndex >= 0) {
			boolean single = false;
			if (parent != null) {
				try {
					single = parent.getObjectCount(objectName) == 1;
				} catch (Exception e) {
					// this should never happen, but life's bitch...
					single = false;
				}
			} else {
				single = true;
			}
			local = local + (single ? "" : "(" + String.valueOf(objectIndex) + ")");
		}
		
		if (absolute) {
			if (parent == null) {
				return local;
			}
			return parent.getName(true) + "." + local;
		}
		
		return local;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getTypeName()
	 */
	public String getTypeName() {
		
		return objectName;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getIndex()
	 */
	public int getIndex() {
		
		return objectIndex;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getObjectCount()
	 */
	public int getObjectCount() {
		
		int result = 0;

		for (ArrayList< PropertyTreeInterface > current: childObjects.values()) {
			result += current.size();
		}
		
		return result;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getObjectCount(java.lang.String)
	 */
	public int getObjectCount(String name) throws InvalidArgumentException {
		
		if (!isValidTypeName(name)) {
			throw new InvalidArgumentException("Invalid typename \"" + name + "\".");
		}
		
		ArrayList< PropertyTreeInterface > p = childObjects.get(name);
		
		if (p == null) {
			return 0;
		} else {
			return p.size();
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getPropertyCount()
	 */
	public int getPropertyCount() {
		
		return localProperties.size();
	}
	
	/**
	 * Find all objects with given type name which satisfy given conditions. Search is performed recursively
	 * in all child nodes starting from current node (depth-first search).
	 * 
	 * @param objTypeName Type name of objects to find.
	 * @param fixed List of <code>NameValuePair</code> objects which specify conditions. If <code>null</code>
	 *        all objects of given type will be considered.
	 * 
	 * @return List with objects which satisfy given conditions. Empty list, if no such object is found.
	 * 
	 * @throws InvalidArgumentException If type name has invalid syntax.
	 */
	protected List< PropertyTreeInterface > findAllOfType(String objTypeName, List< NameValuePair > fixed) 
		throws InvalidArgumentException {
		
		if (!isValidTypeName(objTypeName)) {
			throw new InvalidArgumentException("Invalid type name \"" + objTypeName + "\".");
		}
		
		ArrayList< PropertyTreeInterface > result = new ArrayList< PropertyTreeInterface >();
		
		if (objTypeName.equals(objectName)) {
			
			boolean addThis = true;
			
			if (fixed != null) {
				for (NameValuePair current: fixed) {
					try {
						if (!criteriaTest(getPropertyValue(current.getName()), current.getValue())) {
							addThis = false;
							break;
						}
					} catch (Exception e) {
						addThis = false;
						break;
					}
				}
			}
			
			if (addThis) {
				result.add(this);
			}
		} else {
			for ( PropertyTreeReadInterface object : getObjects() ) {
				PropertyTree current = (PropertyTree) object;
				
				result.addAll(current.findAllOfType(objTypeName, fixed));
			}
		}
		
		return result;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#test(RestrictionInterface, boolean)
	 */
	public boolean test(RestrictionInterface restriction, boolean ignoreMissing)
		throws ValueNotFoundException, ValueTypeIncorrectException, HostManagerException {

		boolean result = false;

		if (restriction instanceof ObjectRestriction) {
			try {
				result = testSingle((ObjectRestriction) restriction, ignoreMissing);
			} catch (ValueNotFoundException e) {
				if (!ignoreMissing) {
					throw e;
				}
			}
		} else if (restriction instanceof AlternativeRestriction) {
			try {
				result = testSingle((AlternativeRestriction) restriction, ignoreMissing);
			} catch (ValueNotFoundException e) {
				if (!ignoreMissing) {
					throw e;
				}
			}
		} else if (restriction instanceof RSLRestriction) {
			result = ((RSLRestriction) restriction).test(this, ignoreMissing);
		} else {
			throw new HostManagerException("Unknown restriction type \""
					+ restriction.getClass().getCanonicalName() + "\".");
		}
	
		return result;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#test(cz.cuni.mff.been.hostmanager.database.RestrictionInterface[], boolean)
	 */
	public boolean test(RestrictionInterface []restrictions, boolean ignoreMissing)
		throws ValueNotFoundException, ValueTypeIncorrectException, HostManagerException {
		
		for (RestrictionInterface r: restrictions) {
			
			if (!test(r, ignoreMissing)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Test object against one restriction.
	 * 
	 * @param restriction Restriction.
	 * @param ignoreMissing If <code>true</code> missing objects or properties do not throw 
	 *        <code>ValueNotFoundException</code>.
	 * @return <code>true</code> if object satisfies given restriction, <code>false</code> otherwise.
	 * 
	 * @throws ValueNotFoundException If some object or property from criterion does not exist.
	 * @throws ValueTypeIncorrectException If condition for given property has type incompatible with
	 *         value type of property.
	 * @throws HostManagerException Other error occurred.
	 */
	protected boolean testSingle(ObjectRestriction restriction, boolean ignoreMissing)
		throws ValueNotFoundException, ValueTypeIncorrectException, HostManagerException {

		if (restriction.getObjectPath().length() == 0) {
			// Object path is empty, test properties of this object
			
			for (NameValuePair current: restriction.getProperties()) {
				try {
					if (!criteriaTest(getPropertyValue(current.getName()), current.getValue())) {
						return false;
					}
				} catch (ValueNotFoundException e) {
					if (ignoreMissing) {
						return false;
					} else {
						throw e;
					}
				} catch (Exception e) {
					return false;
				}
			}
			
			return true;
		} else {
			
			String objName = getObjectNameFromString(restriction.getObjectPath());
			String pathPostfix = "";
			
			if (restriction.getObjectPath().indexOf('.') != -1) {
				restriction.getObjectPath().substring(objName.length());
			}

			// build restriction used for all child objects
			ObjectRestriction childRestriction = 
				new ObjectRestriction(pathPostfix, restriction.getProperties());

			if (objName.indexOf('?') != -1) {
				// search for object in the list of all objects of given type.
				
				String objTypeName = getTypeNameFromString(restriction.getObjectPath());
				
				if (!childObjects.containsKey(objTypeName)) {
					if (ignoreMissing) {
						return false;
					} else {
						throw new ValueNotFoundException("Unable to find object of type \""
								+ objTypeName + "\".");
					}
				}

				for (PropertyTreeInterface current: childObjects.get(objTypeName)) {
					PropertyTree c = (PropertyTree) current;
					
					try {
						if (c.testSingle(childRestriction, ignoreMissing)) {
							// we have found matching child object, no need to test further objects
							return true;
						}
					} catch (ValueNotFoundException e) {
						// this is not necessary, but resulting exception is nicer :)
						throw new ValueNotFoundException("Unable to find child object of \""
								+ objTypeName + "\".", e);
					}
				}
				
				// we didn't find any matching object, what a shame
				return false;
			} else {
				// exact path, test one object only

				try {
					return ((PropertyTree) getObject(objName)).testSingle(childRestriction, ignoreMissing);
				} catch (ValueNotFoundException e) {
					// again, make exception more informative (hopefuly)
					throw new ValueNotFoundException("Unable to find child object of \""
							+ objName + "\".", e);
				}
			}
		}
	}
	
	/**
	 * Test for alternative restrictions.
	 * 
	 * @param alt Alternative restriction.
	 * @param ignoreMissing If <code>true</code> missing objects or properties do not throw 
	 *        <code>ValueNotFoundException</code>.
	 * @return <code>true</code> if object satisfies given restriction, <code>false</code> otherwise.
	 * 
	 * @throws ValueNotFoundException If some object or property from criterion does not exist.
	 * @throws ValueTypeIncorrectException If condition for given property has type incompatible with
	 *         value type of property.
	 * @throws HostManagerException Other error occurred.
	 */
	protected boolean testSingle(AlternativeRestriction alt, boolean ignoreMissing) 
		throws ValueTypeIncorrectException, ValueNotFoundException, HostManagerException {
		
		ObjectRestriction []restrictions = alt.getRestrictions();
		boolean result = false;
		
		// test all restrictions in list
		for (int i = 0; i < restrictions.length; ++i) {
			
			result = result | testSingle(restrictions[i], ignoreMissing);
		}
		
		return result;
	}
	
	/**
	 * Test value against condition.
	 * 
	 * @param value Value to be tested.
	 * @param criteria Condition specified using sub-type of <code>ValueCommonInterface</code>.
	 * @return <code>true</code> if value satisfies given condition, <code>false</code> otherwise.<br>
	 *         Testing algorithm depends on type of condition. If condition is list or range, value must
	 *         be inside to pass. If condition is regexp, value.toString() must match given regexp.
	 *         If type of value is same as type of condition their values must be equal. Otherwise values are
	 *         considered incompatible and exception is thrown.
	 * 
	 * @throws ValueTypeIncorrectException If type of criteria is not compatible with type of value.
	 * @throws HostManagerException If other error occurred (unknown type...).
	 */
	@SuppressWarnings("unchecked")
	protected static boolean criteriaTest(ValueCommonInterface value, ValueCommonInterface criteria) 
		throws ValueTypeIncorrectException, HostManagerException {
		
		if (criteria instanceof ValueList) {
			if (value instanceof ValueList) {
				return ((ValueList< ? >) criteria).equals(value);
			} else if (value instanceof ValueBasicInterface) {
				return ((ValueList) criteria).contains((ValueBasicInterface< ? >) value);
			} else {
				throw new ValueTypeIncorrectException("Unable to compare \""
						+ criteria.getClass().getCanonicalName() + "\" with \""
						+ value.getClass().getCanonicalName() + "\".");
			}
		} else if (criteria instanceof ValueRange) {
			if (value instanceof ValueRange) {
				return ((ValueRange< ? >) criteria).equals((ValueRange< ? >) value);
			} else if (value instanceof ValueBasicInterface) {
				return ((ValueRange) criteria).contains((ValueBasicInterface< ? >) value);
			} else {
				throw new ValueTypeIncorrectException("Unable to compare \""
						+ criteria.getClass().getCanonicalName() + "\" with \""
						+ value.getClass().getCanonicalName() + "\".");
			}
		} else if (criteria instanceof ValueRegexp) {
			if (value instanceof ValueRegexp) {
				return ((ValueRegexp) criteria).equals((ValueRegexp) value);
			} else if (value instanceof ValueBasicInterface) {
				return ((ValueRegexp) criteria).match(value.toString());
			} else {
				throw new ValueTypeIncorrectException("Unable to compare \""
						+ criteria.getClass().getCanonicalName() + "\" with \""
						+ value.getClass().getCanonicalName() + "\".");
			}
		} else if (criteria instanceof ValueBasicInterface) {
			return criteria.equals(value);
		} else {
			throw new HostManagerException("Unknown criteria type \""
					+ criteria.getClass().getCanonicalName() + "\".");
		}
	}
	
	/**
	 * Set parent of current object.
	 * 
	 * @param newParent Parent object. <code>null</code> for root node.
	 */
	protected void setParent(PropertyTreeInterface newParent) {
		
		parent = newParent;
		
		modify();
	}
	
	/**
	 * Get parent object.
	 * 
	 * @return Parent object or <code>null</code> current object if root node.
	 */
	protected PropertyTreeInterface getParent() {
		
		return parent;
	}
	
	/**
	 * Get root node.
	 * 
	 * @return Root node of tree current objects is in.
	 */
	protected PropertyTreeInterface getRoot() {
		
		if (parent == null) {
			return this;
		} else {
			return ((PropertyTree) parent).getRoot();
		}
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return getName(false);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#isValidObjectPath(java.lang.String)
	 */
	public boolean isValidObjectPath(String path) {

		return PropertyTreeFactory.isValidObjectPath(path);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#isValidPropertyPath(java.lang.String)
	 */
	public boolean isValidPropertyPath(String path) {
		
		return PropertyTreeFactory.isValidPropertyPath(path);
		
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#isValidTypeName(java.lang.String)
	 */
	public boolean isValidTypeName(String tname) {
		
		return PropertyTreeFactory.isValidTypeName(tname);
	}
	
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return rehash();
	}
	
	/**
	 * Two <code>PropertyTree</code> objects are considered equal if both have same name, both must
	 * contain same child objects (order is important too) and both must have same properties (order
	 * of properties is not important).
	 * 
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof PropertyTree) {
			PropertyTree p = (PropertyTree) o;
			
			if (!this.objectName.equals(p.objectName)
				|| this.getObjectCount() != p.getObjectCount()
				|| this.getPropertyCount() != p.getPropertyCount()) {
				
				return false;
			}
			
			// well, we have to dig deeper to find differences
			
			// test properties
			for ( NameValuePair left : getProperties() ) {
				ValueCommonInterface right = null;
				
				try {
					right = p.getPropertyValue(left.getName());
				} catch (Exception e) {
					return false;
				}
				
				if (!left.getValue().equals(right)) {
					return false;
				}
			}
			
			// test objects
			for (Iterator< String > it = childObjects.keySet().iterator(); it.hasNext(); ) {
				String currentTypeName = it.next();
				
				ArrayList< PropertyTreeInterface > left = childObjects.get(currentTypeName);
				ArrayList< PropertyTreeInterface > right = p.childObjects.get(currentTypeName);
				
				if (!left.equals(right)) {
					return false;
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getObjectIterator()
	 */
	public Iterable< PropertyTreeReadInterface > getObjects() {
		final ObjectIterable objectIterable = new ObjectIterable();
		
		return objectIterable;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface#getPropertyIterator()
	 */
	public Iterable< NameValuePair > getProperties() {
		final PropertyIterable propertyIterable = new PropertyIterable();
		
		return propertyIterable;
	}

	/**
	 * Increase modification count flag.
	 */
	private void modify() {
		
		modificationCount += 1;
	}
	
	/**
	 * Reset modification flags.
	 */
	private void reset() {
		
		resetModCount = modificationCount;
	}
	
	/**
	 * Recalculate hash code of this instance when needed.
	 * 
	 * @return New hash code of this instance.
	 */
	private int rehash() {
		
		// do we need to update hash code cache?
		if (hashCacheModCount != modificationCount) {
			hashCache = 19840627; // <- yes, I'm that mean...
			
			hashCache += getName(false).hashCode() * 31;
			
			// just simple addition, since order of the elements is not important in the comparison
			for ( PropertyTreeReadInterface object : getObjects() ) {
				hashCache += object.hashCode();
			}
			
			for ( PropertyTreeReadInterface object : getObjects() ) {
				hashCache += object.hashCode();
			}
			
			hashCacheModCount = modificationCount;
		}
		
		return hashCache;
	}
	
	/**
	 * This iterator class provides access to all child objects of the current object.
	 * Objects are iterated by their type name (alphabetically) and objects of the same type
	 * are iterated by index. Note that iterators are read-only.
	 *
	 * @author Branislav Repcek
	 * @author Andrej Podzimek
	 */
	private class ObjectIterable implements Iterable< PropertyTreeReadInterface > { 
		
		/** {@inheritDoc} */
		public Iterator< PropertyTreeReadInterface > iterator() {		
			return new Iterator< PropertyTreeReadInterface >() {
		
				/** Iterator through the lists of properties. */
				private final Iterator< ArrayList< PropertyTreeInterface > > typeIterator;
				
				/** Iterator through the list of properties of the given type. */
				private Iterator< ? extends PropertyTreeReadInterface > propertyIterator;
				
				{
					typeIterator = childObjects.values().iterator();								// Sorted by keys!
					propertyIterator = typeIterator.hasNext() ?
						typeIterator.next().iterator()
						: new Iterator< PropertyTreeReadInterface >() {
							
							@Override
							public boolean hasNext() {
								return false;
							}
							
							@Override
							public PropertyTreeReadInterface next() {
								throw new NoSuchElementException(
									"next() called on dummy empty iterator."
								);
							}
							
							@Override
							public void remove() {}													// Never invoked.
						};
				}
				
				/** {@inheritDoc} */
				public boolean hasNext() {
					if ( propertyIterator.hasNext() ) {
						return true;
					} else {
						while ( typeIterator.hasNext() ) {
							propertyIterator = typeIterator.next().iterator();
							if ( propertyIterator.hasNext() ) {
								return true;
							}
						}
						return false;
					}
				}
		
				/** {@inheritDoc} */
				public PropertyTreeReadInterface next() {
					return propertyIterator.next();
				}
		
				/** {@inheritDoc} */
				public void remove() {
					throw new UnsupportedOperationException(
						"remove() is not supported for ObjectIterable."
					);
				}
			};
		}
	}
	
	/**
	 * This iterator class provides access to all properties of the current object. Iterator is 
	 * read-only;
	 *
	 * @author Branislav Repcek
	 * @author Andrej Podzimek
	 */
	private class PropertyIterable implements Iterable< NameValuePair > {
		
		/** {@inheritDoc} */
		public Iterator< NameValuePair > iterator() {				
			return new Iterator< NameValuePair >() {

				/** Iterator into property array. */
				private final Iterator< Entry< String, ValueCommonInterface > > it;
				
				{
					it = localProperties.entrySet().iterator();
				}
				
				/** {@inheritDoc} */
				public boolean hasNext() {			
					return it.hasNext();
				}
		
				/** {@inheritDoc} */
				public NameValuePair next() {
					Entry< String, ValueCommonInterface > current;
					
					current = it.next();
					return new NameValuePair( current.getKey(), current.getValue() );
				}
		
				/** {@inheritDoc} */		
				public void remove() {
		
					throw new UnsupportedOperationException(
						"remove() is not supported for PropertyIterable.");
				}
			};
		}
	}
}
