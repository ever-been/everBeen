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
package cz.cuni.mff.been.webinterface.packages;

import cz.cuni.mff.been.softwarerepository.AttributeInfo;
import cz.cuni.mff.been.softwarerepository.PackageMetadata;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * One atomic condition of the global package query.
 * 
 * The whole system is a bit tricky: The whole query
 * (<code>PackageListQueryCallback</code>) is composed of atomic conditions,
 * which are built on the client and then passed through the RMI to the
 * Software Repository. The Software Repository then invokes the
 * <code>PackageListQueryCallback.match</code> method, which invokes
 * <code>Condition.execute</code> as needed.
 * 
 * The system of atomical queries is designed to be as flexible as possible.
 * This (among other things) means that the atomic query must remember,
 * against which package metadata attribute it is executed - it has to know,
 * which getter to call on the <code>PackageMetadata</code> class when matching.
 * 
 * Easiest way how to handle this is use the <code>AttributeInfo</code> class,
 * which contains all necessary attribute information. But this class
 * contains some unserializable fields, so we choose another way: We initialize
 * <code>Condition</code> only with a attribute name
 * (<code>attributeName</code> field) and when the
 * <code>PackageListQueryCallback.match</code> method is run for the first
 * time in the Software Repository context, it calls the <code>prepare</code>
 * method for each atomic query. This method walks through the
 * <code>PackageMetadata.attributeInfo</code> array and finds necessary
 * information from there.  
 * 
 * @author David Majda
 */
public class Condition implements Serializable {

	private static final long	serialVersionUID	= 7962315517565272999L;

	/** Matched attribute name. */
	private String attributeName;
	
	/** Information about matched attribute. */
	private transient AttributeInfo attributeInfo;
	
	/** Operator used for matching. */
	private Operator operator;
	
	/** Value to match the metadata attribute against. */
	private Object value;
    
	/** @return returns the attributeName */
	public String getAttributeName() {
		return attributeName;
	}
    
	/** @return returns the attributeInfo */
	public AttributeInfo getAttributeInfo() {
		return attributeInfo;
	}
	
	/** @return returns the operator */
	public Operator getOperator() {
		return operator;
	}
    
	/** @return Returns the value */
	public Object getValue() {
		return value;
	}
    
	/**
	 * Allocates a new <code>Condition</code> object.
	 * 
	 * @param attributeName matched attribute name
	 * @param operator operator used for matching
	 * @param value value to match the metadata attribute against
	 */
	public Condition(String attributeName, Operator operator,
			Object value) {
		super();
		this.attributeName = attributeName;
		this.operator = operator;
		this.value = value;
	}
    
	/**
	 * Walks through the <code>PackageMetadata.attributeInfo</code>
	 * and extract information about attribute specified in
	 * <code>attributeName</code> field.
	 */
	public void prepare() {
		for (int i = 0; i < PackageMetadata.ATTRIBUTE_INFO.length; i++) {
			if (PackageMetadata.ATTRIBUTE_INFO[i].getName().equals(attributeName)) {
				attributeInfo = PackageMetadata.ATTRIBUTE_INFO[i];
				return;
			}
		}
		assert false: "Metadata attribute \"" + attributeName + "\" not found.";
	}
    
	/**
	 * Executes the atomic query.
	 * 
	 * @param metadata package metadata
	 * @return <code>true</code> if the package metadata attribute is
	 *          in relation with <code>value</code> according to the
	 *          <code>operator</code>;
	 *          <code>false</code> otherwise
	 */
	public boolean execute(PackageMetadata metadata) {
		boolean result = false;
		try {
			result = operator.apply(attributeInfo.getGetter().invoke(metadata, (Object[]) null),
					value);
		} catch (IllegalArgumentException e) {
			assert false: "Should not happen";
		} catch (IllegalAccessException e) {
			assert false: "Should not happen";
		} catch (InvocationTargetException e) {
			assert false: "Should not happen";
		}
		return result;
	}
}
