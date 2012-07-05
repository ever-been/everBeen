/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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
package cz.cuni.mff.been.resultsrepositoryng.condition;

import java.io.Serializable;

import cz.cuni.mff.been.resultsrepositoryng.condition.CompareCondition.ComparisonType;

/**
 * Factory to create all types of conditions that RR understands. 
 * @author Jan Tattermusch
 *
 */
public class Restrictions {
	
    /**
     * Creates a disjunction
     * @return disjunction condition
     */
	public static Disjunction disjunction() {
		return new Disjunction();
	}
	
	/**
	 * Creates a conjunction
	 * @return conjunction condition
	 */
	public static Conjunction conjunction() {
		return new Conjunction();
	}
	
	/**
	 * Creates a new equal conditon 
	 * @param propertyName property name 
	 * @param value value to match
	 * @return the condition
	 */
	public static Condition eq(String propertyName, Serializable value) {
		return new EqualsCondition(propertyName, value, false);
	}
	
	/**
     * Creates a new not-equal conditon 
     * @param propertyName property name 
     * @param value value to not match
     * @return the condition
     */
	public static Condition ne(String propertyName, Serializable value) {
		return new EqualsCondition(propertyName, value, true);
	}
	
	/**
     * Creates a new greater than or equal condition 
     * @param propertyName property name 
     * @param value value value
     * @return the condition
     */
	public static Condition ge(String propertyName, Serializable value) {
		return new CompareCondition(propertyName, value, ComparisonType.GREATER_OR_EQUAL);
	}
	
	/**
     * Creates a new greater than condition 
     * @param propertyName property name 
     * @param value value value
     * @return the condition
     */
	public static Condition gt(String propertyName, Serializable value) {
		return new CompareCondition(propertyName, value, ComparisonType.GREATER_THAN);
	}
	
	/**
     * Creates a new less than or equal condition 
     * @param propertyName property name 
     * @param value value value
     * @return the condition
     */
	public static Condition le(String propertyName, Serializable value) {
		return new CompareCondition(propertyName, value, ComparisonType.LESS_OR_EQUAL);
	}
	
	/**
     * Creates a new less than condition 
     * @param propertyName property name 
     * @param value value value
     * @return the condition
     */
	public static Condition lt(String propertyName, Serializable value) {
		return new CompareCondition(propertyName, value, ComparisonType.LESS_THAN);
	}
	
	/**
	 * Creates condition that is always true
	 * @return the condition
	 */
	public static Condition alwaysTrue() {
		return new AlwaysTrueCondition();
	}
	
	/**
	 * Creates a new is-null condition 
	 * @param propertyName property name
	 * @return the condition
	 */
	public static Condition isNull(String propertyName) {
		return new IsNullCondition(propertyName, false);
	}
	
	/**
     * Creates a new is-not-null condition 
     * @param propertyName property name
     * @return the condition
     */
	public static Condition isNotNull(String propertyName) {
		return new IsNullCondition(propertyName, true);
	}

}
