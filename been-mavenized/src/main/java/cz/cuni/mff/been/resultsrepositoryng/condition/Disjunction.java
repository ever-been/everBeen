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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import cz.cuni.mff.been.jaxb.AbstractSerializable;
import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.td.Logical;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * Disjunction of simpler conditions
 * @author Jan Tattermusch
 *
 */
public class Disjunction extends Logical implements GroupCondition {
	
	private static final long serialVersionUID = 5797978938727100249L;
	
	/** This binds the instance to the JAXB tree and makes marshalling possible. */
	private JAXBElement< Logical > element;
	
	private List<Condition> conditions = new ArrayList<Condition> ();
	
	/**
	 * To construct object, use Restrictions factory class
	 */
	public Disjunction() {
		
	}

	@Override
	public Disjunction add(Condition condition) {
		conditions.add(condition);
		return this;
	}

	@Override
	public boolean evaluate(DataHandleTuple data) {
		boolean result = false;
		for (Condition c : conditions) {
			if (c.evaluate(data)) result = true;
		}
		return result;
	}

	@Override
	public Criterion toHibernateCriterion() {
		org.hibernate.criterion.Disjunction disj = Restrictions.disjunction();
		for (Condition c : conditions) {
			disj.add( c.toHibernateCriterion() );
		}
		return disj;
	}

	@Override
	public synchronized JAXBElement< Logical > buildJAXBStructure( boolean binary )
	throws IOException {
		List< JAXBElement< ? extends AbstractSerializable > > localOperand;

		localOperand = getOperand();																// To make sure it's not null.
		if ( conditions.size() != localOperand.size() ) {											// Removal not allowed, so OK.
			localOperand.clear();
			for ( Condition condition : conditions ) {
				localOperand.add( condition.buildJAXBStructure( binary ) );
			}
		}
		if ( null == element ) {
			element = Factory.TD.createOr( this );													// This gives the element its name.
		}
		return element;
	}

}
