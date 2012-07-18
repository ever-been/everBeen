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
 * Equals Condition 
 * @author Jan Tattermusch
 *
 */
class EqualsCondition extends Compare implements Condition {

	/** This binds the instance to the JAXB tree and makes marshalling possible. */
	private JAXBElement< Compare > element;
	
	/** Whether the current {@code element} contains a binary representation of this object. */
	private boolean binary;
	
	private Serializable value;
	
	private boolean notEqual;
	
	/**
	 * Constructs new equals conditions
	 * 
	 * To construct object, use Restrictions factory method.
	 * 
	 * @param propertyName property name to check
	 * @param value value to compare with
	 * @param notEqual if true, comparison result is negated (results in not-equal)
	 */
	public EqualsCondition(String propertyName, Serializable value, boolean notEqual) {
		if (value == null) {
			throw new IllegalArgumentException("Cannot create equals condition that compares to null value.");
		}
		
		setProperty(propertyName);
		this.value = value;
		this.notEqual = notEqual;
		
		
	}
	
	private static final long serialVersionUID = 3317014813320460061L;

	@Override
	public boolean evaluate(DataHandleTuple data) {
		DataHandle d = data.get(getProperty());
		Class<?> javaType = d.getType().getJavaType();
		boolean serialized = d.getType().isPersistSerialized();
		if (!serialized) {
			try {
				return value.equals( d.getValue(javaType) );
			} catch (DataHandleException e) {
				throw new RuntimeException("Failed to evaluate condition.");
			}
		} else {
			throw new IllegalArgumentException("DataHandle of type " + d.getType().toString() + " forbidden in condition (datahandle type is persisted in serialized form).");
		}
	}

	@Override
	public Criterion toHibernateCriterion() {
		if (notEqual) {
			return Restrictions.ne(getProperty(), value);
		} else {
			return Restrictions.eq(getProperty(), value);
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
			element = notEqual ? Factory.TD.createNe( this ) : Factory.TD.createEq( this );
			this.binary = binary;
		}
		return element;
	}

}
