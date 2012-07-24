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

package cz.cuni.mff.been.hostmanager.database;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.cuni.mff.been.common.Version;
import cz.cuni.mff.been.common.rsl.ArrayProperty;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.ContainerProperty;
import cz.cuni.mff.been.common.rsl.InvalidOperatorException;
import cz.cuni.mff.been.common.rsl.InvalidPropertyException;
import cz.cuni.mff.been.common.rsl.InvalidValueTypeException;
import cz.cuni.mff.been.common.rsl.InvalidValueUnitException;
import cz.cuni.mff.been.common.rsl.LongWithUnit;
import cz.cuni.mff.been.common.rsl.ParseException;
import cz.cuni.mff.been.common.rsl.ParserWrapper;
import cz.cuni.mff.been.common.rsl.Property;
import cz.cuni.mff.been.common.rsl.SimpleProperty;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.ValueTypeIncorrectException;
import cz.cuni.mff.been.hostmanager.value.ValueCommonInterface;
import cz.cuni.mff.been.hostmanager.value.ValueInteger;
import cz.cuni.mff.been.hostmanager.value.ValueList;
import cz.cuni.mff.been.hostmanager.value.ValueString;
import cz.cuni.mff.been.hostmanager.value.ValueVersion;

/**
 * Interface which allows you to specify condition as an RSL string.
 *
 * @author Branislav Repcek
 * @author David Majda
 */
public class RSLRestriction implements Serializable,
		XMLSerializableInterface, RestrictionInterface {

	private static final long	serialVersionUID	= 2760551200735502283L;

	/** Name of the node in XML file. */
	public static final String XML_NODE_NAME = "rslRestriction";
	
	/** RSL condition as string. */
	private String rslString;

	/** RSL condition in parsed form. */
	private Condition rslCondition;
	
	/**
	 * Implementation of the <code>SimpleProperty</code> interface. Each instance
	 * corresponds to one Host Manager's property (i.e. leaf of the property tree).
	 * 
	 * @author David Majda
	 */
	private static class HostManagerSimpleProperty implements SimpleProperty {
		/**
		 * Abstract class, whose descendants extract values (in the form of normal
		 * Java classes) from the Host Manager's value classes.
		 * 
		 * In any sane language, this would be simple function pointer/delegate/
		 * /lambda function/closure/whatever.
		 * 
		 * @param <T> extracted class type
		 * 
		 * @author David Majda
		 */
		private abstract static class ValueGetter<T> {
			/**
			 * Extracts value (in the form of normal Java class) from the Host
			 * Manager's value class.
			 * 
			 * @param hostManagerValue Host Manager's value class instance 
			 * @return extracted value
			 */
			public abstract T getValue( ValueCommonInterface hostManagerValue );
		}
		
		/** Maps Host Manager's value classes to normal Java classes. */
		private static Map< Class< ? >, Class< ? > > classMap;
		/**
		 * Maps Host Manager's value classes to classes, which extract values (in
		 * the form of normal Java classes) form them.
		 */  
		private static Map<Class< ? >, ValueGetter< ? > > valueGetterMap;
		/** Host Manager's property corresponding to this class instance. */
		private ValueCommonInterface value;
		
		{
			classMap = new HashMap< Class< ? >, Class< ? > >();
			classMap.put(ValueInteger.class, LongWithUnit.class);
			classMap.put(ValueVersion.class, Version.class);
			classMap.put(ValueString.class, String.class);
			classMap.put(ValueList.class, List.class);
			
			valueGetterMap = new HashMap< Class< ? >, ValueGetter< ? > >();
			valueGetterMap.put(ValueInteger.class, new ValueGetter<LongWithUnit>() {
				@Override
				public LongWithUnit getValue(ValueCommonInterface hostManagerValue) {
					ValueInteger integerValue = (ValueInteger) hostManagerValue;
					return new LongWithUnit(Long.toString(integerValue.longValue()) 
						+ integerValue.getUnit());
				}
			});
			valueGetterMap.put(ValueVersion.class, new ValueGetter<Version>() {
				@Override
				public Version getValue(ValueCommonInterface hostManagerValue) {
					ValueVersion version = (ValueVersion) hostManagerValue; 
					return new Version(version.toString());
				}
			});
			valueGetterMap.put(ValueString.class, new ValueGetter<String>() {
				@Override
				public String getValue(ValueCommonInterface hostManagerValue) {
					return ((ValueString) hostManagerValue).getValue();
				}
			});
			valueGetterMap.put(ValueList.class, new ValueGetter< List< String > >() {
				@SuppressWarnings("unchecked")
				@Override
				public List< String > getValue(ValueCommonInterface hostManagerValue) {
					/* We assume all ValueLists have ValueStrings as items. */
					List< String > result = new LinkedList< String >();
					ValueList< ValueString > valueList = (ValueList<ValueString>) hostManagerValue;
					for (ValueString item: valueList) {
						result.add(item.getValue());
					}
					return result;
				}
			});
		}
		
		/**
		 * Allocates a new <code>HostManagerSimpleProperty</code> object.
		 * 
		 * @param value Host Manager's property corresponding to this class
		 *         instance
		 */
		public HostManagerSimpleProperty(ValueCommonInterface value) {
			this.value = value;
		}

		/**
		 * @see cz.cuni.mff.been.common.rsl.SimpleProperty#getValue()
		 */
		public Object getValue() {
			Object result = valueGetterMap.get(value.getClass()).getValue(value);
			/* Properties with unknown class are treated as Strings. */
			if (result == null) {
				result = value.toString();
			}
			return result;
		}

		/**
		 * @see cz.cuni.mff.been.common.rsl.SimpleProperty#getValueClass()
		 */
		public Class< ? > getValueClass() {
			Class< ? > result = classMap.get(value.getClass());
			/* Properties with unknown class are treated as Strings. */
			if (result == null) {
				result = String.class;
			}
			return result;
		}
	}
	
	/**
	 * Implementation of the <code>ArrayProperty</code> interface. Used in cases
	 * where Host Manager's object has more instances of some sub-object.
	 * 
	 * @author David Majda
	 */
	private static class HostManagerArrayProperty implements ArrayProperty {
		/**
		 * Interface for examination of the Host Manager's object sub-objects.
		 */
		private PropertyTreeReadInterface readInterface;
		/** Subobject's name. */
		private String propertyName;
		
		/**
		 * Allocates a new <code>HostManagerArrayProperty</code> object.
		 * 
		 * @param readInterface interface for examination of the Host Manager's
		 *         object sub-objects
		 * @param propertyName subobject's name
		 */
		public HostManagerArrayProperty(PropertyTreeReadInterface readInterface,
				String propertyName) {
			this.readInterface = readInterface;
			this.propertyName = propertyName;
		}

		/**
		 * @see cz.cuni.mff.been.common.rsl.ArrayProperty#getItems()
		 */
		public ContainerProperty[] getItems() {
			ContainerProperty[] result
				= new ContainerProperty[readInterface.getObjectCount(propertyName)];
			for (int i = 0; i < result.length; i++) {
				result[i] = new HostManagerContainerProperty(readInterface.getObject(propertyName, i));
			}
			return result;
		}
		
	}
	
	/**
	 * Implementation of the <code>ContainerProperty</code> interface. Each
	 * instance corresponds to one Host Manager's object (i.e. inner node of the
	 * property tree).
	 * 
	 * @author David Majda
	 */
	private static class HostManagerContainerProperty implements ContainerProperty {
		/**
		 * Interface for examination of the Host Manager's object sub-objects and
		 * properties.
		 */
		private PropertyTreeReadInterface readInterface;

		/**
		 * Allocates a new <code>HostManagerContainerProperty</code> object.
		 * 
		 * @param readInterface Interface for examination of the Host Manager's
		 *         object sub-objects and properties
		 */
		public HostManagerContainerProperty(PropertyTreeReadInterface readInterface) {
			this.readInterface = readInterface;
		}

		/**
		 * @see cz.cuni.mff.been.common.rsl.ContainerProperty#hasProperty(java.lang.String)
		 */
		public boolean hasProperty(String propertyName) {
			/* Some RSL property names are not legal Host Manager property names -
			 * hasProperty method throws InvalidArgumentException if they are passed
			 * in. We check this and simply consider those properties non-existant.
			 */
			try {
				return readInterface.hasProperty(propertyName)
					|| readInterface.getObjectCount(propertyName) > 0;
			} catch (InvalidArgumentException e) {
				return false;
			}
		}

		/**
		 * @see cz.cuni.mff.been.common.rsl.ContainerProperty#getProperty(java.lang.String)
		 */
		public Property getProperty(String propertyName) {
			if (readInterface.hasProperty(propertyName)) {
				return new HostManagerSimpleProperty(
					readInterface.getPropertyValue(propertyName));
			} else { // readInterface.getObjectCount(propertyName) > 0
				return new HostManagerArrayProperty(readInterface, propertyName);
			}
		}
	}
	
	/** @return the RSL condition */
	public String getRSLString() {
		return rslString;
	}
	
	/**
	 * RSL Condition getter.
	 * 
	 * @return The RSL condition inside this restriction.
	 */
	public Condition getRSLCondition() {
		return rslCondition;
	}
	
	/**
	 * Create RSL restriction from RSL string.
	 * 
	 * @param rslString RSL condition
	 */
	public RSLRestriction(String rslString) {
		this.rslString = rslString;
		try {
			this.rslCondition = ParserWrapper.parseString(rslString);
		} catch (ParseException e) {
			/* We throw IllegalArgumentException just to be safe. We assume the caller
			 * has checked the RSL correctness before calling the constructor. 
			 */ 
			throw new IllegalArgumentException("Error in the RSL string.", e);
		}
	}
	
	/**
	 * Create RSL restriction based on an RSL condition.

	 * @param condition RSL condition instance.
	 */
	public RSLRestriction(Condition condition) {
		this.rslString = condition.toString();
		this.rslCondition = condition;
	}
	
	/**
	 * Create new instance from XML data.
	 * 
	 * @param node Node containing RSL restriction data.
	 * 
	 * @throws InputParseException If an error occurred while parsing node data.
	 */
	public RSLRestriction(Node node) throws InputParseException {
		parseXMLNode(node);
	}
	
	/**
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	public Element exportAsElement(Document document) {
		Element result = document.createElement(XML_NODE_NAME);
		result.appendChild(document.createTextNode(rslString));
		return result;
	}

	/**
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	public String getXMLNodeName() {
		return XML_NODE_NAME;
	}

	/**
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	public void parseXMLNode(Node node) throws InputParseException {
		if (!node.getNodeName().equals(XML_NODE_NAME)) {
			throw new InputParseException("Invalid node name: \""
				+ node.getNodeName() + "\".");
		}
		NodeList children = node.getChildNodes();
		if (children.getLength() != 1 || children.item(0).getNodeType() != Node.TEXT_NODE) {
			throw new InputParseException("Node " + XML_NODE_NAME
				+ " des not contain RSL expression.");
		}
		
		rslString = children.item(0).getNodeValue();
		try {
			rslCondition = ParserWrapper.parseString(rslString);
		} catch (cz.cuni.mff.been.common.rsl.ParseException e) {
			throw new InputParseException("Error parsing RSL expression: "
				+ e.getMessage());
		}
	}
	
	/**
	 * Test if given object satisfies conditions specified in this RSLRestriction.
	 * 
	 * @param object Object to test.
	 * @param ignoreMissing If set to <code>true</code>, missing property or
	 *         object will not cause <code>ValueNotFoundException</code> to be
	 *         thrown, instead, object will not be accepted (method will return
	 *         <code>false</code>). If <code>false</code>, missing objects or
	 *         properties will throw an exception.
	 * 
	 * @return <code>true</code> if object satisfies criteria specified;
	 *          <code>false</code> otherwise.
	 * 
	 * @throws ValueNotFoundException if some object or property specified in the
	 *          condition does not exist
	 * @throws ValueTypeIncorrectException if type of the value in the condition
	 *          is not compatible with value type of the property.
	 */
	public boolean test(PropertyTreeReadInterface object, boolean ignoreMissing) {
		ContainerProperty context = new HostManagerContainerProperty(object);
		try {
			rslCondition.check(context);
		} catch (InvalidPropertyException e) {
			throw new ValueNotFoundException(e.getMessage(), e);
		} catch (InvalidOperatorException e) {
			throw new ValueTypeIncorrectException(e.getMessage(), e);
		} catch (InvalidValueTypeException e) {
			throw new ValueTypeIncorrectException(e.getMessage(), e);
		} catch (InvalidValueUnitException e) {
			throw new ValueTypeIncorrectException(e.getMessage(), e);
		}
		return rslCondition.evaluate(context);
	}
}
