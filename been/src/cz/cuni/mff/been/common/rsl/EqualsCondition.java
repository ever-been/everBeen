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
 * Class representing a RSL condition with "==" operator.
 * 
 * Class is parametrized by the type of values contained in the set.
 * 
 * @param <T> type of values contained in the set
 * @author David Majda
 */
public class EqualsCondition<T> extends SimpleCondition<T> {

	private static final long	serialVersionUID	= 7707151179590153863L;

	/**
	 * Allocates a new <code>EqualsCondition</code> object.
	 * 
	 * @param propertyPath property path
	 * @param value value to compare
	 */
	public EqualsCondition(String propertyPath, T value) {
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
		return propertyPath + " == " + toRSL(value);
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
			if (value.equals(p.getValue())) {
				return true;
			}
		}
		return false;
	}
}
