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

import java.io.IOException;
import java.io.Serializable;

import javax.xml.bind.JAXBElement;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import cz.cuni.mff.been.common.serialize.Serialize;
import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.td.Compare;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * Compare condition 
 * @author Jan Tattermusch
 *
 */
class CompareCondition extends Compare implements Condition {
	
	public enum ComparisonType {
		GREATER_THAN, GREATER_OR_EQUAL,
		LESS_THAN, LESS_OR_EQUAL
	}

	/** This binds the instance to the JAXB tree and makes marshalling possible. */
	private JAXBElement< Compare > element;
	
	/** Whether the current {@code element} contains a binary representation of this object. */
	private boolean binary;
	
	private Serializable value;
	
	private ComparisonType comparisonType;
	
	/**
	 * Constructs new equals conditions
	 * 
	 * To construct object, use Restrictions factory class.
	 * 
	 * @param propertyName property name to check
	 * @param value value to compare with
	 * @param comparisonType type of comparison to do
	 */
	public CompareCondition(String propertyName, Serializable value, ComparisonType comparisonType) {
		if (value == null) {
			throw new IllegalArgumentException("Cannot create compare condition that compares to null value.");
		}
		
		setProperty(propertyName);
		this.value = value;
		this.comparisonType = comparisonType;
	}
	
	
	private static final long serialVersionUID = 3317014813320460061L;

	@SuppressWarnings("unchecked")
    @Override
	public boolean evaluate(DataHandleTuple data) {
		DataHandle d = data.get(getProperty());
		Class<?> javaType = d.getType().getJavaType();
		if (javaType != null) {
			try {
				Object tableValue = d.getValue(javaType);
				if (tableValue == null) return false;
				
				int result = ((Comparable<Object>) value).compareTo(tableValue);
				switch ( comparisonType ) {
					case GREATER_OR_EQUAL:
						return (result <= 0);
					case GREATER_THAN:
						return (result < 0);
					case LESS_OR_EQUAL:
						return (result >= 0);
					case LESS_THAN:
						return (result > 0);
					default:
						throw new RuntimeException("This code should never be reached.");
				}
			} catch (DataHandleException e) {
				throw new RuntimeException("Failed to evaluate condition.");
			}
		} else {
			throw new IllegalArgumentException("DataHandle of type " + d.getType().toString() + " forbidden in condition.");
		}
	}

	@Override
	public Criterion toHibernateCriterion() {
		switch ( comparisonType ) {
			case GREATER_OR_EQUAL:
				return Restrictions.ge(getProperty(), value);
			case GREATER_THAN:
				return Restrictions.gt(getProperty(), value);
			case LESS_OR_EQUAL:
				return Restrictions.le(getProperty(), value);
			case LESS_THAN:
				return Restrictions.lt(getProperty(), value);
			default:
				throw new RuntimeException("This code should never be reached.");
		}
	}

	@Override
	public synchronized JAXBElement< Compare > buildJAXBStructure( boolean binary )
	throws IOException {
		if ( null == element || binary != this.binary ) {			
			if ( binary ) {
				setStrVal( null );
				setBinVal( Serialize.toBase64( value ) );
			} else {
				setBinVal( null );
				setStrVal( Serialize.toString( value ) );
			}
			switch ( comparisonType ) {
				case GREATER_OR_EQUAL:
					element = Factory.TD.createGe( this );
					break;
				case GREATER_THAN:
					element = Factory.TD.createGt( this );
					break;
				case LESS_OR_EQUAL:
					element = Factory.TD.createLe( this );
					break;
				case LESS_THAN:
					element = Factory.TD.createEq( this );
					break;
				default:
					throw new RuntimeException( "This code should never be reached." );				// And CLI crashes. :-D
			}
			this.binary = binary;
		}
		return element;
	}

}
