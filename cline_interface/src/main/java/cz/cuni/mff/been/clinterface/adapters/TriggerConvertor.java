/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.clinterface.adapters;

import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.td.Trigger;
import cz.cuni.mff.been.resultsrepositoryng.RRTrigger;

/**
 * A bunch of static methods for data structure conversion. 
 * 
 * @author Andrej Podzimek
 */
final class TriggerConvertor {

	/**
	 * Don't do this.
	 */
	private TriggerConvertor() {
	}

	/**
	 * Converts a Trigger (based on JAXB) to a RRTrigger instance used by the Results Repository.
	 * 
	 * @param trigger The trigger representation obtained from XML.
	 * @return A trigger instance the Results Repository uses.
	 * @throws ModuleSpecificException On XML integrity and serialization problems.
	 */
	static RRTrigger triggerToRRTrigger( Trigger trigger )
	throws ModuleSpecificException {
		return new RRTrigger(
			trigger.getAnalysis(),
			trigger.getDataset(),
			trigger.getEvaluator(),
			ConditionConvertor.conditionToCondition( trigger.getCondition() ),
			trigger.getTaskDescriptor()
		);		
	}

	/**
	 * Converts a RRTrigger instance used by the Results Repository to a Trigger (based on JAXB).
	 * 
	 * @param rrTrigger The trigger representation used by the Results Repository.
	 * @param binary Whether to use toString() or base64 serialization for objects.
	 * @return A trigger instance the XML marshaller uses.
	 * @throws ModuleSpecificException On XML integrity and serialization problems.
	 */
	static Trigger rrTriggerToTrigger( RRTrigger rrTrigger, boolean binary )
	throws ModuleSpecificException {
		Trigger result;
		
		result = Factory.TD.createTrigger();
		result.setAnalysis( rrTrigger.getAnalysis() );
		result.setDataset( rrTrigger.getDataset() );
		result.setEvaluator( rrTrigger.getEvaluator() );
		result.setUUID( rrTrigger.getId().toString() );
		result.setCondition(
			ConditionConvertor.conditionToCondition( rrTrigger.getCondition(), binary )
		);
		result.setTaskDescriptor( rrTrigger.getTriggeredTask() );
		return result;
	}
}
