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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import cz.cuni.mff.been.common.Version;
import cz.cuni.mff.been.softwarerepository.PackageType;

/**
 * Base abstract class for representation of various RSL conditions and their
 * evaluation. All classes which represent RSL conditions inherit from this
 * class.
 * 
 * @author David Majda
 */
public abstract class Condition implements Serializable {

	private static final long	serialVersionUID	= 3318663340447891846L;

	/**
	 * Helper method, which returns an <code>LongWithUnit</code>,
	 * <code>Version</code>, <code>Date</code>, <code>PackageType</code>,
	 * <code>Pattern</code> or <code>String</code>, passed as a parameter, written
	 * in RSL syntax. If some different type of object is passed to the method,
	 * <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param o object to write in RSL syntax - instance of
	 *         <code>LongWithUnit</code>, <code>Version</code>,
	 *         <code>Date</code>, <code>PackageType</code>, <code>Pattern</code> 
	 *         or <code>String</code>
	 * @return object written in RSL syntax
	 * @throws IllegalArgumentException if object passed as a parameter isn't
	 *          instance of <code>LongWithUnit</code>, <code>Version</code>,
	 *          <code>Date</code>, <code>PackageType</code>, <code>Pattern</code>
	 *          nor <code>String</code> 
	 */
	protected static String toRSL(Object o) {
		if (o instanceof LongWithUnit) {
			return o.toString();
		} else if (o instanceof Version) {
			return o.toString();
		} else if (o instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SZ");
			return sdf.format((Date) o);
		} else if (o instanceof String) {
			return "\"" + o.toString().replaceAll("\"", "\\\\\"") + "\"";
		} else if (o instanceof PackageType) {
			return o.toString();
		} else if (o instanceof Pattern) {
			return "/" + o.toString().replaceAll("/", "\\\\/") + "/";
		} else {
			throw new IllegalArgumentException("Class \""
					+ o.getClass().getName() 
					+ "\" is not supported by RSL.");
		}
	}
	
	/**
	 * Makes child property path from the elements of the parent property path. 
	 * 
	 * @param pathElements elements of the parant property path
	 * @return child proeprty path
	 */
	private String makeChildPropertyPath(String[] pathElements) {
		String result = "";
		for (int i = 1; i < pathElements.length; i++) {
			if (i != 1) {
				result += ".";
			}
			result += pathElements[i];
		}
		return result;
	}
	
	/**
	 * Takes a RSL property path, walks the property tree given in
	 * <code>context</code> parameter and returns a list of all simple properties
	 * which could be represented by this path.
	 * 
	 * Non existant properties on the path don't make the function fail - they
	 * only cut the search in given branch of the property tree.  
	 * 
	 * @param propertyPath property path
	 * @param context root of the property tree to search
	 * @return list of all simple properties which could be represented by this
	 *          path
	 */
	protected List<SimpleProperty> pathToProperties(String propertyPath,
			ContainerProperty context) {
		String[] pathElements = propertyPath.split("\\.");
		assert pathElements.length > 0: "Invalid propertyPath.";
		
		if (!context.hasProperty(pathElements[0])) {
			return Collections.emptyList();
		}
		
		Property property = context.getProperty(pathElements[0]);
		List<SimpleProperty> result = new LinkedList<SimpleProperty>();
		if (property instanceof SimpleProperty) {
			result.add((SimpleProperty) property);
		} else  if (property instanceof ContainerProperty) {
			String childPropertyPath = makeChildPropertyPath(pathElements);
			result.addAll(pathToProperties(childPropertyPath, (ContainerProperty) property));
		} else if (property instanceof ArrayProperty) {
			String childPropertyPath = makeChildPropertyPath(pathElements);
			for (ContainerProperty p: ((ArrayProperty) property).getItems()) {
				result.addAll(pathToProperties(childPropertyPath, p));
			}
		} else {
			assert false: "Invalid property class.";
		}
		return result;
	}
	
	/**
	 * Checks if the represented RSL condition is semantically correct within
	 * given context.
	 * 
	 * @param context root of the property tree, which defines the context
	 * @throws InvalidPropertyException if the proeprty doesn't exist
	 * @throws InvalidOperatorException if the operator is not compatible with
	 *          the property type
	 * @throws InvalidValueTypeException if the value is not compatible with
	 *          the property type
	 * @throws InvalidValueUnitException if the unit is not compatible with
	 *          the property unit
	 */
	public abstract void check(ContainerProperty context)
		throws InvalidPropertyException, InvalidOperatorException,
			InvalidValueTypeException, InvalidValueUnitException;
	
	/**
	 * Evaluates represented RSL condition within given context.
	 * 
	 * @param context root of the property tree, which defines the context
	 * @return <code>true</code> if the represented RSL condition holds in
	 *          given context;
	 *          <code>false</code> otherwise
	 */
	public abstract boolean evaluate(ContainerProperty context);

	@Override
	public abstract String toString();
	
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract int hashCode();
}
