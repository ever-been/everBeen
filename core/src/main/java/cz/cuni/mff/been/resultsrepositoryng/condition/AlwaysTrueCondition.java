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
import cz.cuni.mff.been.jaxb.td.True;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * Condition that always evaluates as true
 * @author Jan Tattermusch
 */
public class AlwaysTrueCondition extends True implements Condition {

	private static final long serialVersionUID = 5127411769255513704L;
	
	/** This binds the instance to the JAXB tree and makes marshalling possible. */
	private JAXBElement< True > element;
	
	@Override
	public boolean evaluate(DataHandleTuple data) {
		return true;
	}

	@Override
	public Criterion toHibernateCriterion() {
		return Restrictions.conjunction();
	}

	@Override
	public synchronized JAXBElement< True > buildJAXBStructure( boolean binary ) {
		if ( null == element ) {
			element = Factory.TD.createTrue( this );
		}
		return element;
	}
	
}
