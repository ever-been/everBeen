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

import java.util.List;

/**
 * Class representing simple conditions in RSL, which are in form:
 * 
 *   propertyPath operator value
 * 
 * Class is parametrized by the type of restricted value.
 * 
 * @param <T> type of restricted value
 * @author David Majda
 */
public abstract class SimpleCondition< T > extends Condition {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6874471086870780404L;

	/** Property path. */
	protected String propertyPath;
	
	/** Value to compare. */
	protected T value;

	/**
	 * Allocates a new <code>SimpleCondition</code> object.
	 * 
	 * @param propertyPath property path
	 * @param value value to compare
	 */
	public SimpleCondition(String propertyPath, T value) {
		this.propertyPath = propertyPath;
		this.value = value;
	}	

	/** @return property path */
	public String getPropertyPath() {
		return propertyPath;
	}

	/** @return value to compare */
	public T getValue() {
		return value;
	}

	/** 
	 * Compares this <code>SimpleCondition</code> to the specified object. The result is
	 * <code>true</code> if and only if the argument is not <code>null</code>,
	 * it is of the came class as this object and contains property path and value
	 * equal to those contained in this object.
	 * 
	 * @throws ClassCastException if the argument is not an <code>SimpleCondition</code>.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return
			getClass().equals(o.getClass())
			&& propertyPath.equals(((SimpleCondition< ? >) o).propertyPath)
			&& value.equals(((SimpleCondition< ? >) o).value);
	}

	/**
	 * Returns a hash code value for this object.
	 * 
	 * The hash code is computed by xoring the hash codes of the vlaue and
	 * property path.  
	 * 
	 * Hash code computed by this algorithm maintains the general contract
	 * for the <code>hashCode</code> method, which states that equal objects
	 * must have equal hash codes.
	 *
	 * @return a hash code value for this object
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return propertyPath.hashCode() ^ value.hashCode();
	}

	/**
	 * Checks if evaluated property exists in the context. Throws
	 * <code>InvalidPropertyException</code> if it doesn't. 
	 * 
	 * @param context root of the property tree, which defines the context
	 * @throws InvalidPropertyException if the proeprty doesn't exist
	 */
	protected void checkPropertyExists(ContainerProperty context) throws InvalidPropertyException {
		if (pathToProperties(propertyPath, context).isEmpty()) {
			throw new InvalidPropertyException("Property \"" + propertyPath
				+ "\" doesn't exist.");
		}
	}	

	/**
	 * Checks if evaluated property is or implements given interface. Throws
	 * <code>InvalidOperatorException</code> if it isn't or doesn't. This logic
	 * can be inverted using the <code>shouldImplement</code> parameter.
	 * 
	 * @param context root of the property tree, which defines the context
	 * @param checkedInterface interface to check
	 * @param shouldImplement if <code>true</code>, property should implement
	 *                         given interface;
	 *                         otherwise it shouldn't
	 * @throws InvalidOperatorException if property isn't or doesn't implement
	 *                                   given interface; can be inverted using
	 *                                   the <code>shouldImplement</code>
	 *                                   parameter
	 */
	protected void checkIsOrImplementsInterface(ContainerProperty context,
			Class< ? > checkedInterface, boolean shouldImplement)
			throws InvalidOperatorException {
		
		for (SimpleProperty p: pathToProperties(propertyPath, context)) {
			Class< ? > propertyClass = p.getValueClass();
			boolean isInterface = propertyClass.equals(checkedInterface); 
			boolean implementsInterface = false;
			if (!isInterface) {
				for (Class< ? > c : propertyClass.getInterfaces()) {
					if (c.equals(checkedInterface)) {
						implementsInterface = true;
						break;
					}
				}
			}
			if (shouldImplement) {
				if (!isInterface && !implementsInterface) {
					throw new InvalidOperatorException("Invalid operator applied to property \""
							+ propertyPath + "\" as it's class \"" + propertyClass.getName()
							+ "\" doesn't implement interface \"" + checkedInterface.getName()
							+ "\".");
				}
			} else {
				if (isInterface || implementsInterface) {
					throw new InvalidOperatorException("Invalid operator applied to property \""
							+ propertyPath + "\" as it's class \"" + propertyClass.getName()
							+ "\" implements interface \"" + checkedInterface.getName()
							+ "\".");
				}
			}
		}
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
	 *
	 * @see cz.cuni.mff.been.common.rsl.Condition#check(cz.cuni.mff.been.common.rsl.ContainerProperty)
	 */
	@Override
	public void check(ContainerProperty context)
			throws InvalidPropertyException, InvalidOperatorException,
			InvalidValueTypeException, InvalidValueUnitException {
		/* Check if evaluated property exists in the context. */
		checkPropertyExists(context);
				
		/* Check if evaluated property is or implements interface List. */
		checkIsOrImplementsInterface(context, List.class, false);

		/* Check if the evaluated properties have same class as the value. */
		for (SimpleProperty p: pathToProperties(propertyPath, context)) {
			Class< ? > propertyClass = p.getValueClass();
			if (!value.getClass().equals(propertyClass)) {
				throw new InvalidValueTypeException("Invalid value type specified: "
						+ "Property \"" + propertyPath + "\" is \"" + propertyClass.getName()
						+ "\", but the value is \"" + value.getClass().getName() + "\".");
			}
		}
		
		/* Special case: When working with LongWithUnit properties, we must check
		 * units. Specifically, if the property specifies some unit, value can't
		 * specify different unit (but can specify no unit), and if the property
		 * doesn't specify any unit, value can't either.
		 */
		if (value.getClass().equals(LongWithUnit.class)) {
			for (SimpleProperty p: pathToProperties(propertyPath, context)) {
				String propertyUnitName = ((LongWithUnit) p.getValue()).getUnitName();
				String valueUnitName = ((LongWithUnit) value).getUnitName();
				if (propertyUnitName != null) {
					if (valueUnitName != null) {
						if (!valueUnitName.equals(propertyUnitName)) {
							throw new InvalidValueUnitException("Invalid unit specified: "
								+ "Property \"" + propertyPath + "\" has unit \"" + propertyUnitName
								+ "\", but the value has unit \"" + valueUnitName + "\".");
						}
					}
				} else {
					if (valueUnitName != null) {
						throw new InvalidValueUnitException("Invalid unit specified: "
							+ "Property \"" + propertyPath + "\" does not have any unit, but "
							+ "the value has unit \"" + valueUnitName + "\".");
					}
				}
			}
		}
	}
}
