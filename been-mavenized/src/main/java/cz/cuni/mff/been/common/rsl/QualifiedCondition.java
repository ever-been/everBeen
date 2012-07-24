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
 * @author David Majda
 */
public class QualifiedCondition extends Condition {

	private static final long	serialVersionUID	= -3041006246385523671L;

	private String qualifier;
	private Condition subCondition;

	public QualifiedCondition(String qualifier, Condition subCondition) {
		super();
		this.qualifier = qualifier;
		this.subCondition = subCondition;
	}

	public String getQualifier() {
		return qualifier;
	}
	
	public Condition getSubCondition() {
		return subCondition;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return qualifier + " { " + subCondition.toString() + " }";
	}

	/** 
	 * Compares this <code>QualifiedCondition</code> to the specified object. The result is
	 * <code>true</code> if and only if the argument is not <code>null</code> and
	 * is an <code>QualifiedCondition</code> object with qualifier and subcondition equal to
	 * qualifier and subcondition in this object.
	 * 
	 * @throws ClassCastException if the argument is not an <code>OrCondition</code>.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof QualifiedCondition)) {
			return false;
		}
		QualifiedCondition otherCondition = (QualifiedCondition) o;
		
		return qualifier.equals(otherCondition.qualifier)
			&& subCondition.equals(otherCondition.subCondition);
	}

	/**
	 * Returns a hash code value for this object.
	 * 
	 * The hash code is computed by xoring the hash codes of the qualifier
	 * and subcondition.  
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
		return qualifier.hashCode() ^ subCondition.hashCode();
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
		if (!context.hasProperty(qualifier)) {
			throw new InvalidPropertyException("Property \"" + qualifier
				+ "\" doesn't exist.");
		}
		
		Property property = context.getProperty(qualifier);
		if (!(property instanceof ContainerProperty)
				&& !(property instanceof ArrayProperty)) {
			throw new InvalidPropertyException("Property \"" + qualifier
				+ "\" is not container or array property.");
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
		Property property = context.getProperty(qualifier);
		if (property instanceof ContainerProperty) {
			return subCondition.evaluate((ContainerProperty) property);
		} else if (property instanceof ArrayProperty) {
			for (ContainerProperty p: ((ArrayProperty) property).getItems()) {
				if (subCondition.evaluate(p)) {
					return true;
				}
			}
			return false;
		} else {
			assert false: "Qualifier must be ContainerProperty or ArrayProperty";
			return false;
		}
	}
}
