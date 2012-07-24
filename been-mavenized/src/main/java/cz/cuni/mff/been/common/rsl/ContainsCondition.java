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
 * Class representing a RSL condition with "contains" operator.
 * 
 * Class is parametrized by the type of values contained in the set.
 * 
 * @param <T> type of values contained in the set
 * @author David Majda
 */
public class ContainsCondition<T> extends SimpleCondition<T> {

	private static final long	serialVersionUID	= -6848403911757339710L;

	/**
	 * Allocates a new <code>ContainsCondition</code> object.
	 * 
	 * @param propertyPath property path
	 * @param value value to compare
	 */
	public ContainsCondition(String propertyPath, T value) {
		super(propertyPath, value);
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return propertyPath + " contains " + toRSL(value);
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
	 *
	 * @see cz.cuni.mff.been.common.rsl.Condition#check(cz.cuni.mff.been.common.rsl.ContainerProperty)
	 */
	@Override
	public void check(ContainerProperty context)
			throws InvalidPropertyException, InvalidOperatorException,
			InvalidValueTypeException {
		/* Check if evaluated property exists in the context. */
		checkPropertyExists(context);

		/* Check if evaluated property is or implements interface List. */
		checkIsOrImplementsInterface(context, List.class, true);

		/* Check if the value is String (we support only lists of strings). */
		if (!value.getClass().equals(String.class)) {
			throw new InvalidValueTypeException("Invalid value type specified: "
				+ "Property \"" + propertyPath + "\" is list of Strings, "
				+ "but the value is \"" + value.getClass().getName() + "\".");
		}
	}

	/**
	 * Evaluates represented RSL condition within given context.
	 * 
	 * @param context root of the property tree, which defines the context
	 * @return <code>true</code> if the represented RSL condition holds in
	 *          given context;
	 *          <code>false</code> otherwise
	 *          
	 * @see cz.cuni.mff.been.common.rsl.Condition#evaluate(cz.cuni.mff.been.common.rsl.ContainerProperty)
	 */
	@Override
	public boolean evaluate(ContainerProperty context) {
		for (SimpleProperty p: pathToProperties(propertyPath, context)) {
			if (((List< ? >) p.getValue()).contains(value)) {
				return true;
			}
		}
		return false;
	}
}
