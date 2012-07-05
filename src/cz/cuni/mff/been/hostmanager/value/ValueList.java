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

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.InputParseException;

import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;

import cz.cuni.mff.been.hostmanager.util.XMLHelper;

/**
 * Class which represents list of values of the same type. <code>ValueList</code> is limited only 
 * to members of simple types (classes which implement <code>ValueBasicInterface</code> interface).
 *
 * @param <T> Type of the elements of the list. This type has to extend ValueBasicInterface< T >.
 *
 * @author Branislav Repcek
 */
public class ValueList< T extends ValueBasicInterface< T > > 
	implements ValueCompoundInterface< T >, Serializable, XMLSerializableInterface, Iterable< T > {
	
	private static final long	serialVersionUID	= -6079670814175099882L;

	/** Type of values this list will store. */
	private ValueType elementType;
	
	/**
	 * List of values.
	 */
	private ArrayList< T > value;
	
	/**
	 * Allocate {@code ValueList} which represents an empty list. This constructor
	 * is dangerous and deprecated, since it doesn't set the element value type properly.
	 * This constructor must be immediately followed by a call to {@code parseXmlNode()}
	 * that will set the instance into a valid state. This constructor exists only to make
	 * reflection tricks work. It is not meant to be invoked directly!
	 */
	@Deprecated
	public ValueList() {
		
		this((ValueType) null);
	}
	
	/**
	 * Allocate empty <code>ValueList</code> value.
	 */
	public ValueList(ValueType elementType) {

		this.elementType = elementType;
		this.value = new ArrayList< T >();
	}
	
	/**
	 * Create new list form array of objects implementing <code>ValueBasicInterface</code>. 
	 * Elements are stored in same order as they appear in <code>newValue</code> array.
	 * 
	 * @param newValue List of objects.
	 */
	public ValueList(T[] newValue, ValueType elementType) {
	
		this.elementType = elementType;

		if (null == newValue) {
			this.value = new ArrayList< T >();
			return;
		} else {
			this.value = new ArrayList< T >(newValue.length);
		}
		
		for (T x: newValue) {
			this.value.add(x);
		}
	}

	/**
	 * Create new list containing all members of given collection.
	 * 
	 * @param values Collection containing values that will be included in the list.
	 */
	public ValueList( Collection< T > values, ValueType elementType ) {
		
		this.elementType = elementType;
		
		if (null == values) {
			this.value = new ArrayList< T >();
			return;
		} else {
			this.value = new ArrayList< T >(values.size());
		}
		
		for (T x: values) {
			value.add(x);
		}
	}
	
	/**
	 * Create new ValueList and read data from XML node.
	 * 
	 * @param node Node containing list data.
	 * 
	 * @throws InputParseException If there was an error while parsing XML data.
	 */
	public ValueList(Node node) throws InputParseException {
		
		this.value = new ArrayList< T >();
		parseXMLNode(node);
	}
	
	/*
	 * @see java.lang.Object#toString
	 */
	@Override
	public String toString() {
		
		String result = "{";
		
		for (int i = 0; i < length(); i++) {
			
			result += value.get(i).toString();
			if (i < length() - 1) {
				result += ',';
			}
		}
		
		return result + "}";
	}
	
	/**
	 * Get number of elements stored in list.
	 * 
	 * @return Number of elements stored in list.
	 */
	public int length() {
		
		return value.size();
	}

	/**
	 * Get element at given position.
	 * 
	 * @param index Position of element to get.
	 * @return Element at given position.
	 * @throws IndexOutOfBoundsException If (index < 0) || (index > length()).
	 */
	public T get(int index) throws IndexOutOfBoundsException {
		
		return value.get(index);
	}
	
	/**
	 * Set element at given position.
	 * 
	 * @param index Index of element to set.
	 * @param val New value.
	 * @throws IndexOutOfBoundsException If (index < 0) || (index > length()).
	 */
	public void set(int index, T val) throws IndexOutOfBoundsException {
		
		value.set(index, val);
	}
	
	/**
	 * Add element to the end of the list.
	 * 
	 * @param val New element to add to the list.
	 */
	public void add(T val) {

		value.add(val);
	}

	/**
	 * Add all elements from specified list. Duplicate values are not removed. Order of elements 
	 * is not changed.
	 * 
	 * @param list List of values to add to the current list.
	 */
	public void addAll(ValueList< T > list) {

		value.addAll(list.value);
	}
	
	/**
	 * Add all elements from the specified collection to the current list. Duplicate entries are  
	 * allowed. Order of elements is not changed.
	 * 
	 * @param list Collection of elements to add to current list.
	 */
	public void addAll(Collection< T > list) {
		
		value.addAll(list);
	}
	
	/**
	 * Remove element with given value from the list.
	 * 
	 * @param val Value to remove.
	 * @return <code>true</code> if specified value was present in the list, <code>false</code> otherwise.
	 */
	public boolean remove(T val) {
		
		return value.remove(val);
	}
	
	/**
	 * Remove value at given position in the list.
	 * 
	 * @param index Index of the value to remove.
	 * @return Value which is being removed.
	 * 
	 * @throws IndexOutOfBoundsException If (index < 0) || (index > length()).
	 */
	public T remove(int index) throws IndexOutOfBoundsException {
		
		return value.remove(index);
	}
	
	/**
	 * Test whether list contains any elements.
	 * 
	 * @return <code>true</code> if list is empty, <code>false</code> otherwise.
	 */
	public boolean empty() {
		
		return value.isEmpty();
	}

	/**
	 * Test whether list contains given element.
	 * 
	 * @param vb Element to test.
	 * @return <code>true</code> if element has been found in list, <code>false</code> otherwise.
	 */
	public boolean contains(T vb) {
		for (T i : value) {
			if (i.equals( vb )) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof ValueList< ? >) {
			return equals((ValueList< ? >)o);
		} else {
			return false;
		}
	}
	
	/**
	 * Compare specified <code>ValueList</code> with this value for equality. Comparison is based only 
	 * on elements contained within respective lists, it does not matter in which order elements are stored.
	 * 
	 * @param vc Value to compare this to.
	 * @return <code>true</code> if values are equal, <code>false</code> otherwise.
	 */
	public boolean equals(ValueList< ? > vc) {
		
		if (length() == vc.length()) {

			Iterator< T > it1 = value.iterator();
			Iterator< ? > it2 = vc.value.iterator();
			
			for (; it1.hasNext(); ) {
				if (!it1.next().equals(it2.next())) {
					return false;
				}
			}
			
			return true;
		} else {
			return false;
		}
	}

	/*
	 * @see java.lang.Object#hashCode
	 */
	@Override
	public int hashCode() {
		
		return value.hashCode();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 */
	@SuppressWarnings("unchecked")
	public void parseXMLNode(Node node) throws InputParseException {
		
		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException("Node does not contain list data. Node name is \""
					+ node.getNodeName() + "\".");
		}
		
		elementType = ValueType.forName(XMLHelper.getAttributeValueByName("subtype", node));

		if (null == elementType) {
			return;
		}
		
		Class< ? > subtypeClass = null;

		try {
			subtypeClass = Class.forName(elementType.NAME);
		} catch (Exception e) {
			throw new InputParseException("Error creating instance of the list element.", e);
		}
		
		ArrayList< Node > subnodes = null;
		
		try {
			subnodes = XMLHelper.getChildNodesByName(((T) subtypeClass.newInstance()).getXMLNodeName(), node);
		} catch (Exception e) {
			throw new InputParseException("Unable to create instance of the list element.", e);
		}

		for (Node current: subnodes) {
			T vbi = null;
			
			try {
				vbi = (T) subtypeClass.newInstance();
			} catch (Exception e) {
				throw new InputParseException("Error creating list items.", e);
			}
			
			vbi.parseXMLNode(current);
			
			value.add(vbi);
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement
	 */
	public Element exportAsElement(Document document) {
		
		/* Resulting node
		 * 
		 * <list subtype="<subtype-name>">
		 *    <element-node 0>
		 *          .
		 *          .
		 *          .
		 *    <element-node n-1>
		 * </list>
		 * 
		 * where <subtype-name> is canonical name of the type of elements in the list or (none) if
		 * list  is empty, <element-node 0> to <element-node n-1> are serialised items from the list.
		 * These nodes will not be present if the list is empty.
		 */
		
		Element element = document.createElement(getXMLNodeName());

		element.setAttribute("subtype", elementType == null ? "(none)" : elementType.NAME);
		
		if (value.size() > 0) {
			
			for (T current: value) {
				element.appendChild(current.exportAsElement(document));
			}
		}

		return element;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 */
	public String getXMLNodeName() {
		
		return "list";
	}
	
	/*
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator< T > iterator() {
		
		return value.iterator();
	}
	
	/**
	 * @return List containing all elements from this list.
	 */
	public List< T > getValue() {
		
		return value;
	}

	@Override
	public ValueType getElementType() {
		
		return elementType;
	}

	@Override
	public ValueType getType() {
		
		return ValueType.LIST;
	}
}
