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
 * Class representing a conjunction of several RSL conditions.
 * 
 * @author David Majda
 */
public class AndCondition extends Condition {

	private static final long	serialVersionUID	= 6270145786632729852L;

	/** Subconditions. */
	private Condition[] subConditions;

	/**
	 * Allocates a new <code>AndCondition</code> object with specified
	 * subconditions.
	 * 
	 * @param subConditions subconditions
	 */
	public AndCondition(Condition[] subConditions) {
		super();
		this.subConditions = subConditions;
	}

	/** @return subconditions */
	public Condition[] getSubConditions() {
		return subConditions;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		boolean first = true;
		String result = "(";
		for (Condition c : subConditions) {
			if (!first) {
				result += ") && (";
			}
			result += c.toString();
			first = false;
		}
		result += ")";
		return result;
	}

	/** 
	 * Compares this <code>AndCondition</code> to the specified object. The result is
	 * <code>true</code> if and only if the argument is not <code>null</code> and
	 * is an <code>AndCondition</code> object that contains conditions equal to
	 * conditions contained in this object.
	 * 
	 * @throws ClassCastException if the argument is not an <code>AndCondition</code>.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AndCondition)) {
			return false;
		}
		AndCondition otherConditinon = (AndCondition) o;
		
		if (subConditions.length != otherConditinon.subConditions.length) {
			return false;
		}
		
		for (int i = 0; i < subConditions.length; i++) {
			if (!subConditions[i].equals(otherConditinon.subConditions[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a hash code value for this object.
	 * 
	 * The hash code is computed by xoring the hash codes of the subConditions.  
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
		int result = 0;
		for (Condition c: subConditions) {
			result ^= c.hashCode();
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
	 *
	 * @see cz.cuni.mff.been.common.rsl.Condition#check(cz.cuni.mff.been.common.rsl.ContainerProperty)
	 */
	@Override
	public void check(ContainerProperty context)
			throws InvalidPropertyException, InvalidOperatorException,
			InvalidValueTypeException, InvalidValueUnitException {
		for (Condition c: subConditions) {
			c.check(context);
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
		/* Note we support boolean short-circuit evaluation. */
		for (Condition c: subConditions) {
			if (!c.evaluate(context)) {
				return false;
			}
		}
		return true;
	}
}
