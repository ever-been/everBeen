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

import cz.cuni.mff.been.jaxb.AbstractSerializable;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * Superclass for all conditions used by results repository
 * 
 * @author Jan Tattermusch
 *
 */
public interface Condition extends Serializable {
	
	/**
	 * Evaluates condition on given data handle tuple
	 * @param data data handle tuple context
	 * @return true if condition is satisfied
	 */
	public abstract boolean evaluate(DataHandleTuple data);
	
	/**
	 * Converts condition to equivalent Hibernate criterion
	 * @return equivalent hibernate criterion
	 */
	public abstract Criterion toHibernateCriterion();

	/**
	 * Builds (possibly recursively) the JAXB tree structure on demand to facilitate
	 * conversion to XML.
	 * 
	 * @param binary Whether to output objects as toString() or as base64 serialization.
	 * @throws IOException When serialization fails.
	 */
	public abstract JAXBElement< ? extends AbstractSerializable > buildJAXBStructure(
		boolean binary
	) throws IOException;
	
}
