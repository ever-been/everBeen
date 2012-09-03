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

import javax.xml.bind.JAXBElement;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.td.NullCompare;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * is-null condition 
 * @author Jan Tattermusch
 *
 */
class IsNullCondition extends NullCompare implements Condition {

	/** This binds the instance to the JAXB tree and makes marshalling possible. */
	private JAXBElement< NullCompare > element;
	
	/**
	 * Constructs new is-null condition
	 * 
	 * To construct object, use Restrictions factory method.
	 * 
	 * @param propertyName property name to check
	 * @param notNull if true, is-null result is negated (results in is-not-null)
	 */
	public IsNullCondition(String propertyName, boolean notNull) {
		setProperty(propertyName);
		setValue(!notNull);
	}
	
	private static final long serialVersionUID = 3317014813320460061L;

	@Override
	public boolean evaluate(DataHandleTuple data) {
		DataHandle d = data.get(getProperty());
		Class<?> javaType = d.getType().getJavaType();
		boolean serialized = d.getType().isPersistSerialized();
		if (!serialized) {
			try {
				if (isValue()) {
					return (d.getValue(javaType) == null);
				} else {
					return (d.getValue(javaType) != null);
				}
			} catch (DataHandleException e) {
				throw new RuntimeException("Failed to evaluate condition.");
			}
		} else {
			throw new IllegalArgumentException("DataHandle of type " + d.getType().toString() + " forbidden in condition (datahandle type is persisted in serialized form).");
		}
	}

	@Override
	public Criterion toHibernateCriterion() {
		if (isValue()) {
			return Restrictions.isNull(getProperty());
		} else {
			return Restrictions.isNotNull(getProperty());
		}
	}

	@Override
	public synchronized JAXBElement< NullCompare > buildJAXBStructure( boolean binary ) {
		if ( null == element ) {
			element = Factory.TD.createNull( this );												// This sets element name.
		}
		return element;
	}

}
