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
import java.util.regex.Pattern;

/**
 * Abstract class, which conatins common functionality of conditions woring with
 * regular expressions (<code>MatchesCondition</code> and
 * <code>DoesNotmatchCondition</code>. Currently the only common piece of code
 * is the <code>check</code> method.  
 * 
 * Class is parametrized by the type of value on the right side of the operator,
 * but this is only for consistency with other conditions - in fact only Pattern
 * is allowed (this is checked by the <code>check</code> method). 
 * 
 * @author David Majda
 *
 * @param <T> type of the value
 */
public abstract class PatternCondition<T> extends SimpleCondition<T> {

	private static final long	serialVersionUID	= 7974978422167511531L;

	/**
	 * Allocates a new <code>PatternCondition</code> object.
	 * 
	 * @param propertyPath property path
	 * @param value regular expression to match against
	 */
	public PatternCondition(String propertyPath, T value) {
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
		checkIsOrImplementsInterface(context, List.class, false);

		/* Check if the evaluated property is String. */
		for (SimpleProperty p: pathToProperties(propertyPath, context)) {
			Class< ? > propertyClass = p.getValueClass();
			if (!(propertyClass.equals(String.class))) {
				throw new InvalidOperatorException("Invalid operator applied to property \""
						+ propertyPath + "\" as it's class \"" + propertyClass.getName()
						+ "\" is not \"java.lang.String\".");
			}
		}
		
		/* Check if the value is Pattern. */
		if (!value.getClass().equals(Pattern.class)) {
			throw new InvalidValueTypeException("Invalid value type specified: "
				+ "\"" + value.getClass().getName() + "\".");
		}
	}	
}
