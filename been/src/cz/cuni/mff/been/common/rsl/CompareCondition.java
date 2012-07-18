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

/**
 * Class representing conditions in RSL with comparison operators. These
 * conditions are in form:
 * 
 *   propertyPath operator value
 * 
 * where operator is one of: "<", "<=", ">", ">=". Property class must
 * implement the <code>Comparable</code> interface.
 * 
 * Class is parametrized by the type of restricted value.
 * 
 * @param <T> type of restricted value
 * @author David Majda
 */
public abstract class CompareCondition<T> extends SimpleCondition<T> {

	private static final long	serialVersionUID	= 4690590129970584192L;

	/**
	 * Allocates a new <code>LessThanCondition</code> object.
	 * 
	 * @param propertyPath property path
	 * @param value value to compare
	 */
	public CompareCondition(String propertyPath, T value) {
		super(propertyPath, value);
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
		super.check(context);
		
		/* Check if evaluated property implements interface Comparable. */
		checkIsOrImplementsInterface(context, Comparable.class, true);
	}	
}
